package com.dosion.noisense.module.geodata.enums;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum GeometryFormat {
  GeoJSON;

  public static GeometryFormat fromKey(String key) {
    log.info("key : {}", key);
    for (GeometryFormat geometryFormat : GeometryFormat.values()) {
      if (geometryFormat.name().equals(key)) {
        return geometryFormat;
      }
    }
    return null;
  }
}
