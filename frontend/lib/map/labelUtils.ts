/**
 * @file labelUtils.ts
 * @author Gahui Baek
 * @description Area 정보를 기반으로 라벨 오버레이를 생성하는 유틸리티
 */

interface Area {
  name: string;
  center: kakao.maps.LatLng;
}

/**
 * Area 중심 좌표에 텍스트 라벨 오버레이 생성
 * @param area 라벨을 표시할 지역 정보
 * @returns CustomOverlay 인스턴스
 */
function createLabel(area: Area): kakao.maps.CustomOverlay {
  const content = `<div class="bg-white text-xs text-gray-800 whitespace-nowrap px-[5px] py-[2px] rounded shadow">${area.name}</div>`;
  return new kakao.maps.CustomOverlay({
    position: area.center,
    content,
    yAnchor: 1,
    zIndex: 1
  });
}

// =====================
// 전역 등록
// =====================

declare global {
  interface Window {
    labelUtils: {
      createLabel: typeof createLabel;
    };
  }
}

export const labelUtils = {
  createLabel
};
