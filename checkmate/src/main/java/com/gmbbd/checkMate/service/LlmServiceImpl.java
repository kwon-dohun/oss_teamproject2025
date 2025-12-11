package com.gmbbd.checkMate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmbbd.checkMate.model.EvaluationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LlmServiceImpl implements LlmService {

    private final WebClient openAiWebClient;   // OpenAIConfig
    private final ObjectMapper objectMapper;   // 스프링 Bean

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    @Override
    public EvaluationResult evaluateRequirement(String requirementText, String documentText) {
        // 프롬프트 생성
        String prompt = buildPrompt(requirementText, documentText);

        // OpenAI 요청
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "temperature", 0,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        // WebClient 호출
        String rawResponse = openAiWebClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e ->
                        Mono.error(new RuntimeException("OpenAI API 호출 실패: " + e.getMessage(), e))
                )
                .block();

        // JSON 파싱 → EvaluationResult
        return parseEvaluationResult(rawResponse, requirementText, null);
    }

    private String buildPrompt(String req, String submission) {
        String safeReq = (req == null) ? "" : req.trim();
        String safeSubmission = (submission == null) ? "" : submission;

        // 공백 정리
        String normalizedReq = normalizeText(safeReq);
        String normalizedSubmission = normalizeText(safeSubmission);

        try {
            ClassPathResource resource =
                    new ClassPathResource("prompts/llm_prompt.txt");

            try (InputStream is = resource.getInputStream()) {
                String template = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                // 프롬프트의 %s 두 군데에 순서대로 요구사항, 문서내용 채우기
                return template.formatted(normalizedReq, normalizedSubmission);
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "프롬프트 템플릿(prompts/llm_prompt.txt)을 읽는 데 실패했습니다.", e
            );
        }
    }

    /**
     * 공백/개행/탭을 하나의 공백으로 줄이고 양 끝 공백 제거
     * 내용 X, 표현만 정리
     */
    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * OpenAI 응답(JSON 문자열)을 EvaluationResult로 변환
     *
     * JSON 형식:
     * {
     *   "status": "FULFILLED",
     *   "score": 0.85,
     *   "matchedKeywordCount": 5,
     *   "totalKeywordCount": 6,
     *   "evidence": "어떤 문장과 키워드가 일치하는지 설명",
     *   "reason": "최종 판단 근거"
     * }
     */
    private EvaluationResult parseEvaluationResult(String rawResponse,
                                                   String requirementText,
                                                   Long requirementId) {
        if (rawResponse == null || rawResponse.isBlank()) {
            throw new IllegalStateException("OpenAI 응답이 비어 있습니다.");
        }

        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            // ChatCompletion 구조에서 content 추출
            JsonNode contentNode = root
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content");

            String content = contentNode.asText("");

            if (content.isBlank()) {
                throw new IllegalStateException("OpenAI 응답에서 content를 찾을 수 없습니다.");
            }

            JsonNode resultJson = objectMapper.readTree(content);

            String status = resultJson.path("status").asText("NOT_FULFILLED");
            double score = resultJson.path("score").asDouble(0.0);
            int matchedKeywordCount = resultJson.path("matchedKeywordCount").asInt(0);
            int totalKeywordCount = resultJson.path("totalKeywordCount").asInt(0);
            String evidence = resultJson.path("evidence").asText("");
            String reason = resultJson.path("reason").asText(evidence); // reason이 없으면 evidence로 대체

            return new EvaluationResult(
                    requirementId,          // 현재 구조로는 항상 null
                    requirementText,
                    status,
                    score,
                    matchedKeywordCount,
                    totalKeywordCount,
                    evidence,
                    reason
            );

        } catch (IOException e) {
            throw new RuntimeException("OpenAI 응답 파싱 실패: " + e.getMessage(), e);
        }
    }
}
