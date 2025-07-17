package com.dosion.noisense.web.geodata.dto;


import com.dosion.noisense.module.geodata.enums.GeometryFormat;
import com.dosion.noisense.module.geodata.enums.GeometryType;
import com.dosion.noisense.module.sensor.enums.BoundaryType;
import com.dosion.noisense.web.district.dto.DistrictDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoundaryPolygonDto {

  private long boundaryPolygonId;
  private DistrictDto district;
  private BoundaryType boundaryType; // 자치구 또는 행정동
  private GeometryFormat geometryFormat; // GeoJSON
  private Geometry geometry; // 폴리곤 유형, 좌표

  @Builder
  public BoundaryPolygonDto(long boundaryPolygonId, DistrictDto district
    , BoundaryType boundaryType, GeometryFormat geometryFormat, Geometry geometry) {
    this.boundaryPolygonId = boundaryPolygonId;
    this.district = district;
    this.boundaryType = boundaryType;
    this.geometryFormat = geometryFormat;
    this.geometry = geometry;
  }


  @Builder
  @Getter
  public static class Geometry {
    private GeometryType geometryType;
    private String coordinates;

    @Builder
    public Geometry(GeometryType geometryType, String coordinates) {
      this.geometryType = geometryType;
      this.coordinates = coordinates;
    }
  }
}



