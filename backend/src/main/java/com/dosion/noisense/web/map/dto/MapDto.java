package com.dosion.noisense.web.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MapDto {

  @Schema(description = "평균 소음")
  private Double avgNoise;

  @Schema(description = "행정구")
  private String autonomousDistrictCode;

  @Schema(description = "행정구_ENG")
  private String autonomousDistrictEng;

  @Schema(description = "행정구_KOR")
  private String autonomousDistrictKor;

  @Schema(description = "행정동")
  private String administrativeDistrictCode;

  @Schema(description = "행정동_ENG")
  private String administrativeDistrictEng;

  @Schema(description = "행정동_KOR")
  private String administrativeDistrictKor;

  @Schema(description = "체감 소음")
  private Double perceivedNoise;
}
