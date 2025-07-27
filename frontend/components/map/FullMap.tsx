/**
 * @file FullMap.tsx
 * @author Gahui Baek
 * @description 자치구 및 행정동 소음 데이터를 시각화하는 풀 인터랙티브 카카오맵 컴포넌트
 */

'use client';

import { useEffect, useRef, useState } from 'react';
import { loadKakaoSdk } from '@/lib/map/kakaoLoader';
import { initMapUtils } from '@/lib/map/initMapUtils';

export default function FullMap() {
  const mapRef = useRef<HTMLDivElement>(null);
  const [ready, setReady] = useState(false);

  useEffect(() => {
    loadKakaoSdk()
      .then(() => {
        initMapUtils.initFullMapUtils(); // SDK 로드된 이후에 유틸 window 등록
        setReady(true);
      })
      .catch(console.error);
  }, []);

  useEffect(() => {
    if (!ready || !mapRef.current || !window.kakao) return;

    const map = new kakao.maps.Map(mapRef.current, {
      center: window.geoUtils.getSeoulCenter(),
      level: window.geoUtils.MAX_ZOOM_LEVEL,
      zoomable: true,
      draggable: true,
      disableDoubleClickZoom: true
    });

    window.mapTileUtils.addWhiteTileOverlay(map);

    (async () => {
      const [autonomousData, administrativeData] = await Promise.all([
        window.dataLoader.fetchJson('/geojson/autonomousDistrict.json'),
        window.dataLoader.fetchJson('/geojson/administrativeDistrict.json')
      ]);
      const [autoNoise, adminNoise] = await Promise.all([
        window.dataLoader.fetchJson('/geojson/autonomousNoiseData.json'),
        window.dataLoader.fetchJson('/geojson/administrativeNoiseData.json')
      ]);

      const noiseMap = window.visualMapping.buildNoiseMap(autoNoise, adminNoise);
      const { autonomousAreas, administrativeAreas, areaCenters } =
        window.mapInitializer.initializeAreas(map, autonomousData, administrativeData, noiseMap);

      const MAX_ALLOWED_DISTANCE = window.mapInitializer.calculateMaxAllowedDistance(
        window.geoUtils.getSeoulCenter(),
        areaCenters,
        3000
      );

      kakao.maps.event.addListener(map, 'idle', () => {
        window.mapEventHandlers.handleIdle(map, autonomousAreas, MAX_ALLOWED_DISTANCE);
      });

      kakao.maps.event.addListener(map, 'zoom_changed', () => {
        const currentCenter = map.getCenter();
        window.mapEventHandlers.handleZoomChanged(map, currentCenter, MAX_ALLOWED_DISTANCE);
      });

      kakao.maps.event.addListener(map, 'mousemove', (mouseEvent: kakao.maps.event.MouseEvent) => {
        window.mapEventHandlers.handleMouseMove(
          map,
          mouseEvent.latLng,
          autonomousAreas,
          window.mapEventHandlers.polygons.polygonMap
        );
      });
    })();
  }, [ready]);

  return (
    <div className="relative w-full h-[500px]">
      <div ref={mapRef} className="w-full h-full" />
      <div className="absolute top-2.5 left-2.5 bg-white px-3 py-2 rounded-lg shadow-md text-sm z-[100] space-y-1.5">
        <div className="flex items-center gap-2">
          <div className="w-[14px] h-[14px] rounded-[3px] border border-gray-400" style={{ background: '#66BB6A' }} />
          70dB 미만
        </div>
        <div className="flex items-center gap-2">
          <div className="w-[14px] h-[14px] rounded-[3px] border border-gray-400" style={{ background: '#FFEB3B' }} />
          70~79dB
        </div>
        <div className="flex items-center gap-2">
          <div className="w-[14px] h-[14px] rounded-[3px] border border-gray-400" style={{ background: '#EF5350' }} />
          80dB 이상
        </div>
        <div className="flex items-center gap-2">
          <div className="w-[14px] h-[14px] rounded-[3px] border border-gray-400" style={{ background: '#ccc' }} />
          데이터 없음
        </div>
      </div>
    </div>
  );
}
