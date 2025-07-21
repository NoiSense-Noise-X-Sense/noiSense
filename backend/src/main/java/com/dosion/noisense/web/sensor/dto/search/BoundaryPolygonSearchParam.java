package com.dosion.noisense.web.sensor.dto.search;

import com.dosion.noisense.module.sensor.enums.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 경계 좌표 검색 조건을 담는 파라미터 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoundaryPolygonSearchParam {
  private String autonomousDistrict;       // 자치구
  private String administrativeDistrict;   // 행정동 (nullable 가능)
  private String boundaryType; // 자치구, 행정동
  private Region region; // 지역 유형
}
