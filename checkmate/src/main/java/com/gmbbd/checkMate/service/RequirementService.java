package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.model.Requirement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// PDF/DOCX 텍스트에서 "요구사항 항목"을 찾아서 Requirement 리스트로 변환
@Service
public class RequirementService {


    // 인식할 요구사항 패턴
    private static final List<Pattern> REQUIREMENT_PATTERNS = List.of(
            // 1. 내용 / 1) 내용 / 1 - 내용 / 1 내용 / 1.1. 내용 / 1.2.3 내용 ...
            Pattern.compile("^\\s*(\\d+(?:\\.\\d+)*)(?:[\\.)-])?\\s+(.+)$"),

            // (1) 내용 / [1] 내용 / (A) 내용 / a) 내용 ...
            Pattern.compile("^\\s*[\\[\\(]?(\\d+|[A-Za-z])[\\]\\)]?\\)?\\s+(.+)$"),

            // • 내용 / ● 내용 / - 내용 / * 내용 (앞에 기호 하나 + 공백)
            Pattern.compile("^\\s*[•●\\-\\*]\\s+(.+)$")
    );

    /**
     * 한 줄에서 요구사항 본문을 추출
     *  - 어떤 패턴에도 맞지 않으면 null 반환
     */
    private String extractRequirementBody(String line) {
        String trimmed = line.trim();
        if (trimmed.isBlank()) {
            return null;
        }

        for (Pattern p : REQUIREMENT_PATTERNS) {
            Matcher m = p.matcher(trimmed);
            if (m.matches()) {
                // "본문"은 항상 마지막 그룹으로 두었으므로 groupCount() 사용
                String body = m.group(m.groupCount());
                if (body != null) {
                    body = body.trim();
                }
                // 너무 짧은 건(예: "-"만 있는 줄)은 필터링
                if (body != null && body.length() >= 3) {
                    return body;
                }
            }
        }

        return null;
    }

    /**
     * 요구사항 텍스트를 줄 단위로 나누고,
     * 번호/불릿 패턴으로 시작하는 줄들을 Requirement 리스트로 변환
     * 11/17 번호 없이 줄글로 입력된 경우 fallback 로직 추가
     */
    public List<Requirement> extractRequirements(String requirementText) {
        List<Requirement> requirements = new ArrayList<>();

        if (requirementText == null || requirementText.isBlank()) {
            return requirements;
        }

        String[] lines = requirementText.split("\\r?\\n");
        long id = 1L;

        // 1차: 번호/불릿 패턴
        for (String line : lines) {
            String body = extractRequirementBody(line);
            if (body == null) {
                continue;
            }
            requirements.add(new Requirement(id++, body));
        }

        // 번호/불릿에서 아무것도 못 찾았으면 줄글 fallback
        if (requirements.isEmpty()) {
            // 2차: 문장 단위로 잘라서 요구사항으로 사용
            // 마침표/물음표/느낌표 + 줄바꿈 기준으로 문장 분리
            String[] sentences = requirementText.split("(?<=[\\.?!。！？])\\s+|\\r?\\n");

            id = 1L;
            for (String s : sentences) {
                String trimmed = s.trim();
                // 너무 짧은 것 제외
                if (trimmed.length() < 10) {
                    continue;
                }
                requirements.add(new Requirement(id++, trimmed));
            }
        }

        return requirements;
    }

}
