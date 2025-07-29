package com.dosion.noisense.web.geodata.dto;

import com.dosion.noisense.module.geodata.enums.GeometryFormat;
import com.dosion.noisense.module.geodata.enums.GeometryType;
import com.dosion.noisense.module.sensor.enums.BoundaryType;
import com.fasterxml.jackson.databind.JsonNode;

// repository 전용 dto
public record BoundaryPolygonProjection(
  long boundaryPolygonId,
  String districtNameEn,
  String districtNameKo,
  String administrativeDistrict,
  String autonomousDistrict,
  BoundaryType boundaryType,
  GeometryFormat geometryFormat,
  GeometryType geometryType,
  JsonNode geometryCoordinate,
  JsonNode geometryCentroid
) {}
