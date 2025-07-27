/**
 * @file visualMapping.ts
 * @author Gahui Baek
 * @description 평균 소음 수치에 따른 색상 반환 및 NoiseData 맵 생성 유틸리티
 */

// =====================
// 타입 정의
// =====================
import { models } from './models'

interface NoiseRaw {
  autonomousDistrictCode?: string | number;
  administrativeDistrictCode?: string | number;
  autonomousDistrictName?: string;
  administrativeDistrictName?: string;
  avgNoise: number;
  perceivedNoise: number;
  updatedAt: string;
}

interface NoiseData {
  districtCode?: string;
  districtName?: string;
  avgNoise: number;
  perceivedNoise: number;
  updatedAt: string;
}

// =====================
// 함수 정의
// =====================

/**
 * 평균 소음에 따라 색상 코드 반환
 * @param avgNoise dB 단위 평균 소음
 * @returns 색상 hex
 */
function getFillColorByNoise(avgNoise: number | null | undefined): string {
  if (avgNoise == null) return '#ccc';
  if (avgNoise < 70) return '#66BB6A';
  if (avgNoise < 80) return '#FFEB3B';
  return '#EF5350';
}

/**
 * 소음 데이터 배열을 Map 형태로 변환
 * @param autonomousNoiseData 자치구 단위 raw 배열
 * @param administrativeNoiseData 행정동 단위 raw 배열
 * @returns 각 구분별 noiseMap 객체
 */
function buildNoiseMap(
  autonomousNoiseData?: NoiseRaw[],
  administrativeNoiseData?: NoiseRaw[]
): {
  autonomousNoiseMap: Map<string, NoiseData>;
  administrativeNoiseMap: Map<string, NoiseData>;
} {
  const autonomousNoiseMap = new Map<string, NoiseData>();
  const administrativeNoiseMap = new Map<string, NoiseData>();

  if (autonomousNoiseData) {
    autonomousNoiseData.forEach(item => {
      const noise = new models.NoiseData(item, 'autonomous');
      if (noise.districtCode) {
        autonomousNoiseMap.set(noise.districtCode, noise);
      }
    });
  }

  if (administrativeNoiseData) {
    administrativeNoiseData.forEach(item => {
      const noise = new models.NoiseData(item, 'administrative');
      if (noise.districtCode) {
        administrativeNoiseMap.set(noise.districtCode, noise);
      }
    });
  }

  return { autonomousNoiseMap, administrativeNoiseMap };
}

// =====================
// 전역 등록
// =====================

declare global {
  interface Window {
    visualMapping: {
      getFillColorByNoise: typeof getFillColorByNoise;
      buildNoiseMap: typeof buildNoiseMap;
    };
  }
}

export const visualMapping = {
  getFillColorByNoise,
  buildNoiseMap
};
