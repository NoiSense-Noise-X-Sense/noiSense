package com.dosion.noisense.web.noise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvgNoiseWithPerceivedDto {

  @Schema(description = "평균 소음")
  private Double avgNoise;

  @Schema(description = "체감 소음")
  private Double perceivedNoise;

  @Schema(description = "자치구 코드")
  private String autonomousDistrictCode;

  @Schema(description = "자치구 영문명")
  private String autonomousDistrictEng;

  @Schema(description = "자치구 한글명")
  private String autonomousDistrictKor;

  @Schema(description = "행정동 코드")
  private String administrativeDistrictCode;

  @Schema(description = "행정동 영문명")
  private String administrativeDistrictEng;

  @Schema(description = "행정동 한글명")
  private String administrativeDistrictKor;
}
