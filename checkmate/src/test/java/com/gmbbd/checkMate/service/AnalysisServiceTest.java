package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.model.EvaluationResult;
import com.gmbbd.checkMate.model.Requirement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

    @Mock
    private ValidationService validationService;

    @Mock
    private ParseService parseService;

    @Mock
    private RequirementService requirementService;

    @Mock
    private LlmService llmService;

    @InjectMocks
    private AnalysisService analysisService;

    @Test
    void evaluate_callsDependencies_andReturnsEvaluationResults() {
        // given
        MultipartFile requirementsFile = mock(MultipartFile.class);
        MultipartFile submissionFile = mock(MultipartFile.class);

        String requirementText = "REQ1: The system shall support dark mode.\n" +
                "REQ2: The system shall store user profiles.";
        String submissionText = "Our system supports dark mode but not user profiles yet.";

        // ParseService가 텍스트를 잘 뽑았다고 가정
        when(parseService.extractText(requirementsFile)).thenReturn(requirementText);
        when(parseService.extractText(submissionFile)).thenReturn(submissionText);

        // RequirementService가 요구사항 2개를 뽑아줬다고 가정
        Requirement r1 = new Requirement(1L, "The system shall support dark mode.");
        Requirement r2 = new Requirement(2L, "The system shall store user profiles.");

        when(requirementService.extractRequirements(requirementText))
                .thenReturn(List.of(r1, r2));

        // LLM 결과도 미리 세팅 (네 EvaluationResult 구조 그대로 사용)
        EvaluationResult er1 = new EvaluationResult(
                null,
                r1.getRawText(),
                "FULFILLED",
                1.0,
                3,
                3,
                "dark mode found",
                "fully implemented"
        );
        EvaluationResult er2 = new EvaluationResult(
                null,
                r2.getRawText(),
                "NOT_FULFILLED",
                0.2,
                0,
                3,
                "no related keywords",
                "not implemented"
        );

        when(llmService.evaluateRequirement(r1.getRawText(), submissionText)).thenReturn(er1);
        when(llmService.evaluateRequirement(r2.getRawText(), submissionText)).thenReturn(er2);

        // when
        List<EvaluationResult> results = analysisService.evaluate(requirementsFile, submissionFile);

        // then
        assertThat(results).hasSize(2);

        // AnalysisService에서 requirementId를 세팅해주는지 확인
        EvaluationResult first = results.get(0);
        EvaluationResult second = results.get(1);

        assertThat(first.getRequirementId()).isEqualTo(1L);
        assertThat(first.getStatus()).isEqualTo("FULFILLED");

        assertThat(second.getRequirementId()).isEqualTo(2L);
        assertThat(second.getStatus()).isEqualTo("NOT_FULFILLED");

        // 의존 서비스들이 제대로 호출됐는지 검증
        verify(validationService, times(1)).validateFile(requirementsFile);
        verify(validationService, times(1)).validateFile(submissionFile);

        verify(parseService, times(1)).extractText(requirementsFile);
        verify(parseService, times(1)).extractText(submissionFile);

        verify(requirementService, times(1)).extractRequirements(requirementText);

        verify(llmService, times(1)).evaluateRequirement(r1.getRawText(), submissionText);
        verify(llmService, times(1)).evaluateRequirement(r2.getRawText(), submissionText);
    }
}