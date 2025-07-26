/**
 * @file models.ts
 * @author Gahui Baek
 * @description Area 및 NoiseData 모델 클래스 정의
 */

// =====================
// 타입 선언
// =====================

interface LatLngCoord {
  lat: number;
  lng: number;
}

interface Centroid {
  x: number; // 경도
  y: number; // 위도
}

interface NoiseRaw {
  autonomousDistrictCode?: string | number;
  administrativeDistrictCode?: string | number;
  autonomousDistrictName?: string;
  administrativeDistrictName?: string;
  avgNoise: number;
  perceivedNoise: number;
  updatedAt: string;
}

// =====================
// 클래스 정의
// =====================

class Area {
  name: string;
  districtCode: string;
  path: kakao.maps.LatLng[];
  center: kakao.maps.LatLng;
  polygon: kakao.maps.Polygon;

  constructor(
    districtName: string,
    districtCode: string,
    coords: LatLngCoord[],
    centroid: Centroid
  ) {
    this.name = districtName;
    this.districtCode = districtCode;
    this.path = coords.map(coord => new kakao.maps.LatLng(coord.lat, coord.lng));
    this.center = new kakao.maps.LatLng(centroid.y, centroid.x);

    this.polygon = new kakao.maps.Polygon({
      path: this.path,
      strokeWeight: 1,
      strokeColor: '#424242',
      strokeOpacity: 0.8,
      fillColor: '#ccc',
      fillOpacity: 0.5
    });

    this.polygon.setMap(null); // 초기에는 숨김
  }
}

class NoiseData {
  districtCode?: string;
  districtName?: string;
  avgNoise: number;
  perceivedNoise: number;
  updatedAt: string;

  constructor(raw: NoiseRaw, type: 'autonomous' | 'administrative') {
    this.districtCode =
      type === 'autonomous'
        ? raw.autonomousDistrictCode?.toString()
        : raw.administrativeDistrictCode?.toString();
    this.districtName =
      type === 'autonomous'
        ? raw.autonomousDistrictName
        : raw.administrativeDistrictName;

    this.avgNoise = raw.avgNoise;
    this.perceivedNoise = raw.perceivedNoise;
    this.updatedAt = raw.updatedAt;
  }
}

// =====================
// 전역 등록
// =====================

declare global {
  interface Window {
    models: {
      Area: typeof Area;
      NoiseData: typeof NoiseData;
    };
  }
}

export const models = {
  Area,
  NoiseData
};
