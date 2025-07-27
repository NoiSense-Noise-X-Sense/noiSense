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
  autonomousNoiseMap: Map<string, { avgNoise: number, perceivedNoise: number }>;
  administrativeNoiseMap: Map<string, { avgNoise: number, perceivedNoise: number }>;
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
  autonomousData?: GeoJsonResponse | null,
  administrativeData?: GeoJsonResponse | null,
  noiseMap: NoiseMap
): {
  autonomousAreas: Area[];
  administrativeAreas: Area[];
  areaCenters: AreaCenter[];
} {
  const autonomousAreas = autonomousData?.data
    ? window.geoUtils.convertGeoJsonToAreas(autonomousData.data)
    : [];

  const administrativeAreas = administrativeData?.data
    ? window.geoUtils.convertGeoJsonToAreas(administrativeData.data)
    : [];

  const autonomousInit = autonomousAreas.length ?
    initializePolygonsAndLabels(autonomousAreas, map, noiseMap, false)
    : null;
    // { polygons: [], labels: [] };

  const administrativeInit = administrativeAreas.length ?
    initializePolygonsAndLabels(administrativeAreas, map, noiseMap, true)
    : null;
    // { polygons: [], labels: [] };

  if (autonomousInit?.polygons) {
    window.mapEventHandlers.polygons.autonomousPolygons = autonomousInit.polygons;
    window.mapEventHandlers.labels.autonomousLabels = autonomousInit.labels;
    window.mapEventHandlers.noises.autonomousNoises = noiseMap.autonomousNoiseMap;
  }

  if (administrativeInit?.polygons) {
    window.mapEventHandlers.polygons.administrativePolygons = administrativeInit.polygons;
    window.mapEventHandlers.labels.administrativeLabels = administrativeInit.labels;
    window.mapEventHandlers.noises.administrativeNoises = noiseMap.administrativeNoiseMap;
  }

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



    const rawNoise = isAdministrative
      ? noiseMap.administrativeNoiseMap?.get(code)
      : noiseMap.autonomousNoiseMap?.get(code);

    if (rawNoise) {
      const noiseInstance = new window.models.NoiseData(
        rawNoise,
        isAdministrative ? 'administrative' : 'autonomous'
      );
      area.noise = noiseInstance; // ★ noise를 area에 주입
    }

    const avgNoise = rawNoise?.avgNoise;
    // const avgNoise = isAdministrative
    //   ? noiseMap.administrativeNoiseMap?.get(code)?.avgNoise
    //   : noiseMap.autonomousNoiseMap?.get(code)?.avgNoise;
    //
    // const perceivedNoise = isAdministrative
    //   ? noiseMap.administrativeNoiseMap?.get(code)?.perceivedNoise
    //   : noiseMap.autonomousNoiseMap?.get(code)?.perceivedNoise;

    const fillColor = window.visualMapping.getFillColorByNoise(avgNoise);
    const polygon = window.polygonUtils.createPolygon(area, fillColor);
    const label = window.labelUtils.createLabel(area);

    // const autoNoiseInstance = new window.models.NoiseData(noiseMap.autonomousNoiseMap, 'autonomous');
    // const adminNoiseInstance = new window.models.NoiseData(noiseMap.administrativeNoiseMap, 'administrative');
    // console.log(autoNoiseInstance.districtCode, autoNoiseInstance.districtName autoNoiseInstance.avgNoise, autoNoiseInstance.perceivedNoise);

    // // 맵을 만든다.
    // // 키는 districtCode, 값은 avgNoise,
    // const infoContent = createInfo(
    //   area.districtCode
    //   , area.name
    //   ,avgNoise
    //   , perceivedNoise);

    polygons.set(code, polygon);
    labels.set(code, label);
    // noises.set(code, infoContent);

    polygon.setMap(isAdministrative ? null : map);
    label.setMap(isAdministrative ? null : map);
    // noises.setMap(isAdministrative ? null : map);
  });

  return { polygons, labels };
}

function createInfo(code: string, name: string, avgNoise:number, perceivedNoise:number) {
  return {code, name, avgNoise, perceivedNoise};
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
  calculateMaxAllowedDistance,
};
