package com.dosion.noisense.module.geodata.service;

import com.dosion.noisense.module.geodata.entity.BoundaryPolygon;
import com.dosion.noisense.module.geodata.repository.BoundaryPolygonRepository;
import com.dosion.noisense.module.sensor.enums.BoundaryType;
import com.dosion.noisense.web.district.dto.DistrictDto;
import com.dosion.noisense.web.geodata.dto.BoundaryPolygonDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GeodataService {

  private final BoundaryPolygonRepository boundaryPolygonRepository;


  /*전체 경계 좌표 조회*/
  public List<BoundaryPolygonDto> getAllPolygons() {

    List<BoundaryPolygon> polygons = boundaryPolygonRepository.findAllPolygons();

    // dto로 변환해서 넘겨준다
    List<BoundaryPolygonDto> polygonDtos = polygons
      .stream()
      .map(entity -> {

        DistrictDto districtDto;

        // 이거 통과 못 할 것 같은데???
        if (BoundaryType.AUTONOMOUS_DISTRICT.equals(entity.getBoundaryType())) {
          districtDto = DistrictDto.builder()
            .hasAutonomousDistrictCode(false)
            .districtCode(entity.getAutonomousDistrict())
            .build();
        } else {
          districtDto = DistrictDto.builder()
            .hasAutonomousDistrictCode(true)
            .districtCode(entity.getAdministrativeDistrict())
            .build();
        }

        return BoundaryPolygonDto.builder()
          .boundaryPolygonId(entity.getBoundaryPolygonId())
          .district(districtDto)
          // TODO: 검증 필요
          .geometry(BoundaryPolygon.toGeometryDto(entity))
          .build();
        }
      ).toList();

    return polygonDtos;
  }

}
