/**
 * @file mapInitializer.ts
 * @author Gahui Baek
 * @description 자치구 및 행정동 소음 데이터를 바탕으로 지도에 폴리곤과 라벨을 생성 및 등록하는 초기화 유틸리티
 */

// =====================
// 타입 정의
// =====================

interface Area {
  name: string;
  districtCode: string;
  center: kakao.maps.LatLng;
  path: kakao.maps.LatLng[];
}

interface NoiseMap {
  autonomousNoiseMap: Map<string, { avgNoise: number }>;
  administrativeNoiseMap: Map<string, { avgNoise: number }>;
}

interface GeoJsonResponse {
  data: any;
}

interface AreaCenter {
  name: string;
  districtCode: string;
  centroid: kakao.maps.LatLng;
}

// =====================
// 함수 정의
// =====================

function initializeAreas(
  map: kakao.maps.Map,
  autonomousData: GeoJsonResponse,
  administrativeData: GeoJsonResponse,
  noiseMap: NoiseMap
): {
  autonomousAreas: Area[];
  administrativeAreas: Area[];
  areaCenters: AreaCenter[];
} {
  const autonomousAreas = window.geoUtils.convertGeoJsonToAreas(autonomousData.data);
  const administrativeAreas = window.geoUtils.convertGeoJsonToAreas(administrativeData.data);

  const { polygons: autonomousPolygons, labels: autonomousLabels } =
    initializePolygonsAndLabels(autonomousAreas, map, noiseMap, false);
  const { polygons: administrativePolygons, labels: administrativeLabels } =
    initializePolygonsAndLabels(administrativeAreas, map, noiseMap, true);

  window.mapEventHandlers.polygons.autonomousPolygons = autonomousPolygons;
  window.mapEventHandlers.polygons.administrativePolygons = administrativePolygons;
  window.mapEventHandlers.labels.autonomousLabels = autonomousLabels;
  window.mapEventHandlers.labels.administrativeLabels = administrativeLabels;

  const areaCenters = calculateAreaCenters(autonomousAreas);
  return { autonomousAreas, administrativeAreas, areaCenters };
}

function initializePolygonsAndLabels(
  areas: Area[],
  map: kakao.maps.Map,
  noiseMap: NoiseMap,
  isAdministrative: boolean = false
): {
  polygons: Map<string, kakao.maps.Polygon>;
  labels: Map<string, kakao.maps.CustomOverlay>;
} {
  const polygons = new Map<string, kakao.maps.Polygon>();
  const labels = new Map<string, kakao.maps.CustomOverlay>();

  areas.forEach(area => {
    const code = area.districtCode;
    if (!code) return;

    const avgNoise = isAdministrative
      ? noiseMap.administrativeNoiseMap.get(code)?.avgNoise
      : noiseMap.autonomousNoiseMap.get(code)?.avgNoise;

    const fillColor = window.visualMapping.getFillColorByNoise(avgNoise);
    const polygon = window.polygonUtils.createPolygon(area, fillColor);
    const label = window.labelUtils.createLabel(area);

    polygons.set(code, polygon);
    labels.set(code, label);

    polygon.setMap(isAdministrative ? null : map);
    label.setMap(isAdministrative ? null : map);
  });

  return { polygons, labels };
}

function calculateAreaCenters(areas: Area[]): AreaCenter[] {
  return areas.map(area => ({
    name: area.name,
    districtCode: area.districtCode,
    centroid: area.center
  }));
}

function calculateMaxAllowedDistance(
  center: kakao.maps.LatLng,
  areaCenters: AreaCenter[],
  buffer: number = 3000
): number {
  const maxDistance = Math.max(
    ...areaCenters.map(a =>
      window.geoUtils.haversineDistance(
        center.getLat(),
        center.getLng(),
        a.centroid.getLat(),
        a.centroid.getLng()
      )
    )
  );
  return maxDistance + buffer;
}

// =====================
// 전역 등록
// =====================

declare global {
  interface Window {
    mapInitializer: {
      initializeAreas: typeof initializeAreas;
      initializePolygonsAndLabels: typeof initializePolygonsAndLabels;
      calculateAreaCenters: typeof calculateAreaCenters;
      calculateMaxAllowedDistance: typeof calculateMaxAllowedDistance;
    };
  }
}

export const mapInitializer = {
  initializeAreas,
  initializePolygonsAndLabels,
  calculateAreaCenters,
  calculateMaxAllowedDistance
};
