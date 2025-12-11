package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.model.EvaluationResult;
import com.gmbbd.checkMate.model.Requirement;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompareServiceLogicTest {

    private final CompareService compareService = new CompareService();

    @Test
    void fulfilled_whenMostKeywordsMatch() {
        // given
        // ⚠ Requirement 생성자는 네 클래스에 맞게 수정! (id, rawText, ... 순서)
        Requirement req = new Requirement(1L, "The system shall support dark mode.");
        String submission = "Our system supports dark mode for better usability.";

        // when
        EvaluationResult result = compareService.compare(req, submission);

        // then
        assertThat(result.getStatus()).isEqualTo("FULFILLED");
        assertThat(result.getScore()).isGreaterThanOrEqualTo(0.9);
        assertThat(result.getMatchedKeywordCount()).isPositive();
        assertThat(result.getTotalKeywordCount()).isGreaterThan(0);
    }

    @Test
    void notFulfilled_whenNoKeywordsMatch() {
        // given
        Requirement req = new Requirement(1L, "The system shall support dark mode.");
        String submission = "This document describes only payment processing and billing.";

        // when
        EvaluationResult result = compareService.compare(req, submission);

        // then
        assertThat(result.getStatus()).isEqualTo("NOT_FULFILLED");
        assertThat(result.getScore()).isEqualTo(0.0);
        assertThat(result.getMatchedKeywordCount()).isZero();
    }
}