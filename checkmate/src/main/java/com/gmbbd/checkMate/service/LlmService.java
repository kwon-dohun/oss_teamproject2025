package com.gmbbd.checkMate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmbbd.checkMate.exception.ApiException;
import com.gmbbd.checkMate.model.EvaluationResult;
import com.gmbbd.checkMate.util.MarkdownCleaner;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LlmService {

    private final WebClient openAiWebClient;
    private final ObjectMapper objectMapper;
    private final MarkdownCleaner markdownCleaner;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    // JSON 감지를 위한 정규식
    private static final Pattern JSON_EXTRACT_PATTERN =
            Pattern.compile("\\{[\\s\\S]*?}", Pattern.MULTILINE);

    // Retry 횟수
    private static final int MAX_RETRY = 2;


    public EvaluationResult evaluateRequirement(String requirementMarkdown, String submissionMarkdown) {

        try {
            String submissionClean = markdownCleaner.cleanForLLM(submissionMarkdown);
            String requirementClean = requirementMarkdown == null ? "" : requirementMarkdown.trim();
            String prompt = buildPrompt(requirementClean, submissionClean);

            String rawResponse = callOpenAIWithRetry(prompt);

            return parseEvaluationResult(rawResponse, requirementMarkdown, null);

        } catch (Exception e) {
            throw new ApiException("LLM 평가 중 오류 발생: " + e.getMessage());
        }
    }

    private String callOpenAIWithRetry(String prompt) {

        Exception lastError = null;

        for (int attempt = 0; attempt <= MAX_RETRY; attempt++) {

            try {
                Map<String, Object> requestBody = Map.of(
                        "model", model,
                        "temperature", 0,
                        "messages", List.of(
                                Map.of("role", "user", "content", prompt)
                        )
                );

                return openAiWebClient.post()
                        .uri("/chat/completions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

            } catch (Exception e) {
                lastError = e;

                if (attempt == MAX_RETRY) {
                    throw new ApiException("OpenAI 호출 실패: " + e.getMessage());
                }

                // exponential backoff 가능하지만 단순 sleep도 충분
                try {
                    Thread.sleep(300L * (attempt + 1));
                } catch (InterruptedException ignored) {}
            }
        }

        throw new ApiException("OpenAI 호출 실패: " + (lastError != null ? lastError.getMessage() : ""));
    }

    private String buildPrompt(String reqMd, String submissionMd) {

        try {
            ClassPathResource resource = new ClassPathResource("prompts/llm_prompt.txt");

            try (InputStream is = resource.getInputStream()) {
                String template = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                return template.formatted(reqMd, submissionMd);
            }

        } catch (IOException e) {
            throw new ApiException("프롬프트 템플릿 로딩 실패: " + e.getMessage());
        }
    }

    private EvaluationResult parseEvaluationResult(String rawResponse,
                                                   String requirementText,
                                                   Long requirementId) {

        if (rawResponse == null || rawResponse.isBlank()) {
            throw new ApiException("OpenAI 응답이 비어 있음");
        }

        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            JsonNode contentNode = root
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content");

            String content = contentNode.asText("");

            if (content.isBlank()) {
                throw new ApiException("OpenAI 응답에서 content를 읽지 못함");
            }

            // JSON 블록만 추출
            Matcher matcher = JSON_EXTRACT_PATTERN.matcher(content);

            if (!matcher.find()) {
                throw new ApiException("OpenAI 응답에서 JSON 형식 발견 실패");
            }

            String jsonText = matcher.group();

            // JSON → Node
            JsonNode json = objectMapper.readTree(jsonText);

            String status = json.path("status").asText("NOT_FULFILLED");
            String evidence = json.path("evidence").asText("");

            double computedScore = 0.0;

            return new EvaluationResult(
                    requirementId,
                    requirementText,
                    status,
                    computedScore,
                    0,
                    0,
                    evidence,
                    evidence
            );

        } catch (Exception e) {
            throw new ApiException("OpenAI 응답 파싱 실패: " + e.getMessage());
        }

    }
}
