package com.dosion.noisense.common.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class GptService {

  private final ObjectMapper mapper = new ObjectMapper();

  // 환경변수, application.yaml 등에서 관리 추천
  @Value("${openai.api-key}")
  private String apiKey;

  public String gptMessage(String message) throws Exception {

    // 요청 본문 구성
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", "gpt-4o"); // 모델명
    // OpenAI ChatCompletion 구조에 맞게 조정 필요 (예시 기준)
    requestBody.put("messages", new Object[] {
      Map.of("role", "user", "content", message)
    });

    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create("https://api.openai.com/v1/chat/completions"))
      .header("Authorization", "Bearer " + apiKey)
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
      .build();

    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    JsonNode jsonNode = mapper.readTree(response.body());

    // 응답 구조에 따라 content 추출
    // OpenAI 응답: choices[0].message.content
    String gptMessageResponse = jsonNode.get("choices")
      .get(0)
      .get("message")
      .get("content")
      .asText();

    return gptMessageResponse.trim();
  }
}
