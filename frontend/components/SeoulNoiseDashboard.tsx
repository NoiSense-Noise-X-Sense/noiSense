'use client';

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useRouter } from 'next/navigation';
import MainMap from "@/components/map/MainMap";

// 서울시 각 구별 소음 데이터 (샘플)
const districtData = {
  gangnam: { name: '강남구', noise: 76.8, status: '보통', color: 'yellow' },
  gangdong: { name: '강동구', noise: 68.5, status: '양호', color: 'green' },
  gangbuk: { name: '강북구', noise: 72.1, status: '보통', color: 'yellow' },
  gangseo: { name: '강서구', noise: 81.2, status: '주의', color: 'red' },
  gwanak: { name: '관악구', noise: 74.3, status: '보통', color: 'yellow' },
  gwangjin: { name: '광진구', noise: 73.9, status: '보통', color: 'yellow' },
  guro: { name: '구로구', noise: 79.4, status: '보통', color: 'yellow' },
  geumcheon: { name: '금천구', noise: 75.6, status: '보통', color: 'yellow' },
  nowon: { name: '노원구', noise: 69.8, status: '양호', color: 'green' },
  dobong: { name: '도봉구', noise: 67.2, status: '양호', color: 'green' },
  dongdaemun: { name: '동대문구', noise: 77.5, status: '보통', color: 'yellow' },
  dongjak: { name: '동작구', noise: 74.8, status: '보통', color: 'yellow' },
  mapo: { name: '마포구', noise: 78.1, status: '보통', color: 'yellow' },
  seodaemun: { name: '서대문구', noise: 75.2, status: '보통', color: 'yellow' },
  seocho: { name: '서초구', noise: 73.4, status: '보통', color: 'yellow' },
  seongdong: { name: '성동구', noise: 76.9, status: '보통', color: 'yellow' },
  seongbuk: { name: '성북구', noise: 71.8, status: '보통', color: 'yellow' },
  songpa: { name: '송파구', noise: 75.1, status: '보통', color: 'yellow' },
  yangcheon: { name: '양천구', noise: 72.6, status: '보통', color: 'yellow' },
  yeongdeungpo: { name: '영등포구', noise: 80.3, status: '주의', color: 'red' },
  yongsan: { name: '용산구', noise: 77.8, status: '보통', color: 'yellow' },
  eunpyeong: { name: '은평구', noise: 70.4, status: '보통', color: 'yellow' },
  jongno: { name: '종로구', noise: 78.9, status: '보통', color: 'yellow' },
  jung: { name: '중구', noise: 82.1, status: '주의', color: 'red' },
  jungnang: { name: '중랑구', noise: 71.3, status: '보통', color: 'yellow' },
};

type DistrictKey = keyof typeof districtData;

type Props = {
  onDistrictClick: (district: string) => void;
};

export default function SeoulNoiseDashboard({ onDistrictClick }: Props) {
  const [hoveredDistrict, setHoveredDistrict] = useState<DistrictKey | null>(null);

  const getColorClass = (color: string) => {
    switch (color) {
      case 'green':
        return 'fill-green-500 hover:fill-green-600';
      case 'yellow':
        return 'fill-yellow-500 hover:fill-yellow-600';
      case 'red':
        return 'fill-red-500 hover:fill-red-600';
      default:
        return 'fill-gray-400 hover:fill-gray-500';
    }
  };

  const router = useRouter();

  const handleDistrictClick = (district: DistrictKey) => {
    onDistrictClick(districtData[district].name); // props로 콜백 호출
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto space-y-6">
        {/* 상단 섹션 - 지도 */}
        <div>
          {/* 지도 카드 */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg font-semibold">서울시 자치구별 소음 지도</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {/* 메인 지도 영역 */}
              <div className="relative h-100 bg-gray-100 rounded-lg border overflow-hidden">
                <MainMap onDistrictClick={onDistrictClick} />
              </div>
            </CardContent>
          </Card>
        </div>

        {/* 정보 카드 그리드 */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card>
            <CardContent className="p-4 text-center">
              <div className="text-sm text-gray-600 mb-2">데이터 출처</div>
              <div className="font-semibold text-gray-900">서울시 공공데이터</div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-4 text-center">
              <div className="text-sm text-gray-600 mb-2">측정 단위</div>
              <div className="font-semibold text-gray-900">데시벨 (dB)</div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-4 text-center">
              <div className="text-sm text-gray-600 mb-2">업데이트 주기</div>
              <div className="font-semibold text-gray-900">매일 오전 9시 30분, 오후 3시 30분</div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-4 text-center">
              <div className="text-sm text-gray-600 mb-2">측정소</div>
              <div className="font-semibold text-gray-900">서울시 전 구역</div>
            </CardContent>
          </Card>
        </div>

        {/* 하단 브랜드 슬로건 */}
        <footer className="text-center py-8 border-t border-gray-200 mt-12">
          <h2 className="text-xl font-bold text-gray-900 mb-1">NoiSense</h2>
          <p className="text-gray-600">서울 공공데이터를 활용한 소음정보 제공 서비스</p>
        </footer>
      </div>
    </div>
  );
}
