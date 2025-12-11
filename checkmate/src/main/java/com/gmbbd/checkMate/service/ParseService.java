package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.exception.ApiException;
import com.gmbbd.checkMate.util.PandocExecutor;
import com.gmbbd.checkMate.util.TextCleaner;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
public class ParseService {

    private final PandocExecutor pandocExecutor;
    private final TextCleaner cleaner;

    public String extractText(MultipartFile multipartFile) {
        try {
            String name = multipartFile.getOriginalFilename().toLowerCase();

            if (!(name.endsWith(".pdf") || name.endsWith(".docx") || name.endsWith(".txt"))) {
                throw new ApiException("지원하지 않는 파일 형식입니다. (pdf/docx/txt)");
            }

            File temp = File.createTempFile("upload-", name.substring(name.lastIndexOf(".")));
            multipartFile.transferTo(temp);

            String markdown;

            if (name.endsWith(".pdf")) {

                markdown = extractPdf(temp);

            } else {

                markdown = pandocExecutor.convertToMarkdown(temp);

            }

            temp.delete();


            // Markdown-safe cleaning
            return cleaner.cleanMarkdown(markdown);

        } catch (Exception e) {
            throw new ApiException("업로드 파일 처리 중 오류 발생: " + e.getMessage());
        }
    }

    private String extractPdf(File file) {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setStartPage(1);
            stripper.setEndPage(Integer.MAX_VALUE);

            String text = stripper.getText(document);
            return text != null ? text : "";

        } catch (Exception e) {
            throw new ApiException("PDF 파싱 실패: " + e.getMessage());
        }
    }
}
