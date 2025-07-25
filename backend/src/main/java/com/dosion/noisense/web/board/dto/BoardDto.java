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
@Builder(toBuilder = true)
public class BoardDto {

  private Long boardId;              // 게시글 PK
  private Long userId;               // 작성자 ID
  private String nickname;           // 작성자 닉네임
  private String title;              // 제목
  private String content;            // 내용
  private Long emotionalScore;       // 감정 점수
  private Long empathyCount;         // 공감 수
  private Long viewCount;            // 조회수
  private String autonomousDistrict; // 자치구 코드(UUID)
  private String autonomousDistrictName; // 프론트 출력용 이름
  private String administrativeDistrict; // 행정동
  private LocalDateTime createdDate; // 작성일
  private LocalDateTime modifiedDate; // 수정일

  // JPQL 용 생성자 (autonomousDistrictName 제외)
  public BoardDto(Long boardId, Long userId, String nickname, String title, String content,
                  Long emotionalScore, Long empathyCount, Long viewCount,
                  String autonomousDistrict, String administrativeDistrict,
                  LocalDateTime createdDate, LocalDateTime modifiedDate) {
    this.boardId = boardId;
    this.userId = userId;
    this.nickname = nickname;
    this.title = title;
    this.content = content;
    this.emotionalScore = emotionalScore;
    this.empathyCount = empathyCount;
    this.viewCount = viewCount;
    this.autonomousDistrict = autonomousDistrict;
    this.administrativeDistrict = administrativeDistrict;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }
}
