/**
 * @file polygonUtils.ts
 * @author Gahui Baek
 * @description Area 객체로부터 카카오맵 Polygon을 생성하고 스타일을 정의하는 유틸리티
 */

interface Area {
  name: string;
  path: kakao.maps.LatLng[];
}

/**
 * 주어진 Area 정보로 다각형을 생성
 * @param area 다각형으로 표현할 지역 정보
 * @param color 채우기 색상 (기본값: #ccc)
 * @returns Polygon 인스턴스
 */
function createPolygon(area: Area, color: string = '#ccc'): kakao.maps.Polygon {
  return new kakao.maps.Polygon({
    name: area.name,
    path: area.path,
    strokeWeight: 1,
    strokeColor: '#424242',
    strokeOpacity: 0.8,
    fillColor: color,
    fillOpacity: 0.5
  });
}

const polygonStyles = {
  default: {
    strokeWeight: 1,
    strokeColor: '#424242'
  },
  highlight: {
    strokeWeight: 4,
    strokeColor: '#000000'
  }
} as const;

// =====================
// 전역 등록
// =====================

declare global {
  interface Window {
    polygonUtils: {
      createPolygon: typeof createPolygon;
      polygonStyles: typeof polygonStyles;
    };
  }
}

export const polygonUtils = {
  createPolygon,
  polygonStyles
};
