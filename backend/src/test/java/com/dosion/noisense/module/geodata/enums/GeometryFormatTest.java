package com.dosion.noisense.module.geodata.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeometryFormatTest {

  @DisplayName("key가 존재하면 enum을, 존재하지 않으면 null을 반환한다")
  @Test
  void given_exist_enum_then_return_enum_test() {
    // given

    // when
    GeometryFormat from1 = GeometryFormat.fromKey("GeoJSON");
    GeometryFormat from2 = GeometryFormat.fromKey("JSON");

    // then
    assertAll(
      () -> assertEquals(GeometryFormat.GeoJSON, from1),
      () -> assertNull(from2)
    );
  }
}
