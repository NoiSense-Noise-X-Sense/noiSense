package com.dosion.noisense.module.geodata.enums;

public enum GeometryType {
  MultiPolygon, Polygon;

  public static GeometryType from(String key) {
    for (GeometryType geoMetryType : GeometryType.values()) {
      if (geoMetryType.name().equals(key)) {
        return geoMetryType;
      }
    }
    return null;
  }
}
