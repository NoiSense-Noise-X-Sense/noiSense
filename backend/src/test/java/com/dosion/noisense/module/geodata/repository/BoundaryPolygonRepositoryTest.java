package com.dosion.noisense.module.geodata.repository;

import com.dosion.noisense.module.geodata.entity.BoundaryPolygon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.dosion.noisense.common.util.JsonUtilTest.toJsonNode;
import static com.dosion.noisense.module.geodata.enums.GeometryFormat.GeoJSON;
import static com.dosion.noisense.module.geodata.enums.GeometryType.MultiPolygon;
import static com.dosion.noisense.module.geodata.fixture.GeodataFixture.getBoundaryPolygons;
import static com.dosion.noisense.module.sensor.enums.BoundaryType.AUTONOMOUS_DISTRICT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class BoundaryPolygonRepositoryTest {

  @Mock
  private BoundaryPolygonRepository boundaryPolygonRepository;

  List<BoundaryPolygon> data;


  @BeforeEach
  void setUp() {
    data = getBoundaryPolygons();
  }

  @Test
  void test_autonomousDistrict_findAll_success_if_all_data_exists_in_db_then_return_all_data_in_db() {
    // given stub
    BDDMockito.given(boundaryPolygonRepository.findAll()).willReturn(data);

    List<BoundaryPolygon> result = boundaryPolygonRepository.findAll();

    //. then
    assertThat(result).hasSize(3);
    assertAll(
      () -> assertThat(result.get(0)).extracting(
        BoundaryPolygon::getBoundaryPolygonId
          , BoundaryPolygon::getAutonomousDistrict
          , BoundaryPolygon::getAdministrativeDistrict
          , BoundaryPolygon::getBoundaryType
          , BoundaryPolygon::getGeometryType
          , BoundaryPolygon::getGeometryFormat
          , BoundaryPolygon::getGeometryCoordinate)
        .contains(1L, "11060", null, AUTONOMOUS_DISTRICT, MultiPolygon, GeoJSON, toJsonNode("[[[[206125.5336832993,556523.929393474],[206126.01199087722,556434.116097902]]]]")),
      () -> assertThat(result.get(1)).extracting(
        BoundaryPolygon::getBoundaryPolygonId
        , BoundaryPolygon::getAutonomousDistrict
        , BoundaryPolygon::getAdministrativeDistrict
        , BoundaryPolygon::getBoundaryType
        , BoundaryPolygon::getGeometryType
        , BoundaryPolygon::getGeometryFormat
        , BoundaryPolygon::getGeometryCoordinate)
        .contains(2L, "11070", null, AUTONOMOUS_DISTRICT, MultiPolygon, GeoJSON, toJsonNode("[[[[209826.33645053432,557905.3167482152],[209881.5303268841,557873.5327106739],[209900.3525923023,557862.6933919518]]]]")),
    () -> assertThat(result.get(2)).extracting(
      BoundaryPolygon::getBoundaryPolygonId
      , BoundaryPolygon::getAutonomousDistrict
      , BoundaryPolygon::getAdministrativeDistrict
      , BoundaryPolygon::getBoundaryType
      , BoundaryPolygon::getGeometryType
      , BoundaryPolygon::getGeometryFormat
      , BoundaryPolygon::getGeometryCoordinate)
      .contains(3L, "11080", null, AUTONOMOUS_DISTRICT, MultiPolygon, GeoJSON, toJsonNode("[[[[198584.19091137382,559647.2756913631],[198605.31397907215,559636.1141751622],[198605.6207541241,559636.4228293214]]]]"))
    );
  }

}
