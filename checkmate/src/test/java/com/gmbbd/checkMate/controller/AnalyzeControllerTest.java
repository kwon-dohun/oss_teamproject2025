package com.gmbbd.checkMate.controller;

import com.gmbbd.checkMate.model.EvaluationResult;
import com.gmbbd.checkMate.service.AnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyzeController.class)
class AnalyzeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalysisService analysisService;

    @Test
    void analyze_shouldReturnSummaryResponse() throws Exception {

        List<EvaluationResult> mockResults = List.of(
                new EvaluationResult(1L, "REQ1", "FULFILLED", 1.0, 1, 1, null, null)
        );

        when(analysisService.evaluate(any(), any()))
                .thenReturn(mockResults);

        MockMultipartFile req = new MockMultipartFile("requirements", "req.pdf", "application/pdf", "data".getBytes());
        MockMultipartFile sub = new MockMultipartFile("submission", "sub.pdf", "application/pdf", "data".getBytes());

        mockMvc.perform(multipart("/api/analyze")
                        .file(req)
                        .file(sub))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fulfilled").value(1));
    }
}
