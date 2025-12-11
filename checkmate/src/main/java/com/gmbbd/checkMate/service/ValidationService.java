package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.exception.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ValidationService {

    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * MultipartFile 검증
     * 검증 내용:
     *  - null / empty
     *  - 파일 크기 제한
     *  - 확장자(pdf/docx/txt)
     */
    public void validateFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new ApiException("업로드된 파일이 비어있습니다.");
        }

        // 파일명 확보 (없다면 오류)
        String originalName = multipartFile.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new ApiException("파일 이름을 확인할 수 없습니다.");
        }

        // 용량 제한
        if (multipartFile.getSize() > MAX_SIZE) {
            throw new ApiException("파일 용량은 10MB 이하만 지원합니다.");
        }

        // 확장자 검사
        String lower = originalName.toLowerCase();
        if (!(lower.endsWith(".pdf") || lower.endsWith(".docx") || lower.endsWith(".txt"))) {
            throw new ApiException("지원하지 않는 파일 형식입니다. (pdf/docx/txt)");
        }
    }

}
