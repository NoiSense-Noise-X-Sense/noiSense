package com.dosion.noisense.web.board.elasticsearch.dto;

import com.dosion.noisense.web.board.dto.BoardDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "board-index")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardEsDocument {

  @Id
  private String id;
  private String title;
  private String content;
  private String username;
  private Long userId;
  private String created_date;
  private String updated_date;

  @Builder.Default
  private Long view_count = 0L;

  /** BoardDto → BoardEsDocument 변환 메서드 **/
  public static BoardEsDocument from(BoardDto dto) {
    return BoardEsDocument.builder()
      .id(dto.getBoardId() != null ? String.valueOf(dto.getBoardId()) : null)
      .title(dto.getTitle())
      .content(dto.getContent())
      .username(dto.getNickname())
      .userId(dto.getUserId())
      .created_date(dto.getCreatedDate() != null ? dto.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) : null)
      .updated_date(dto.getModifiedDate() != null ? dto.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) : null)
      .view_count(dto.getViewCount())
      .build();
  }

}
