package com.dosion.noisense.web.board.elasticsearch.dto;

import com.dosion.noisense.web.board.dto.BoardDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "board-index", createIndex = false)
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
  private String autonomousDistrict;

  // Instant 기반으로 변경
  @Field(type = FieldType.Date, format = DateFormat.date_time)
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant createdDate;
  @Field(type = FieldType.Date, format = DateFormat.date_time)
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant modifiedDate;

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
      .autonomousDistrict(dto.getAutonomousDistrict())
      .createdDate(dto.getCreatedDate() != null ? dto.getCreatedDate().atZone(ZoneId.of("Asia/Seoul")).toInstant() : null)
      .modifiedDate(dto.getModifiedDate() != null ? dto.getModifiedDate().atZone(ZoneId.of("Asia/Seoul")).toInstant() : null)
      .view_count(dto.getViewCount())
      .build();
  }

  /** Instant → LocalDateTime (KST) 변환 도우미 메서드 **/
  public LocalDateTime getCreatedDateAsLocalDateTime() {
    return createdDate != null ? createdDate.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null;
  }

  public LocalDateTime getModifiedDateAsLocalDateTime() {
    return modifiedDate != null ? modifiedDate.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null;
  }
}
