package com.dosion.noisense.module.geodata.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeometryTypeTest {

  @DisplayName("key가 존재하면 enum을, 존재하지 않으면 null을 반환한다")
  @Test
  void multiPolygonTest() {
    // given

    // when
    GeometryType from1 = GeometryType.from("MultiPolygon");
    GeometryType from2 = GeometryType.from("Polygon");
    GeometryType from3 = GeometryType.from("qwer");

    // then
    assertAll(
      () -> assertEquals(GeometryType.MultiPolygon, from1),
      () -> assertEquals(GeometryType.Polygon, from2),
      () -> assertNull(from3)
    );
  }

}
