package com.dosion.noisense.module.geodata.fixture;

import com.dosion.noisense.module.district.entity.District;
import com.dosion.noisense.module.geodata.entity.BoundaryPolygon;
import com.dosion.noisense.module.geodata.enums.GeometryType;
import com.dosion.noisense.module.sensor.enums.BoundaryType;
import com.dosion.noisense.web.geodata.dto.BoundaryPolygonDto;

import java.util.List;

import static com.dosion.noisense.common.util.JsonUtilTest.toJsonNode;
import static com.dosion.noisense.module.geodata.enums.GeometryFormat.GeoJSON;

public class GeodataFixture {

  public static List<District> getAutonomousDistricts() {
    return List.of(
      District.builder().autonomousDistrictCode("11010").autonomousDistrictNameEn("Jongno-gu").autonomousDistrictNameKo("종로구").build(),
      District.builder().autonomousDistrictCode("11020").autonomousDistrictNameEn("Jung-gu").autonomousDistrictNameKo("중구").build(),
      District.builder().autonomousDistrictCode("11030").autonomousDistrictNameEn("Yongsan-gu").autonomousDistrictNameKo("용산구").build()
    );
  }

  public static List<District> getAdministrativeDistricts() {
    return List.of(
      District.builder().administrativeDistrictCode("11010530").administrativeDistrictNameKo("사직동").administrativeDistrictNameEn("Sajik-dong").parentDistrictCode("11010").build(),
      District.builder().administrativeDistrictCode("11010540").administrativeDistrictNameKo("삼청동").administrativeDistrictNameEn("Samcheong-dong").parentDistrictCode("11010").build(),
      District.builder().administrativeDistrictCode("11010550").administrativeDistrictNameKo("부암동").administrativeDistrictNameEn("Buam-dong").parentDistrictCode("11010").build()
    );
  }

  public static List<BoundaryPolygon> getBoundaryPolygons() {
    return List.of(
      BoundaryPolygon.builder().boundaryPolygonId(1L).autonomousDistrict("11060").boundaryType(BoundaryType.AUTONOMOUS_DISTRICT).geometryFormat(GeoJSON).geometryType(GeometryType.MultiPolygon).geometryCoordinate(toJsonNode("[[[[206125.5336832993,556523.929393474],[206126.01199087722,556434.116097902]]]]")).build(),
      BoundaryPolygon.builder().boundaryPolygonId(2L).autonomousDistrict("11070").boundaryType(BoundaryType.AUTONOMOUS_DISTRICT).geometryFormat(GeoJSON).geometryType(GeometryType.MultiPolygon).geometryCoordinate(toJsonNode("[[[[209826.33645053432,557905.3167482152],[209881.5303268841,557873.5327106739],[209900.3525923023,557862.6933919518]]]]")).build(),
      BoundaryPolygon.builder().boundaryPolygonId(3L).autonomousDistrict("11080").boundaryType(BoundaryType.AUTONOMOUS_DISTRICT).geometryFormat(GeoJSON).geometryType(GeometryType.MultiPolygon).geometryCoordinate(toJsonNode("[[[[198584.19091137382,559647.2756913631],[198605.31397907215,559636.1141751622],[198605.6207541241,559636.4228293214]]]]")).build()
    );
  }

  public static List<BoundaryPolygonDto> getBoundaryPolygonDto() {
    return List.of(
      BoundaryPolygonDto.builder()
        .boundaryPolygonId(1L)
        .district(null)
        .boundaryType(BoundaryType.AUTONOMOUS_DISTRICT)
        .geometryFormat(GeoJSON)
        .geometry(BoundaryPolygonDto.Geometry.builder()
          .geometryType(GeometryType.MultiPolygon)
          .coordinates(toJsonNode("[[[[206125.5336832993,556523.929393474],[206126.01199087722,556434.116097902]]]]"))
          .build())
        .build(),
      BoundaryPolygonDto.builder()
        .boundaryPolygonId(2L)
        .district(null)
        .boundaryType(BoundaryType.AUTONOMOUS_DISTRICT)
        .geometryFormat(GeoJSON)
        .geometry(BoundaryPolygonDto.Geometry.builder()
          .geometryType(GeometryType.MultiPolygon)
          .coordinates(toJsonNode("[[[[198584.19091137382,559647.2756913631],[198605.31397907215,559636.1141751622],[198605.6207541241,559636.4228293214]]]]"))
          .build())
        .build()
    );
  }

}
