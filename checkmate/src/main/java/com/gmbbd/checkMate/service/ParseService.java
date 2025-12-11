package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.exception.ApiException;
import com.gmbbd.checkMate.util.TextCleaner;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class ParseService {

    private final TextCleaner cleaner;

    public ParseService(TextCleaner cleaner) {
        this.cleaner = cleaner;
    }

    /**
     * MultipartFile → InputStream 기반 파싱 (절대 temp 파일 만들지 않음)
     */
    public String extractText(MultipartFile multipartFile) {
        try {
            String name = multipartFile.getOriginalFilename().toLowerCase();

            InputStream is = multipartFile.getInputStream();

            if (name.endsWith(".pdf")) {
                return cleaner.clean(parsePdf(is));
            } else if (name.endsWith(".docx")) {
                return cleaner.clean(parseDocx(is));
            } else if (name.endsWith(".txt")) {
                return cleaner.clean(parseTxt(is));
            } else {
                throw new ApiException("지원하지 않는 파일 형식입니다. (pdf/docx/txt)");
            }

        } catch (Exception e) {
            throw new ApiException("업로드 파일 처리 중 오류 발생: " + e.getMessage());
        }
    }

    // PDF 파싱 (PDFBox)
    private String parsePdf(InputStream inputStream) throws IOException {
        try (PDDocument pdf = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdf);
        }
    }

    // DOCX 파싱
    private String parseDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(inputStream)) {
            StringBuilder sb = new StringBuilder();
            doc.getParagraphs().forEach(p -> sb.append(p.getText()).append("\n"));
            return sb.toString();
        }
    }

    // TXT 파싱
    private String parseTxt(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
