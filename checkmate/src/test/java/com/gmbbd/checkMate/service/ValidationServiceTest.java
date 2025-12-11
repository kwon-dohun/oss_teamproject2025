package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private final ValidationService validationService = new ValidationService();

    @Test
    void validateFile_shouldThrowException_whenFileIsNull() {
        assertThrows(ApiException.class, () -> validationService.validateFile(null));
    }

    @Test
    void validateFile_shouldThrowException_whenFileIsEmpty() {
        MockMultipartFile emptyFile =
                new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);

        assertThrows(ApiException.class, () -> validationService.validateFile(emptyFile));
    }

    @Test
    void validateFile_shouldPass_whenFileIsValid() {
        MockMultipartFile validFile =
                new MockMultipartFile("file", "test.pdf", "application/pdf", "hello".getBytes());

        assertDoesNotThrow(() -> validationService.validateFile(validFile));
    }
}
