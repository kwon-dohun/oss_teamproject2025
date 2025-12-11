package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.exception.ApiException;
import com.gmbbd.checkMate.util.PandocExecutor;
import com.gmbbd.checkMate.util.TextCleaner;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParseServiceTest {

    private final TextCleaner cleaner = mock(TextCleaner.class);
    private final PandocExecutor pandocExecutor = mock(PandocExecutor.class);
    private final ParseService parseService = new ParseService(pandocExecutor, cleaner);

    @Test
    void extractText_shouldThrowException_whenFileIsNull() {
        assertThrows(ApiException.class, () -> parseService.extractText(null));
    }

    @Test
    void extractText_shouldThrowException_whenUnsupportedFile() {
        MockMultipartFile file =
                new MockMultipartFile("file", "hello.xyz", "text/plain", "hi".getBytes());

        assertThrows(ApiException.class, () -> parseService.extractText(file));
    }

    @Test
    void extractText_txtFile_shouldReturnCleanedText() {

        MockMultipartFile file =
                new MockMultipartFile("file", "sample.txt", "text/plain", "hello world".getBytes());

        when(pandocExecutor.convertToMarkdown(any())).thenReturn(" ### hello world");
        when(cleaner.cleanMarkdown(" ### hello world")).thenReturn("hello world");

        String result = parseService.extractText(file);

        assertEquals("hello world", result);
    }
}
