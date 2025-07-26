package com.dosion.noisense.web.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BoardDto {

  private Long boardId;              // 게시글 PK
  private Long userId;               // 작성자 ID
  private String nickname;           // 작성자 닉네임
  private String title;              // 제목
  private String content;            // 내용
  private Long emotionalScore;       // 감정 점수
  private Long empathyCount;         // 공감 수
  private Long viewCount;            // 조회수
  private Long commentCount;         // 댓글 수
  private String autonomousDistrict; // 자치구 코드(UUID)
  private String autonomousDistrictName; // 프론트 출력용 이름
  private String administrativeDistrict; // 행정동
  private LocalDateTime createdDate; // 작성일
  private LocalDateTime modifiedDate; // 수정일
  private Boolean isEmpathized; // 내가 이미 공감했는지 여부

  // JPQL 용 생성자 (autonomousDistrictName 제외)
  public BoardDto(Long boardId, Long userId, String nickname, String title, String content,
                  Long emotionalScore, Long empathyCount, Long viewCount, Integer commentCount,
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
    this.commentCount = commentCount != null ? commentCount.longValue() : 0L;
    this.autonomousDistrict = autonomousDistrict;
    this.administrativeDistrict = administrativeDistrict;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  // Builder 용 생성자
  @Builder
  public BoardDto(Long boardId, Long userId, String nickname, String title, String content,
                  Long emotionalScore, Long empathyCount, Long viewCount, Long commentCount,
                  String autonomousDistrict, String autonomousDistrictName, String administrativeDistrict,
                  LocalDateTime createdDate, LocalDateTime modifiedDate, Boolean isEmpathized) {
    this.boardId = boardId;
    this.userId = userId;
    this.nickname = nickname;
    this.title = title;
    this.content = content;
    this.emotionalScore = emotionalScore;
    this.empathyCount = empathyCount;
    this.viewCount = viewCount;
    this.commentCount = commentCount;
    this.autonomousDistrict = autonomousDistrict;
    this.autonomousDistrictName = autonomousDistrictName;
    this.administrativeDistrict = administrativeDistrict;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.isEmpathized = isEmpathized;
  }

  // int 타입 지원 (JPA 쿼리에서 int로 들어오는 경우 대응)
  public BoardDto(Long boardId, Long userId, String nickname, String title, String content,
                  Long emotionalScore, Long empathyCount, Long viewCount, int commentCount,
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
    this.commentCount = (long) commentCount;
    this.autonomousDistrict = autonomousDistrict;
    this.administrativeDistrict = administrativeDistrict;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  // Long 타입 지원 (JPA 쿼리에서 Long으로 들어오는 경우 대응)
  public BoardDto(Long boardId, Long userId, String nickname, String title, String content,
                  Long emotionalScore, Long empathyCount, Long viewCount, Long commentCount,
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
    this.commentCount = commentCount != null ? commentCount : 0L;
    this.autonomousDistrict = autonomousDistrict;
    this.administrativeDistrict = administrativeDistrict;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }


  public Boolean getIsEmpathized() {
    return isEmpathized;
  }
  public void setIsEmpathized(Boolean isEmpathized) {
    this.isEmpathized = isEmpathized;
  }


}
