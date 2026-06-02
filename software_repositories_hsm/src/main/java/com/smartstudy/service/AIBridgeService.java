package com.smartstudy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartstudy.domain.Note;
import com.smartstudy.domain.Question;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIBridgeService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String PROMPT_TEMPLATE = """
            아래 텍스트를 분석하여 반드시 다음 JSON 형식만 반환하세요. 마크다운 코드블록 없이 순수 JSON만 출력하세요.

            {
              "summary": "3~5문장으로 핵심 내용 요약",
              "keywords": ["핵심단어1", "핵심단어2", "핵심단어3"],
              "questions": [
                { "type": "ox", "question": "첫 번째 O/X 문제를 서술형으로 작성", "answer": "O" },
                { "type": "ox", "question": "두 번째 O/X 문제를 서술형으로 작성", "answer": "X" },
                { "type": "short_answer", "question": "단답형 질문 1", "answer": "정답1" },
                { "type": "short_answer", "question": "단답형 질문 2", "answer": "정답2" },
                { "type": "short_answer", "question": "단답형 질문 3", "answer": "정답3" }
              ]
            }

            반드시 지켜야 할 규칙:
            1. questions 배열은 정확히 5개 (ox 2개, short_answer 3개 순서대로).
            2. ox 문제의 answer는 반드시 "O" 또는 "X" 중 하나만 사용.
            3. choices 필드는 절대 포함하지 말 것.
            4. 다른 type의 문제는 절대 생성하지 말 것.

            분석할 텍스트:
            """;

    public Note analyze(String userId, String subjectId, String title, String text) {
        String rawJson = callGeminiApi(text);
        return parseResponse(userId, subjectId, title, text, rawJson);
    }

    private String callGeminiApi(String text) {
        String prompt = PROMPT_TEMPLATE + text;

        String requestBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        { "text": %s }
                      ]
                    }
                  ]
                }
                """.formatted(objectMapper.valueToTree(prompt).toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        String url = apiUrl + "?key=" + apiKey;

        int maxRetries = 3;
        int delayMs = 5000;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.at("/candidates/0/content/parts/0/text").asText();
            } catch (org.springframework.web.client.HttpClientErrorException e) {
                if (e.getStatusCode().value() == 429 && attempt < maxRetries) {
                    try { Thread.sleep((long) delayMs * attempt); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                } else {
                    throw new RuntimeException("Gemini API 호출 실패 (시도 " + attempt + "): " + e.getMessage(), e);
                }
            } catch (Exception e) {
                throw new RuntimeException("Gemini API 응답 파싱 실패", e);
            }
        }
        throw new RuntimeException("Gemini API 최대 재시도 횟수 초과");
    }

    private Note parseResponse(String userId, String subjectId, String title, String originalText, String rawJson) {
        String json = rawJson.strip();
        if (json.startsWith("```")) {
            json = json.replaceAll("^```[a-z]*\\n?", "").replaceAll("```$", "").strip();
        }

        try {
            JsonNode root = objectMapper.readTree(json);

            Note note = new Note();
            note.setTitle(title);
            note.setUserId(userId);
            note.setSubjectId(subjectId);
            note.setOriginalText(originalText);
            note.setSummary(root.get("summary").asText());
            note.setCreatedAt(LocalDateTime.now());

            List<String> keywords = new ArrayList<>();
            root.get("keywords").forEach(kw -> keywords.add(kw.asText()));
            note.setKeywords(keywords);

            List<Question> questions = new ArrayList<>();
            root.get("questions").forEach(qNode -> {
                Question q = new Question();
                q.setType(qNode.get("type").asText());
                q.setQuestion(qNode.get("question").asText());
                q.setAnswer(qNode.get("answer").asText());
                questions.add(q);
            });
            note.setQuestions(questions);

            return note;
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 JSON 파싱 실패: " + json, e);
        }
    }
}
