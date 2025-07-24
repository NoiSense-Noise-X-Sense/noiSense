package com.dosion.noisense.common.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmotionScoreService {

  private final com.dosion.noisense.common.ai.service.GptService gptService;

  /**
   * 게시글 내용(content)로 감정 점수(0~50점) 평가
   */
  public int evaluateEmotionScore(String content) {
    String prompt = buildPrompt(content);
    try {
      String gptResult = gptService.gptMessage(prompt);
      return Integer.parseInt(gptResult.replaceAll("[^0-9]", ""));
    } catch (Exception e) {
      throw new RuntimeException("감정점수 분석 중 오류 발생", e);
    }
  }

  /**
   * 프롬프트 일관성 유지
   */
  private String buildPrompt(String content) {
    return String.format(
      "아래 글의 감정점수를 0~50점 사이의 정수로만 답하세요.\n" +
        "0점은 매우 부정적, 50점은 매우 긍정적입니다.\n" +
        "점수 외 다른 설명은 절대 하지 마세요.\n\n" +
        "---\n글 내용:\n%s", content
    );
  }
}
