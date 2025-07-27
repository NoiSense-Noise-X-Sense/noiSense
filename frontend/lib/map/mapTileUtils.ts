/**
 * @file mapTileUtils.ts
 * @author Gahui Baek
 * @description 흰색 타일 오버레이를 지도에 추가하는 유틸리티
 */

/**
 * 흰 배경 타일 오버레이 추가
 * @param map 카카오맵 인스턴스
 */
function addWhiteTileOverlay(map: kakao.maps.Map): void {
  kakao.maps.Tileset.add(
    'TILE_WHITE',
    new kakao.maps.Tileset({
      width: 256,
      height: 256,
      getTile: () => {
        const div = document.createElement('div');
        div.style.background = '#ffffff';
        return div;
      }
    })
  );
  map.addOverlayMapTypeId(kakao.maps.MapTypeId.TILE_WHITE);
}

// =====================
// 전역 등록
// =====================

declare global {
  interface Window {
    mapTileUtils: {
      addWhiteTileOverlay: typeof addWhiteTileOverlay;
    };
  }
}

export const mapTileUtils = {
  addWhiteTileOverlay
};
