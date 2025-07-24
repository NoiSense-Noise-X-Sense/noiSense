package com.dosion.noisense.web.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {

  private Long boardId;              // 게시글 PK
  private Long userId;               // 작성자 ID
  private String nickname;           // 작성자 닉네임
  private String title;              // 제목
  private String content;            // 내용
  private Long emotionalScore;       // 감정 점수
  private Long empathyCount;         // 공감 수
  private Long viewCount;            // 조회수
  private String autonomousDistrict; // 자치구
  private String administrativeDistrict; // 행정동
  private LocalDateTime createdDate; // 작성일
  private LocalDateTime modifiedDate; // 수정일
}
