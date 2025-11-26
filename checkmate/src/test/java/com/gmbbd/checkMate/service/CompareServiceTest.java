package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.model.EvaluationResult;
import com.gmbbd.checkMate.model.Requirement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CompareService에 대한 단위 테스트.
 *  - 요구사항과 과제 텍스트를 넣었을 때
 *    FULFILLED / PARTIAL / NOT_FULFILLED 판단이
 *    우리가 예상한 대로 나오는지 확인한다.
 */
class CompareServiceTest {

    @Test
    void evaluateByKeywordMatch_fulfilledCase() {
        // given: 요구사항 1개
        Requirement req1 = new Requirement(1L, "시스템 개요를 설명하시오.");
        List<Requirement> requirements = List.of(req1);

        // 과제 텍스트: 요구사항의 단어들이 충분히 들어있는 경우
        String assignmentText = """
                본 보고서에서는 시스템의 전체적인 개요를 충분히 설명하고,
                구성 요소와 동작 방식을 자세히 기술하였다.
                """;

        CompareService service = new CompareService();

        // when
        List<EvaluationResult> results = service.evaluateByKeywordMatch(requirements, assignmentText);

        // then
        assertEquals(1, results.size());
        EvaluationResult r1 = results.get(0);

        assertEquals(1, r1.getRequirementId());
        assertEquals("FULFILLED", r1.getStatus(), "충분히 포함되었으므로 FULFILLED여야 한다.");
        assertTrue(r1.getScore() >= 0.9, "점수는 1.0에 가까워야 한다.");
    }

    @Test
    void evaluateByKeywordMatch_notFulfilledCase() {
        // given: 요구사항 1개
        Requirement req1 = new Requirement(1L, "ERD 다이어그램을 포함하시오.");
        List<Requirement> requirements = List.of(req1);

        // 과제 텍스트: ERD 관련 내용이 전혀 없는 경우
        String assignmentText = """
                이 보고서는 시스템의 성능 측정 결과와 테스트 케이스만을 포함한다.
                데이터베이스 다이어그램에 대한 설명은 포함하지 않았다.
                """;

        CompareService service = new CompareService();

        // when
        List<EvaluationResult> results = service.evaluateByKeywordMatch(requirements, assignmentText);

        // then
        assertEquals(1, results.size());
        EvaluationResult r1 = results.get(0);

        assertEquals("NOT_FULFILLED", r1.getStatus(), "관련 키워드가 없으므로 NOT_FULFILLED여야 한다.");
        assertEquals(0.0, r1.getScore(), 0.0001);
    }

    @Test
    void evaluateByKeywordMatch_partialCase() {
        // given
        Requirement req1 = new Requirement(1L, "요구분석 방법론을 두 가지 이상 기술하시오.");
        List<Requirement> requirements = List.of(req1);

        // 과제 텍스트: 일부 키워드는 있지만, 완전하진 않은 경우
        String assignmentText = """
                이 보고서에서는 요구분석 방법론 중 하나만 간단히 언급하였다.
                다른 방법론에 대해서는 자세히 기술하지 않았다.
                """;

        CompareService service = new CompareService();

        // when
        List<EvaluationResult> results = service.evaluateByKeywordMatch(requirements, assignmentText);

        // then
        EvaluationResult r1 = results.get(0);

        assertEquals("PARTIAL", r1.getStatus(), "일부만 포함되었으므로 PARTIAL이어야 한다고 기대.");
        assertTrue(r1.getScore() > 0.0 && r1.getScore() < 1.0,
                "점수는 0과 1 사이(부분 충족)여야 한다.");
    }
}