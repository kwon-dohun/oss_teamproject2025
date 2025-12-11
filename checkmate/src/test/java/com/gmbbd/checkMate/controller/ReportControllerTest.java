package com.gmbbd.checkMate.controller;

import com.gmbbd.checkMate.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    void report_shouldReturnTextFile() throws Exception {

        when(reportService.generateReport(any(), any()))
                .thenReturn("dummy report");

        MockMultipartFile req = new MockMultipartFile("requirements", "req.pdf", "application/pdf", "data".getBytes());
        MockMultipartFile sub = new MockMultipartFile("submission", "sub.pdf", "application/pdf", "data".getBytes());

        mockMvc.perform(multipart("/api/report")
                        .file(req)
                        .file(sub))
                .andExpect(status().isOk())
                .andExpect(content().string("dummy report"));
    }
}
