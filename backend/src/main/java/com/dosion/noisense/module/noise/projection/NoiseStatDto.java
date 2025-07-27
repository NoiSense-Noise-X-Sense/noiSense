package com.dosion.noisense.module.noise.projection;

import org.springframework.lang.Nullable;

public record NoiseStatDto(

  String autonomousDistrictCode,
  String autonomousDistrictKor,
  String autonomousDistrictEng,

  @Nullable String administrativeDistrictCode,
  @Nullable String administrativeDistrictKor,
  @Nullable String administrativeDistrictEng,

  Double avgNoise,
  Double perceivedNoise
) {}
