package com.dosion.noisense.web.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EmotionBoardDto {

  private Double emotionalScore;

  private LocalDateTime createdDate;

}
