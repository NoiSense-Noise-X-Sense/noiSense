package com.dosion.noisense.module.datamap.entity;

import com.dosion.noisense.module.geodata.entity.BoundaryPolygon;
import com.dosion.noisense.module.geodata.enums.GeometryFormat;
import com.dosion.noisense.module.geodata.enums.GeometryType;
import com.dosion.noisense.module.sensor.enums.BoundaryType;
import com.dosion.noisense.web.district.dto.DistrictDto;
import com.dosion.noisense.web.geodata.dto.BoundaryPolygonDto;
import com.dosion.noisense.web.geodata.dto.BoundaryPolygonDto.Geometry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.dosion.noisense.common.util.JsonUtilTest.toJsonNode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class BoundaryPolygonTest {

  @DisplayName("BoundaryPolygon을 BoundaryPolygonDto로 변환할 수 있다.")
  @Test
  void toBoundaryPolygonDtoTest() {
    // given: Entity
    final BoundaryPolygon boundaryPolygon = generateBoundaryPolygon();

    // when: toDto
    DistrictDto districtDto = DistrictDto.builder()
      .hasAutonomousDistrictCode(false)
      .districtCode(boundaryPolygon.getAutonomousDistrict())
      .autonomousDistrictCode(null)
      .districtNameEn("What-gu")
      .districtNameKo("무슨 구")
      .build();

    BoundaryPolygonDto polygonDto = BoundaryPolygonDto.builder()
      .district(districtDto)
      .geometryFormat(boundaryPolygon.getGeometryFormat())
      .boundaryType(boundaryPolygon.getBoundaryType())
      .boundaryPolygonId(1L)
      .geometry(BoundaryPolygon.toGeometryDto(boundaryPolygon))
      .build();

    // then
    assertAll(
      () -> assertThat(polygonDto.getDistrict()).extracting(
        DistrictDto::getDistrictCode, DistrictDto::getDistrictNameEn, DistrictDto::getDistrictNameKo, DistrictDto::isHasAutonomousDistrictCode, DistrictDto::getAutonomousDistrictCode
      ).contains("11060", "What-gu", "무슨 구", false, null),

      () -> assertThat(polygonDto).extracting(
        BoundaryPolygonDto::getBoundaryPolygonId
        ,BoundaryPolygonDto::getGeometryFormat
        , BoundaryPolygonDto::getBoundaryType
      ).contains(1L, GeometryFormat.GeoJSON, BoundaryType.AUTONOMOUS_DISTRICT)

      , () -> assertThat(polygonDto.getGeometry()).extracting(
          Geometry::getGeometryType,
          Geometry::getCoordinates)
        .contains(GeometryType.MultiPolygon
          , toJsonNode("[[[[204531.47076072573,555728.2664946103],[204558.03973446076,555697.8914101887],[204558.05276319708,555697.8766260077]" +
            ",[204567.01469351168,555687.6309262416],[204572.4370195482,555665.2014242801],[204573.6641217806,555660.1254498911]" +
            ",[204600.75054789186,555620.4223300604],[204594.93240312062,555618.8151275992],[204601.6614379808,555609.2753732953]" +
            ",[204606.16114361628,555602.7999378574]]]]")
        )
    );

  }


  private BoundaryPolygon generateBoundaryPolygon() {
    return BoundaryPolygon.builder()
      .boundaryPolygonId(1L)
      .autonomousDistrict("11060") // 동대문구
      .administrativeDistrict("11060710") // 회기동
      .geometryFormat(GeometryFormat.GeoJSON)
      .boundaryType(BoundaryType.AUTONOMOUS_DISTRICT)
      .geometryType(GeometryType.MultiPolygon)
      .geometryCoordinate(toJsonNode("[[[[204531.47076072573,555728.2664946103],[204558.03973446076,555697.8914101887],[204558.05276319708,555697.8766260077]" +
        ",[204567.01469351168,555687.6309262416],[204572.4370195482,555665.2014242801],[204573.6641217806,555660.1254498911]" +
        ",[204600.75054789186,555620.4223300604],[204594.93240312062,555618.8151275992],[204601.6614379808,555609.2753732953]" +
        ",[204606.16114361628,555602.7999378574]]]]"))
      .build();
  }
}
