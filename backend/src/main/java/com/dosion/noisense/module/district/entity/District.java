package com.dosion.noisense.module.district.entity;

import com.dosion.noisense.web.district.dto.DistrictDto;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class District {

  @Column(nullable = false, name = "autonomous_district")
  private String autonomousDistrictCode;
  @Column(nullable = false, name = "autonomous_district_name_en")
  private String autonomousDistrictNameEn;
  @Column(nullable = false, name = "autonomous_district_name_ko")
  private String autonomousDistrictNameKo;

  @Column(nullable = false, name = "administrative_district")
  private String administrativeDistrictCode;
  @Column(nullable = false, name = "administrative_district_name_en")
  private String administrativeDistrictNameEn;
  @Column(nullable = false, name = "administrative_district_name_ko")
  private String administrativeDistrictNameKo;
  @Column(nullable = false, name = "parent_district_code")
  private String parentDistrictCode;

  @Builder
  public District(String autonomousDistrictCode, String autonomousDistrictNameEn, String autonomousDistrictNameKo, String administrativeDistrictCode, String administrativeDistrictNameEn, String administrativeDistrictNameKo, String parentDistrictCode) {
    this.autonomousDistrictCode = autonomousDistrictCode;
    this.autonomousDistrictNameEn = autonomousDistrictNameEn;
    this.autonomousDistrictNameKo = autonomousDistrictNameKo;
    this.administrativeDistrictCode = administrativeDistrictCode;
    this.administrativeDistrictNameEn = administrativeDistrictNameEn;
    this.administrativeDistrictNameKo = administrativeDistrictNameKo;
    this.parentDistrictCode = parentDistrictCode;
  }


  public DistrictDto toDto() {
    if (this.parentDistrictCode != null) {
      // 행정동
      return DistrictDto.builder()
        .hasAutonomousDistrictCode(true)
        .autonomousDistrictCode(this.autonomousDistrictCode)
        .districtCode(this.administrativeDistrictCode)
        .districtNameKo(this.administrativeDistrictNameKo)
        .districtNameEn(this.administrativeDistrictNameEn)
        .build();
    } else {
      // 자치구
      return DistrictDto.builder()
        .hasAutonomousDistrictCode(false)
        .autonomousDistrictCode(null)
        .districtCode(this.autonomousDistrictCode)
        .districtNameKo(this.autonomousDistrictNameKo)
        .districtNameEn(this.autonomousDistrictNameEn)
        .build();
    }
  }
}
