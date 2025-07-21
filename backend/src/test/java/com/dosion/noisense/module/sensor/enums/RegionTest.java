package com.dosion.noisense.module.sensor.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegionTest {


  @DisplayName("경게 구분을 Key로 검색하면 enum을 반환한다")
  @Test
  void region_args_key_Test() {
    // given

    // when
    Region from = Region.fromKey("ROADS_AND_PARKS");

    // then
    assertEquals(Region.ROADS_AND_PARKS, from);
  }

  @DisplayName("경게 구분을 영문명으로 검색하면 enum을 반환한다")
  @Test
  void region_args_nameEn_Test() {
    // given

    // when
    Region from = Region.fromNameEn("industrial_area");

    // then
    assertEquals(Region.INDUSTRIAL_AREA, from);
  }

  @DisplayName("경게 구분을 국문명으로 검색하면 enum을 반환한다")
  @Test
  void region_args_nameKo_Test() {
    // given

    // when
    Region from = Region.fromNameKo("공공시설");

    // then
    assertEquals(Region.PUBLIC_FACILITIES, from);

  }


}
