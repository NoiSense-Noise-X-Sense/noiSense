package com.dosion.noisense.web.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

  private Long id; // 댓글 pk

  @JsonProperty("board_id")
  private Long boardId;

  @JsonProperty("user_id")
  private Long userId;

  private String nickname; // 작성자 닉네임
  private String content; // 댓글 내용

  @JsonProperty("created_date")
  private LocalDateTime createdDate;

  @JsonProperty("updated_date")
  private LocalDateTime updatedDate;
}


