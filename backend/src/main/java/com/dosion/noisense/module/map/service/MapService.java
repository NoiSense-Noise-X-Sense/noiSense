package com.dosion.noisense.module.map.service;


import com.dosion.noisense.common.util.PerceivedNoiseCalculator;
import com.dosion.noisense.module.sensor.repository.HourDistrictSummaryRepository;
import com.dosion.noisense.web.map.dto.MapDto;
import com.dosion.noisense.web.report.dto.AvgNoiseRegionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapService {

  private final HourDistrictSummaryRepository hourDistrictSummaryRepository;

  private final PerceivedNoiseCalculator perceivedNoiseCalculator;

  public List<MapDto> getMapData(LocalDateTime startDate, LocalDateTime endDate, String autonomousDistrictCode, String administrativeDistrictCode, List<String> regionList) {
    log.info("getMapData");
    long startTime = System.nanoTime();

/*     지역을 Region Enum에 담기
    List<Region> regionTypeList = new ArrayList<>();
    for (String region : regionList) {
      regionTypeList.add(Region.fromNameEn(region));
    }
     log.info("regionTypeList : {}", regionTypeList);*/

    List<String> lowerCaseRegionList = new ArrayList<>();
    for (String region : regionList) {
      lowerCaseRegionList.add(region.toLowerCase());
    }

    List<MapDto> result = new ArrayList<>();

    List<AvgNoiseRegionDto> avgNoiseRegionDtoList = hourDistrictSummaryRepository.findAverageNoiseByRegion(startDate, endDate, autonomousDistrictCode, administrativeDistrictCode, lowerCaseRegionList);
    // log.info("avgNoiseRegionDtoList : {}", avgNoiseRegionDtoList);
    for (AvgNoiseRegionDto dto : avgNoiseRegionDtoList) {
      // Double perceivedNoise = 0.0;
      Double perceivedNoise = perceivedNoiseCalculator.calcPerceivedNoise(dto.getAvgNoise(), startDate, endDate, dto.getAutonomousDistrictCode(), dto.getAdministrativeDistrictCode());
      result.add(MapDto.builder()
        .avgNoise(dto.getAvgNoise())
        .perceivedNoise(perceivedNoise)
        .autonomousDistrictCode(dto.getAutonomousDistrictCode())
        .autonomousDistrictKor(dto.getAutonomousDistrictKor())
        .autonomousDistrictEng(dto.getAutonomousDistrictEng())
        .administrativeDistrictCode(dto.getAdministrativeDistrictCode())
        .administrativeDistrictKor(dto.getAdministrativeDistrictKor())
        .administrativeDistrictEng(dto.getAdministrativeDistrictEng())
        .build());
    }

    long endTime = System.nanoTime();
    log.info("getMapData time : {} ms", (endTime - startTime) / 1000000);

    return result;
  }


}
