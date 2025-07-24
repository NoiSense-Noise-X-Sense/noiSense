package com.dosion.noisense.module.geodata.service;

import com.dosion.noisense.module.geodata.repository.BoundaryPolygonRepository;
import com.dosion.noisense.web.district.dto.DistrictDto;
import com.dosion.noisense.web.geodata.dto.BoundaryPolygonDto;
import com.dosion.noisense.web.geodata.dto.BoundaryPolygonProjection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GeodataService {

  private final BoundaryPolygonRepository boundaryPolygonRepository;

  /*자치구 경계 좌표 조회*/
  public List<BoundaryPolygonDto> getAutonomousPolygons() {

    try {
      List<BoundaryPolygonProjection> projections = boundaryPolygonRepository.findAutonomousPolygons();

      return projections.stream()
        .map(p -> {
          DistrictDto districtDto = DistrictDto.builder()
            .hasAutonomousDistrictCode(false) // or true? 상황에 맞게
            .districtCode(p.autonomousDistrict())
            .districtNameEn(p.districtNameEn())
            .districtNameKo(p.districtNameKo())
            .build();

          BoundaryPolygonDto.Geometry geometry = BoundaryPolygonDto.Geometry.builder()
            .geometryType(p.geometryType())
            .coordinates(p.geometryCoordinate())
            .build();

          return BoundaryPolygonDto.builder()
            .boundaryPolygonId(p.boundaryPolygonId())
            .district(districtDto)
            .boundaryType(p.boundaryType())
            .geometryFormat(p.geometryFormat())
            .geometry(geometry)
            .build();
        })
        .toList();

    } catch(Exception e) {
      log.error("select autonomous polygons error", e);
      throw e;
    }
  }

  /*행정동 경계 좌표 조회*/
  public List<BoundaryPolygonDto> getAdministrativePolygons() {

    try {
      List<BoundaryPolygonProjection> projections = boundaryPolygonRepository.findAdministrativePolygons();

      return projections.stream()
        .map(p -> {
          DistrictDto districtDto = DistrictDto.builder()
            .hasAutonomousDistrictCode(true)
            .districtCode(p.administrativeDistrict())
            .districtNameEn(p.districtNameEn())
            .districtNameKo(p.districtNameKo())
            .autonomousDistrictCode(p.autonomousDistrict())
            .build();

          BoundaryPolygonDto.Geometry geometry = BoundaryPolygonDto.Geometry.builder()
            .geometryType(p.geometryType())
            .coordinates(p.geometryCoordinate())
            .build();

          return BoundaryPolygonDto.builder()
            .boundaryPolygonId(p.boundaryPolygonId())
            .district(districtDto)
            .boundaryType(p.boundaryType())
            .geometryFormat(p.geometryFormat())
            .geometry(geometry)
            .build();
        })
        .toList();

    } catch(Exception e) {
      log.error("select administrative polygons error", e);
      throw e;
    }
  }

}
