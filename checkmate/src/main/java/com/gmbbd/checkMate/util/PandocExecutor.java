package com.gmbbd.checkMate.util;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Component
public class PandocExecutor {

    public String convertToMarkdown(File input) {
        try {
//            // CODE FOR DEBUGGING!!
//            ProcessBuilder check = new ProcessBuilder("pandoc", "--version");
//            Process p1 = check.start();
//            String out = new String(p1.getInputStream().readAllBytes());
//            String err = new String(p1.getErrorStream().readAllBytes());
//
//            System.out.println("=== PANDOC VERSION STDOUT ===");
//            System.out.println(out);
//            System.out.println("=== PANDOC VERSION STDERR ===");
//            System.out.println(err);
//
//            int code = p1.waitFor();
//            System.out.println("=== PANDOC VERSION EXIT CODE: " + code + " ===");

            if (!input.exists() || !input.canRead()) {
                throw new RuntimeException("Pandoc 입력 파일 접근 불가");
            }

            File output = File.createTempFile("pandoc-", ".md");

            // 확장자 기반 format 강제 지정
            String name = input.getName().toLowerCase();
            String format = "markdown";  // default

            if (name.endsWith(".txt")) {
                format = "plain";
            } else if (name.endsWith(".docx")) {
                format = "docx";
            } else if (name.endsWith(".pdf")) {
                // Pandoc은 pdf reader가 없음 → 무조건 실패
                throw new RuntimeException("Pandoc은 PDF 입력을 지원하지 않습니다.");
            }

            ProcessBuilder pb = new ProcessBuilder(
                    "pandoc",
                    input.getAbsolutePath(),
                    "-f", format,
                    "-t", "markdown",
                    "-o", output.getAbsolutePath()
            );

            pb.redirectErrorStream(true);
            Process p = pb.start();
            int exit = p.waitFor();

            if (exit != 0) {
                throw new RuntimeException("Pandoc 변환 실패: exit code = " + exit);
            }

            String result = Files.readString(output.toPath(), StandardCharsets.UTF_8);
            output.delete();
            return result;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Pandoc 실행 오류: " + e.getMessage(), e);
        }
    }
}

