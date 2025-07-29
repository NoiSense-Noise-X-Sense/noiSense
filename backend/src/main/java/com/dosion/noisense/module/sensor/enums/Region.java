package com.dosion.noisense.module.sensor.enums;

import lombok.Getter;

public enum Region {
  RESIDENTIAL_AREA("residential_area", "주거 지역"),
  ROADS_AND_PARKS("roads_and_parks", "도로 및 공원"),
  INDUSTRIAL_AREA("industrial_area", "산업 지역"),
  TRADITIONAL_MARKETS("traditional_markets", "전통 시장"),
  MAIN_STREET("main_street", "주요 도로"),
  COMMERCIAL_AREA("commercial_area", "상업 지역"),
  PUBLIC_FACILITIES("public_facilities", "공공시설"),
  PARKS("parks", "공원");


  @Getter
  private final String nameEn;
  @Getter
  private final String nameKo;


  Region(String nameEn, String nameKo) {
    this.nameEn = nameEn;
    this.nameKo = nameKo;
  }

  public static Region fromKey(String key) {
    for (Region region: Region.values()) {
      if (region.name().equals(key)) {
        return region;
      }
    }
    return null; // 또는 throw new IllegalArgumentException("No matching region for key: " + key);
  }

  public static Region fromNameEn(String key) {
    for (Region region: Region.values()) {
      if (region.nameEn.equals(key)) {
        return region;
      }
    }
    return null;
  }

  public static Region fromNameKo(String key) {
    for (Region region: Region.values()) {
      if (region.nameKo.equals(key)) {
        return region;
      }
    }
    return null;
  }


}
