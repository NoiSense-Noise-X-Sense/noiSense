package com.dosion.noisense.module.board.service;

import com.dosion.noisense.common.ai.service.GptService;
import com.dosion.noisense.web.board.dto.BoardDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardServiceTest {

  @Autowired
  private BoardService boardService;

  @Test
  void emotionalScoreIsReanalyzedWhenBoardIsUpdated() {
    // 실제 서비스 코드 테스트
    BoardDto createDto = BoardDto.builder()
      .userId(1L)
      .nickname("테스터")
      .title("첫 제목")
      .content("오늘 정말 기쁨이 넘친다!") // "기쁨" 포함
      .build();

    BoardDto saved = boardService.createBoard(createDto);
    assertEquals(40L, saved.getEmotionalScore());

    BoardDto updateDto = BoardDto.builder()
      .title("수정된 제목")
      .content("정말 슬픈 하루였다...") // "슬픈" 포함
      .empathyCount(saved.getEmpathyCount())
      .viewCount(saved.getViewCount())
      .autonomousDistrict(saved.getAutonomousDistrict())
      .administrativeDistrict(saved.getAdministrativeDistrict())
      .build();

    BoardDto updated = boardService.updateBoard(saved.getBoardId(), saved.getUserId(), updateDto);

    assertEquals(10L, updated.getEmotionalScore());
  }

  @TestConfiguration
  static class MockGptServiceConfig {
    @Bean
    @Primary
    public GptService mockGptService() {
      return new GptService() {
        @Override
        public String gptMessage(String prompt) {
          // prompt에 따라 결과 반환
          if (prompt.contains("슬픈")) return "10";
          if (prompt.contains("기쁨")) return "40";
          return "25";
        }
      };
    }
  }
}

