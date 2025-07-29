package com.dosion.noisense.module.geodata.entity;

import com.dosion.noisense.common.util.JsonNodeConverter;
import com.dosion.noisense.module.geodata.enums.GeometryFormat;
import com.dosion.noisense.module.geodata.enums.GeometryType;
import com.dosion.noisense.module.sensor.enums.BoundaryType;
import com.dosion.noisense.web.geodata.dto.BoundaryPolygonDto;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "boundary_polygon", schema = "noisense")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoundaryPolygon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long boundaryPolygonId;

  @Column(nullable = false)
  private String autonomousDistrict;
  private String administrativeDistrict;

  @Column(nullable = false, name = "name_en")
  private String districtNameEn;
  @Column(nullable = false, name = "name_ko")
  private String districtNameKo;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BoundaryType boundaryType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GeometryFormat geometryFormat;
  //
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GeometryType geometryType;

  @Column(name = "geometryCoordinate", columnDefinition = "TEXT") // 255자 이상일 시 columnDefinition 권장
  @Convert(converter = JsonNodeConverter.class)
  private JsonNode geometryCoordinate;

  @Column(name = "geometryCentroid") // 255자 이상일 시 columnDefinition 권장
  @Convert(converter = JsonNodeConverter.class)
  private JsonNode geometryCentroid;

  @CreatedDate
  @Column(updatable = false)
  protected LocalDateTime createdDate;

  public static BoundaryPolygonDto.Geometry toGeometryDto(BoundaryPolygon boundaryPolygon) throws IllegalArgumentException{
    JsonNode coordinate = boundaryPolygon.geometryCoordinate;
    JsonNode centroid = boundaryPolygon.geometryCentroid;

    return BoundaryPolygonDto.Geometry.builder()
      .geometryType(boundaryPolygon.getGeometryType())
      .coordinates(coordinate)
      .centroid(centroid)
      .build();
  }
}
