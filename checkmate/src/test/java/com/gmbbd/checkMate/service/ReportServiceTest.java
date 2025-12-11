package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.model.EvaluationResult;
import com.gmbbd.checkMate.model.Requirement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private RequirementService requirementService;

    @Mock
    private ParseService parseService;

    @Mock
    private CompareService compareService;

    @Mock
    private AnalysisService analysisService;

    @InjectMocks
    private ReportService reportService;

    @Test
    void generateReport_basic_success() throws Exception {

        MockMultipartFile reqPdf = new MockMultipartFile("requirements", "req.pdf", "application/pdf", "dummy".getBytes());
        MockMultipartFile subPdf = new MockMultipartFile("submission", "sub.pdf", "application/pdf", "dummy".getBytes());

        List<EvaluationResult> mockResults = List.of(
                new EvaluationResult(1L, "REQ1", "FULFILLED", 1.0, 1, 1, null, null),
                new EvaluationResult(2L, "REQ2", "NOT_FULFILLED", 0.0, 0, 1, null, null)
        );

        when(analysisService.evaluate(reqPdf, subPdf))
                .thenReturn(mockResults);

        String report = reportService.generateReport(reqPdf, subPdf);

        assertNotNull(report);
        assertTrue(report.contains("REQ1"));
        assertTrue(report.contains("REQ2"));
    }
}
