package com.dosion.noisense.web.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "소음 순위 DTO")
public class RankDto {

  @Schema(description = "행정구 or 행정동")
  private String region;

  @Schema(description = "평균 소음")
  private Double avgNoise;

}
