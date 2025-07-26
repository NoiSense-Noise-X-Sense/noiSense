/**
 * @file geoUtils.ts
 * @author Gahui Baek
 * @description 카카오맵 관련 지리 유틸리티 함수 모음
 */

// =====================
// 상수
// =====================
const MAX_ZOOM_LEVEL = 9;
const MIN_ZOOM_LEVEL = 6;

// =====================
// 함수
// =====================

/**
 * 서울 중심 좌표를 반환
 */
function getSeoulCenter(): kakao.maps.LatLng {
  return new kakao.maps.LatLng(37.5665, 126.9780);
}

/**
 * 두 좌표 간 거리 계산 (미터 단위)
 */
function haversineDistance(
  lat1: number,
  lng1: number,
  lat2: number,
  lng2: number
): number {
  const R = 6371e3;
  const toRad = (deg: number) => (deg * Math.PI) / 180;

  const dLat = toRad(lat2 - lat1);
  const dLng = toRad(lng2 - lng1);

  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLng / 2) ** 2;

  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

/**
 * 서울 중심으로부터의 거리 초과 여부
 */
function isTooFarFromSeoul(center: kakao.maps.LatLng, maxDistance: number): boolean {
  const d = haversineDistance(
    center.getLat(),
    center.getLng(),
    getSeoulCenter().getLat(),
    getSeoulCenter().getLng()
  );
  return d > maxDistance;
}

/**
 * 가장 가까운 중심 좌표를 가진 지역을 찾음
 */
function findNearestAreaCenter(
  point: kakao.maps.LatLng,
  areaList: { center: kakao.maps.LatLng; districtCode: string; name: string }[]
) {
  let nearest = null;
  let minDistance = Infinity;

  areaList.forEach(area => {
    const dist = haversineDistance(
      point.getLat(),
      point.getLng(),
      area.center.getLat(),
      area.center.getLng()
    );
    if (dist < minDistance) {
      minDistance = dist;
      nearest = area;
    }
  });

  return nearest;
}

/**
 * GeoJSON 데이터를 Area 객체 리스트로 변환
 */
function convertGeoJsonToAreas(data: GeoJsonItem[]): Area[] {
  const areaList: Area[] = [];

  data.forEach(item => {
    const name = item.district.districtNameKo;
    const districtCode = item.district.districtCode;
    const multiPolygons = item.geometry.coordinates;
    const centroid = item.geometry.centroid;

    multiPolygons.forEach(polygonGroup => {
      polygonGroup.forEach(polygon => {
        const coords = polygon.map(([lat, lng]) => ({ lat, lng }));
        const area = new (window as any).models.Area(name, districtCode, coords, centroid);
        areaList.push(area);
      });
    });
  });

  return areaList;
}

// =====================
// 전역 등록
// =====================

declare global {
  interface Window {
    geoUtils: {
      MAX_ZOOM_LEVEL: number;
      MIN_ZOOM_LEVEL: number;
      getSeoulCenter: () => kakao.maps.LatLng;
      haversineDistance: typeof haversineDistance;
      isTooFarFromSeoul: typeof isTooFarFromSeoul;
      findNearestAreaCenter: typeof findNearestAreaCenter;
      convertGeoJsonToAreas: typeof convertGeoJsonToAreas;
    };
  }
}

export const geoUtils = {
  MAX_ZOOM_LEVEL,
  MIN_ZOOM_LEVEL,
  getSeoulCenter,
  haversineDistance,
  isTooFarFromSeoul,
  findNearestAreaCenter,
  convertGeoJsonToAreas,
};
