package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.model.EvaluationResult;
import com.gmbbd.checkMate.model.Requirement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * CompareService
 *  요구사항 텍스트와 과제 텍스트를 키워드 단위로 단순 비교해서 FULFILLED / PARTIAL / NOT_FULFILLED 상태 판정
 *  CompareServiceTest에 맞춰 점수/상태 규칙을 맞춤
 */
@Service
public class CompareService {

    private static final double FULFILLED_THRESHOLD = 0.6;

    public List<EvaluationResult> evaluateByKeywordMatch(List<Requirement> requirements,
                                                         String assignmentText) {

        List<EvaluationResult> results = new ArrayList<>();

        if (requirements == null || requirements.isEmpty()) {
            return results;
        }

        if (assignmentText == null) {
            assignmentText = "";
        }
        String normalizedAssignment = normalize(assignmentText);

        for (Requirement req : requirements) {
            if (req == null) continue;

            EvaluationResult r = compareRequirementInternal(req, normalizedAssignment);
            results.add(r);
        }

        return results;
    }

    /**
     *  AnalysisService용 단일 비교 method
     *  하나의 Requirement와 제출 텍스트를 입력받아 EvaluationResult 1개 반환
     */
    public EvaluationResult compare(Requirement req, String submissionText) {

        if (req == null) {
            return null;
        }

        if (submissionText == null) {
            submissionText = "";
        }

        String normalizedAssignment = normalize(submissionText);

        return compareRequirementInternal(req, normalizedAssignment);
    }

    /**
     * 내부 공통 로직
     * 단일 Requirement에 대해 키워드 기반 비교를 수행하고 EvaluationResult 생성
     */
    private EvaluationResult compareRequirementInternal(Requirement req, String normalizedAssignment) {

        String normalizedReq = normalize(req.getRawText());

        // 요구사항 문장을 토큰(단어) 단위로 분해
        String[] tokens = normalizedReq.split("\\s+");

        int totalKeywords = 0;   // 실제로 의미 있는 키워드 수
        int matched = 0;         // 과제 텍스트에서 발견된 키워드 수

        for (String token : tokens) {
            if (token.isBlank()) continue;
            if (isStopWord(token)) continue; // 조사/접속사 등은 제외

            totalKeywords++;

            if (normalizedAssignment.contains(token)) {
                matched++;
            }
        }

        double score;
        String status;

        if (totalKeywords == 0) {
            status = "NOT_FULFILLED";
            score = 0.0;

        } else {
            double ratio = (double) matched / totalKeywords;

            if (ratio >= FULFILLED_THRESHOLD) {
                status = "FULFILLED";
                score = 1.0;

            } else if (matched > 0) {
                status = "PARTIAL";
                score = ratio;

            } else {
                status = "NOT_FULFILLED";
                score = 0.0;
            }
        }

        // 결과 객체 생성 및 값 저장
        EvaluationResult r = new EvaluationResult();
        r.setRequirementId(req.getId());
        r.setStatus(status);
        r.setScore(score);
        r.setMatchedKeywordCount(matched);
        r.setTotalKeywordCount(totalKeywords);

        return r;
    }

    /**
     * 비교를 위해 텍스트 소문자 + 특수문자 제거 + 공백 정리
     */
    private String normalize(String text) {
        if (text == null) return "";
        return text
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]", " ") // 한글/영어/숫자/공백만 남김
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * 의미 없는 조사/접속사 등을 stop word로 정의
     */
    private boolean isStopWord(String token) {
        return List.of(
                "을", "를", "은", "는", "이", "가",
                "에", "에서", "및", "그리고", "또는",
                "으로", "으로서", "으로써", "와", "과"
        ).contains(token);
    }
}
