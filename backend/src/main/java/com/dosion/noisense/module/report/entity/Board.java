package com.dosion.noisense.module.report.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board", schema = "noisense")
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "board_id")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "nickname")
  private String nickname;

  @Column(name = "title")
  private String title;

  @Column(name = "content")
  private String content;

  @Column(name = "emotional_score")
  private Double emotionalScore;

  @Column(name = "empathy_count")
  private Double empathyCount;

  @Column(name = "view_count")
  private Double viewCount;

  @Column(name = "autonomous_district")
  private String autonomousDistrict;

  @Column(name = "administrative_district")
  private String administrativeDistrict;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;

}
