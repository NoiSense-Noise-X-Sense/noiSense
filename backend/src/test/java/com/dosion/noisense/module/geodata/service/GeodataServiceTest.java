package com.dosion.noisense.module.geodata.service;

import com.dosion.noisense.common.util.JsonUtilTest;
import com.dosion.noisense.module.geodata.entity.BoundaryPolygon;
import com.dosion.noisense.module.geodata.enums.GeometryType;
import com.dosion.noisense.module.geodata.repository.BoundaryPolygonRepository;
import com.dosion.noisense.web.geodata.dto.BoundaryPolygonDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Disabled // TODO: 왜 실패해
@ExtendWith(MockitoExtension.class)
class GeodataServiceTest {

  @Mock
  private BoundaryPolygonRepository boundaryPolygonRepository;

@InjectMocks
  private GeodataService geodataService;

  @Test
  void getAllPolygons_shouldReturnDtoList() {
//    // given
//    BoundaryPolygon polygon = getBoundaryPolygons().get(0);
    BoundaryPolygon polygon = mock(BoundaryPolygon.class);
//
    given(boundaryPolygonRepository.findAll())
      .willReturn(List.of(polygon));
//

    BoundaryPolygonDto polygonDto = mock(BoundaryPolygonDto.class);
////    given(polygon.toGeometryDto()).willReturn(mock(BoundaryPolygonDto.Geometry.class));
////    BoundaryPolygonDto.Geometry geometryDto = polygon.toGeometryDto();
////    given(geometryDto.getGeometryType()).willReturn(GeometryType.MultiPolygon);
////    given(geometryDto.getCoordinates()).willReturn(StringUtils.collectionToCommaDelimitedString(List.of("100,100", "200,200")));

//    BoundaryPolygonDto polygonDto = BoundaryPolygonDto.builder()
//      .boundaryPolygonId(1L)
//      .boundaryType(BoundaryType.AUTONOMOUS_DISTRICT)
//      .geometryFormat(GeometryFormat.GeoJSON)
//      .geometry(BoundaryPolygonDto.Geometry.builder().geometryType(GeometryType.MultiPolygon).coordinates(StringUtils.collectionToCommaDelimitedString(List.of("100,100", "200,200"))).build())
//      .build();

    when(polygonDto.getGeometry()).thenReturn(mock(BoundaryPolygonDto.Geometry.class));
    when(polygonDto.getGeometry().getGeometryType()).thenReturn(GeometryType.valueOf(GeometryType.MultiPolygon.toString()));
    when(polygonDto.getGeometry().getCoordinates()).thenReturn(JsonUtilTest.toJsonNode(StringUtils.collectionToCommaDelimitedString(List.of("100,100", "200,200"))));
//    when(BoundaryPolygon.toGeometryDto(polygon)).thenReturn(mock(BoundaryPolygonDto.Geometry.class));
//    when(GeometryType.from("MultiPolygon")).thenReturn(GeometryType.MultiPolygon);
//    when(polygon.getGeometryType()).thenReturn(String.valueOf(GeometryType.MultiPolygon));
    when(polygon.getGeometryCoordinate()).thenReturn(JsonUtilTest.toJsonNode(StringUtils.collectionToCommaDelimitedString(List.of("100,100", "200,200"))));


//    when(polygon.()).thenReturn(Geo);

    given(geodataService.getAutonomousPolygons()).willReturn(List.of(BoundaryPolygonDto.builder().boundaryPolygonId(1L).build()));

    // when
//    assertThat(geodataService.getAllPolygons()).hasSize(1);


//    List<BoundaryPolygonDto> result = geodataService.getAllPolygons();

    // then
//    assertThat(result).hasSize(1);
//
//    assertThat(result.get(0).getDistrict()).extracting(
//      BoundaryPolygonDto.DistrictDto::getDistrictCode, BoundaryPolygonDto.DistrictDto::getDistrictName, BoundaryPolygonDto.DistrictDto::isHasAutonomousDistrictCode, BoundaryPolygonDto.DistrictDto::getAutonomousDistrictCode)
//      .contains(anyString(), anyString(), anyString(), anyString());
//
//    assertThat(result.get(0)).extracting(
//      BoundaryPolygonDto::getBoundaryPolygonId, BoundaryPolygonDto::getBoundaryType, BoundaryPolygonDto::getGeometryFormat)
//      .contains(anyLong(), anyString(), anyString());
  }

//  @Test
//  void getAdminPolygons_shouldReturnDtoList() {
//    // given
//    AutonomousDistrict district1 = getAutonomousDistricts().get(0);
//    AutonomousDistrict district2 = getAutonomousDistricts().get(1);
//    given(autonomousDistrictRepository.findAll()).willReturn(List.of(district1, district2));
//
//    BoundaryPolygon polygon1 = mock(BoundaryPolygon.class);
//    BoundaryPolygon polygon2 = mock(BoundaryPolygon.class);
//    given(boundaryPolygonRepository.findByAdministrativeDistrictCode(district1.getCode(), null)).willReturn(polygon1);
//    given(boundaryPolygonRepository.findByAdministrativeDistrictCode(district2.getCode(), null)).willReturn(polygon2);
//
////    BoundaryPolygonDto dto1 = getBoundaryPolygonDto().get(0);
////    BoundaryPolygonDto dto2 = getBoundaryPolygonDto().get(1);
//    BoundaryPolygonDto dto1 = mock(BoundaryPolygonDto.class);
//    BoundaryPolygonDto dto2 = mock(BoundaryPolygonDto.class);
//    given(polygon1.toBoundaryPolygonDto()).willReturn(dto1);
//    given(polygon2.toBoundaryPolygonDto()).willReturn(dto2);
//
//    // when
//    List<BoundaryPolygonDto> result = geodataService.getAutonomousPolygons();
//
//    BoundaryPolygonDto.Geometry geometry = BoundaryPolygonDto.Geometry.builder()
//      .geometryType(GeometryType.MultiPolygon)
//      .coordinates(polygon1.getCoordinate())
//      .build();
//
//    // then
//    assertAll(
//      () -> assertThat(result).hasSize(2)
////      () -> assertThat(result.get(0).getGeometry()).isInstanceOf(BoundaryPolygonDto.Geometry.class),
////      () -> assertThat(result.get(0).getGeometry().getGeometryType()).isInstanceOf(GeometryType.class),
////      () -> assertThat(result.get(0).getBoundaryType()).isInstanceOf(BoundaryType.class),
////      () -> assertThat(result.get(0))
////        .extracting(
////          BoundaryPolygonDto::getBoundaryPolygonId
////          , BoundaryPolygonDto::getBoundaryType, BoundaryPolygonDto::getGeometryFormat
////          , BoundaryPolygonDto::getAutonomousDistrict, BoundaryPolygonDto::getAdministrativeDistrict
////        ).contains(1L, BoundaryType.AUTONOMOUS_DISTRICT, GeometryFormat.GeoJSON
////          , district1.getCode(), null)
//    );
//  }

}
