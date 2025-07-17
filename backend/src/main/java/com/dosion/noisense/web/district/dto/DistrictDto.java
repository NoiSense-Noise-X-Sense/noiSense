package com.dosion.noisense.web.district.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DistrictDto {
  private String districtNameEn;
  private String districtNameKo;
  private String districtCode;
  private String autonomousDistrictCode;
  private boolean hasAutonomousDistrictCode;

  @Builder
  public DistrictDto(String districtNameEn, String districtNameKo, String districtCode, String autonomousDistrictCode, boolean hasAutonomousDistrictCode) {
    this.districtNameEn = districtNameEn;
    this.districtNameKo = districtNameKo;
    this.districtCode = districtCode;
    this.autonomousDistrictCode = autonomousDistrictCode;
    this.hasAutonomousDistrictCode = hasAutonomousDistrictCode;
  }
}
