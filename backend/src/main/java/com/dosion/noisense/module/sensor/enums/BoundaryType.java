package com.dosion.noisense.module.sensor.enums;

import lombok.Getter;

public enum BoundaryType {
  AUTONOMOUS_DISTRICT( "AUTONOMOUS_DISTRICT", "자치구"),
  ADMINISTRATIVE_DISTRICT("ADMINISTRATIVE_DISTRICT", "행정동");

  @Getter
  private final String nameEn;
  @Getter
  private final String nameKo;

  BoundaryType(String nameEn, String nameKo) {
    this.nameEn = nameEn;
    this.nameKo = nameKo;
  }

  public static BoundaryType fromKey(String key) {
    for (BoundaryType boundaryType : BoundaryType.values()) {
      if (boundaryType.name().equals(key)) {
        return boundaryType;
      }
    }
    return null;
  }

  public static BoundaryType fromNameKo(String nameKo) {
    for (BoundaryType boundaryType : BoundaryType.values()) {
      if (boundaryType.nameKo.equals(nameKo)) {
        return boundaryType;
      }
    }
    return null;
  }

  public static BoundaryType fromNameEn(String nameEn) {
    for (BoundaryType boundaryType : BoundaryType.values()) {
      if (boundaryType.nameEn.equals(nameEn)) {
        return boundaryType;
      }
    }
    return null;
  }
}
