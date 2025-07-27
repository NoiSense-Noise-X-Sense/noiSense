/**
 * @file dataLoader.ts
 * @author Gahui Baek
 * @description fetch 기반으로 JSON 데이터를 비동기 로드하는 유틸리티
 */

// =====================
// 상수
// =====================

const DEFAULT_OPTIONS: RequestInit = {
  headers: {
    'Content-Type': 'application/json'
  }
};

// =====================
// 함수
// =====================

/**
 * 지정된 URL로부터 JSON 데이터를 비동기적으로 로드
 * @param url JSON 파일의 경로 또는 API 엔드포인트
 * @returns 파싱된 JSON 객체
 */
async function fetchJson<T = unknown>(url: string): Promise<T> {
  const res = await fetch(url, DEFAULT_OPTIONS);
  if (!res.ok) throw new Error(`데이터 로딩 실패: ${url}`);
  return await res.json();
}

// =====================
// 전역 등록
// =====================

declare global {
  interface Window {
    dataLoader: {
      fetchJson: <T = unknown>(url: string) => Promise<T>;
    };
  }
}

export const dataLoader = {
  fetchJson
};
