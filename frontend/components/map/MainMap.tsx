/**
 * @file MainMap.tsx
 * @author Gahui Baek
 * @description 자치구 소음 데이터를 시각화하는 카카오맵 컴포넌트
 */

'use client';

import { useEffect, useRef, useState } from 'react';
import { loadKakaoSdk } from '@/lib/map/kakaoLoader';
import { initMapUtils } from '@/lib/map/initMapUtils';

type Props = {
  onDistrictClick: (districtCode: string) => void;
};

export default function MainMap({ onDistrictClick }: Props) {
  const mapRef = useRef<HTMLDivElement>(null);
  const [ready, setReady] = useState(false);

  const infoOverlayRef = useRef(null);
  const infoTitleRef = useRef(null);
  const infoNoiseRef = useRef(null);
  const infoSubRef = useRef(null);

  function formatDate(isoString: string): string {
    const date = new Date(isoString);
    return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
  }

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
      zoomable: false,
      draggable: false,
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

      //  자치구 별 소음 데이터 매핑
      autonomousAreas.forEach(autonomousArea => {
        const noise = window.mapEventHandlers.noises.autonomousNoises.get(autonomousArea.districtCode);

        const poly = window.mapEventHandlers.polygons.autonomousPolygons.get(autonomousArea.districtCode);
        kakao.maps.event.addListener(poly, 'mouseover', () => {
          if (!infoOverlayRef.current || !infoTitleRef.current || !infoNoiseRef.current || !infoSubRef.current) return;

          // 텍스트 삽입
          infoTitleRef.current.textContent =autonomousArea.name;

          if (noise !== undefined) {
            infoNoiseRef.current.textContent = `평균 소음: ${noise.avgNoise.toFixed(1)}dB`;
            infoSubRef.current.textContent = `체감 소음: ${noise.perceivedNoise.toFixed(1)}dB`; // TODO: 측정일시 추가되면 삽입  · ${formatDate(noise.updatedAt)}
          } else {
            infoNoiseRef.current.textContent = '평균 소음: 측정된 데이터가 없음';
            infoSubRef.current.textContent = '체감 소음: 측정된 데이터가 없음';
          }

          // 오버레이 보이게 하기
          infoOverlayRef.current.style.display = 'flex';
        });

        kakao.maps.event.addListener(poly, 'mouseout', () => {
          if (infoOverlayRef.current) {
            infoOverlayRef.current.style.display = 'none';
          }
        });

        kakao.maps.event.addListener(poly, 'click', () => {
          console.log('mainmap click');
          onDistrictClick(autonomousArea.districtCode);
        });

      })
    })();
  }, [ready]);

  return (
    <div className="relative w-full h-[500px]">
      {/* 오버레이 */}
      <div
        ref={infoOverlayRef}
        className="absolute top-0 left-0 right-0 bottom-0 bg-black/50 z-[9999] hidden justify-center items-center pointer-events-none"
      >
        <div className="text-white text-base text-center p-6">
          <div ref={infoTitleRef} className="font-bold text-lg mb-2" />
          <div ref={infoNoiseRef} className="text-2xl font-semibold" />
          <div ref={infoSubRef} className="mt-1" />
          <div className="mt-2 text-xs text-neutral-300">클릭하여 상세보기</div>
        </div>
      </div>

      {/* 카카오맵 */}
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
