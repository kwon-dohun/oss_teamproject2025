package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.exception.ApiException;
import com.gmbbd.checkMate.util.PandocExecutor;
import com.gmbbd.checkMate.util.TextCleaner;
import lombok.RequiredArgsConstructor;
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

//            System.out.println("AFTER TRANSFER, EXISTS=" + temp.exists() + ", SIZE=" + temp.length());
//
//            // 강제 flush 용으로 재열기
//            byte[] raw = Files.readAllBytes(temp.toPath());
//            System.out.println("AFTER RE-READ, SIZE=" + raw.length);

            // Pandoc: Into Markdown
            String markdown = pandocExecutor.convertToMarkdown(temp);

            temp.delete();

            // Markdown-safe cleaning
            return cleaner.cleanMarkdown(markdown);

        } catch (Exception e) {
            throw new ApiException("업로드 파일 처리 중 오류 발생: " + e.getMessage());
        }
    }
}
