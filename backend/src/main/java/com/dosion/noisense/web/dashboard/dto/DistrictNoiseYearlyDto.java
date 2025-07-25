package com.dosion.noisense.web.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "연도별 평균 소음 정보")
public class DistrictNoiseYearlyDto {

  @Schema(description = "연도")
  private Integer year;

  @Schema(description = "서울시 평균 소음 (dB)")
  private BigDecimal avgSeoul;

  @Schema(description = "선택 자치구 평균 소음 (dB)")
  private BigDecimal avgDistrict;
}
