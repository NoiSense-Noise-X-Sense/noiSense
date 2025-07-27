package com.dosion.noisense.module.noise.repository;

import com.dosion.noisense.module.noise.projection.NoiseStatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface NoiseStatRepository {

  List<NoiseStatDto> findAverageNoiseByAutonomousDistrict(LocalDateTime startDate, LocalDateTime endDate);

  List<NoiseStatDto> findAverageNoiseByAdministrativeDistrict(LocalDateTime startDate, LocalDateTime endDate, String parentDistrictCode);

  List<NoiseStatDto> findAverageNoiseByAllAdministrativeDistrict(LocalDateTime startDate, LocalDateTime endDate, List<String> regionList);

  List<NoiseStatDto> findAverageNoiseByAllAutonomousDistrict(LocalDateTime startDate, LocalDateTime endDate, List<String> regionList);
}
