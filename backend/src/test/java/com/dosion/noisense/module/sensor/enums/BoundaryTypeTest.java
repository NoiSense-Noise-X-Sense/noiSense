package com.dosion.noisense.module.sensor.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoundaryTypeTest {


  @DisplayName("경게 구분을 Key로 검색하면 Enum이 존재한다.")
  @Test
  void boundaryType_args_key_Test() {
    // given

    // when
    BoundaryType from = BoundaryType.fromKey("AUTONOMOUS_DISTRICT");

    // then
    assertEquals(BoundaryType.AUTONOMOUS_DISTRICT, from);
  }

  @DisplayName("경게 구분을 영문명으로 검색하면 Enum이 존재한다.")
  @Test
  void boundaryType_args_nameEn_Test() {
    // given

    // when
    BoundaryType from = BoundaryType.fromNameEn("AUTONOMOUS_DISTRICT");

    // then
    assertEquals(BoundaryType.AUTONOMOUS_DISTRICT, from);
  }

  @DisplayName("경게 구분을 국문명으로 검색하면 Enum이 존재한다.")
  @Test
  void boundaryType_args_nameKo_Test() {
    // given

    // when
    BoundaryType from = BoundaryType.fromNameKo("행정동");

    // then
    assertEquals(BoundaryType.ADMINISTRATIVE_DISTRICT, from);

  }

}
