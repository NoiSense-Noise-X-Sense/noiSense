package com.dosion.noisense.web.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "편차 소음 DTO")
public class DeviationDto {

  @Schema(description = "행정구 or 행정동")
  String region;

  @Schema(description = "평균 소음")
  Double avgNoise;

  @Schema(description = "최대 소음")
  Double maxNoise;

  @Schema(description = "최소 소음")
  Double minNoise;

  @Schema(description = "편차 / 최대 소음 - 최소 소음")
  Double deviation;

}
