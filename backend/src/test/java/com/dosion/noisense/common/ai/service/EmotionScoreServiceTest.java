package com.dosion.noisense.common.ai.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("새 API 구조와 충돌로 임시 중단 - 추후 리팩토링 필요")
@SpringBootTest
public class EmotionScoreServiceTest {

  @Autowired
  private EmotionScoreService emotionScoreService;

  @Test
  void testEvaluateEmotionScore() {
    String contentPositive = "이 게시글은 정말 기쁘고 즐거운 내용입니다!";
    int scorePositive = emotionScoreService.evaluateEmotionScore(contentPositive);
    System.out.println("긍정 감정점수: " + scorePositive);
    assertTrue(scorePositive >= 0 && scorePositive <= 50);

    String contentNegative = "오늘은 너무 힘들고 우울한 하루였다. 모든 일이 마음처럼 되지 않는다.";
    int scoreNegative = emotionScoreService.evaluateEmotionScore(contentNegative);
    System.out.println("부정 감정점수: " + scoreNegative);
    assertTrue(scoreNegative >= 0 && scoreNegative <= 50);

    // 추가 테스트(중립)
    String contentNeutral = "오늘은 날씨가 맑다. 평소와 다름없이 하루가 흘러간다.";
    int scoreNeutral = emotionScoreService.evaluateEmotionScore(contentNeutral);
    System.out.println("중립 감정점수: " + scoreNeutral);
    assertTrue(scoreNeutral >= 0 && scoreNeutral <= 50);

    // 점수 비교 (긍정 > 중립 > 부정이면 제일 이상적)
    assertTrue(scorePositive > scoreNeutral);
    assertTrue(scoreNeutral > scoreNegative);
  }
}
