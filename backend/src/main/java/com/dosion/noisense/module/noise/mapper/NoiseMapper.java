package com.dosion.noisense.module.noise.mapper;

import com.dosion.noisense.module.noise.projection.NoiseStatDto;
import com.dosion.noisense.web.noise.dto.AvgNoiseWithPerceivedDto;
import com.dosion.noisense.web.noise.dto.response.DashboardMapNoiseResponseDto;
import com.dosion.noisense.web.noise.dto.response.FullMapNoiseResponseDto;
import com.dosion.noisense.web.noise.dto.response.MainMapNoiseResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class NoiseMapper {

  // 공통 변환: Projection → AvgNoiseWithPerceivedDto
  public static AvgNoiseWithPerceivedDto toDto(NoiseStatDto dto) {
    return AvgNoiseWithPerceivedDto.builder()
      .avgNoise(dto.avgNoise())
      .perceivedNoise(dto.perceivedNoise())
      .autonomousDistrictCode(dto.autonomousDistrictCode())
      .autonomousDistrictEng(dto.autonomousDistrictEng())
      .autonomousDistrictKor(dto.autonomousDistrictKor())
      .administrativeDistrictCode(dto.administrativeDistrictCode())
      .administrativeDistrictEng(dto.administrativeDistrictEng())
      .administrativeDistrictKor(dto.administrativeDistrictKor())
      .build();
  }

  public static MainMapNoiseResponseDto toMainMapResponse(List<NoiseStatDto> list) {
    return MainMapNoiseResponseDto.builder()
      .autonomousDistricts(list.stream().map(NoiseMapper::toDto).collect(Collectors.toList()))
      .build();
  }

  public static DashboardMapNoiseResponseDto toDashboardMapResponse(List<NoiseStatDto> list) {
    return DashboardMapNoiseResponseDto.builder()
      .administrativeDistricts(list.stream().map(NoiseMapper::toDto).collect(Collectors.toList()))
      .build();
  }

  public static FullMapNoiseResponseDto toFullMapResponse(List<NoiseStatDto> autoList, List<NoiseStatDto> adminList) {
    return FullMapNoiseResponseDto.builder()
      .autonomousDistricts(autoList.stream().map(NoiseMapper::toDto).collect(Collectors.toList()))
      .administrativeDistricts(adminList.stream().map(NoiseMapper::toDto).collect(Collectors.toList()))
      .build();
  }
}
