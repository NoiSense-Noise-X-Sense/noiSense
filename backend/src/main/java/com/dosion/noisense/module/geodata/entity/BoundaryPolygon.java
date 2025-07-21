package com.dosion.noisense.module.geodata.entity;

import com.dosion.noisense.module.geodata.enums.GeometryFormat;
import com.dosion.noisense.module.geodata.enums.GeometryType;
import com.dosion.noisense.module.sensor.enums.BoundaryType;
import com.dosion.noisense.web.geodata.dto.BoundaryPolygonDto;
import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonProperty("geometry_coordinate")
  @Column(nullable = false, name = "geometry_coordinate")
  private String coordinate; // JSONString

  @CreatedDate
  @Column(updatable = false)
  protected LocalDateTime createdDate;

  public static BoundaryPolygonDto.Geometry toGeometryDto(BoundaryPolygon boundaryPolygon) {
    return BoundaryPolygonDto.Geometry.builder()
      .geometryType(boundaryPolygon.getGeometryType())
      .coordinates(boundaryPolygon.coordinate)
      .build();
  }

}
