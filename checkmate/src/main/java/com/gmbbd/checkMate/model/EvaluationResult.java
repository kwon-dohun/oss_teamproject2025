package com.gmbbd.checkMate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * - requirementId        : 어떤 요구사항의 결과인지
 * - requirementText      : 요구사항 원문 텍스트
 * - status               : FULFILLED / PARTIAL / NOT_FULFILLED
 * - score                : 매칭 점수
 * - matchedKeywordCount  : 과제 텍스트에서 실제로 매칭된 키워드 개수
 * - totalKeywordCount    : 평가에 사용된 전체 키워드 개수
 * - evidence             : 상태 판단의 근거(키워드 매칭 설명 또는 LLM 분석 결과)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResult {

    private Long requirementId;

    private String requirementText;

    private String status;

    private double score;

    private int matchedKeywordCount;

    private int totalKeywordCount;

    private String evidence;

    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
