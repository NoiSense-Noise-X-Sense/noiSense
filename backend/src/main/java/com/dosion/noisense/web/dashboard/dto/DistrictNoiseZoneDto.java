package com.dosion.noisense.web.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "행정동별 평균 소음 정보")
public class DistrictNoiseZoneDto {

  @Schema(description = "행정동명")
  private String administrativeDistrict;

  @Schema(description = "평균 소음 (dB)")
  private BigDecimal avgNoise;
}
