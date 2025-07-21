package com.dosion.noisense.web.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Schema(description = "지도")
public class AvgNoiseRegionDto {

  @Schema(description = "평균 소음")
  private Double avgNoise;

  @Schema(description = "행정구")
  private String autonomousDistrict_Code;

  @Schema(description = "행정구_ENG")
  private String autonomousDistrict_english;

  @Schema(description = "행정구_KOR")
  private String autonomousDistrict_kor;

  @Schema(description = "행정동")
  private String administrativeDistrict_Code;

  @Schema(description = "행정동_ENG")
  private String administrativeDistrict_english;

  @Schema(description = "행정동_KOR")
  private String administrativeDistrict_kor;

}
