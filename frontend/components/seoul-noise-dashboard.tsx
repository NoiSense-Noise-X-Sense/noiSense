"use client"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

// 서울시 각 구별 소음 데이터 (샘플)
const districtData = {
  gangnam: { name: "강남구", noise: 76.8, status: "보통", color: "yellow", noiseTemp: 87 },
  gangdong: { name: "강동구", noise: 68.5, status: "양호", color: "green", noiseTemp: 92 },
  gangbuk: { name: "강북구", noise: 72.1, status: "보통", color: "yellow", noiseTemp: 85 },
  gangseo: { name: "강서구", noise: 81.2, status: "주의", color: "red", noiseTemp: 70 },
  gwanak: { name: "관악구", noise: 74.3, status: "보통", color: "yellow", noiseTemp: 88 },
  gwangjin: { name: "광진구", noise: 73.9, status: "보통", color: "yellow", noiseTemp: 86 },
  guro: { name: "구로구", noise: 79.4, status: "보통", color: "yellow", noiseTemp: 78 },
  geumcheon: { name: "금천구", noise: 75.6, status: "보통", color: "yellow", noiseTemp: 84 },
  nowon: { name: "노원구", noise: 69.8, status: "양호", color: "green", noiseTemp: 90 },
  dobong: { name: "도봉구", noise: 67.2, status: "양호", color: "green", noiseTemp: 93 },
  dongdaemun: { name: "동대문구", noise: 77.5, status: "보통", color: "yellow", noiseTemp: 82 },
  dongjak: { name: "동작구", noise: 74.8, status: "보통", color: "yellow", noiseTemp: 85 },
  mapo: { name: "마포구", noise: 78.1, status: "보통", color: "yellow", noiseTemp: 80 },
  seodaemun: { name: "서대문구", noise: 75.2, status: "보통", color: "yellow", noiseTemp: 83 },
  seocho: { name: "서초구", noise: 73.4, status: "보통", color: "yellow", noiseTemp: 89 },
  seongdong: { name: "성동구", noise: 76.9, status: "보통", color: "yellow", noiseTemp: 81 },
  seongbuk: { name: "성북구", noise: 71.8, status: "보통", color: "yellow", noiseTemp: 87 },
  songpa: { name: "송파구", noise: 75.1, status: "보통", color: "yellow", noiseTemp: 84 },
  yangcheon: { name: "양천구", noise: 72.6, status: "보통", color: "yellow", noiseTemp: 86 },
  yeongdeungpo: { name: "영등포구", noise: 80.3, status: "주의", color: "red", noiseTemp: 72 },
  yongsan: { name: "용산구", noise: 77.8, status: "보통", color: "yellow", noiseTemp: 79 },
  eunpyeong: { name: "은평구", noise: 70.4, status: "보통", color: "yellow", noiseTemp: 88 },
  jongno: { name: "종로구", noise: 78.9, status: "보통", color: "yellow", noiseTemp: 77 },
  jung: { name: "중구", noise: 82.1, status: "주의", color: "red", noiseTemp: 68 },
  jungnang: { name: "중랑구", noise: 71.3, status: "보통", color: "yellow", noiseTemp: 85 },
}

type DistrictKey = keyof typeof districtData

export default function SeoulNoiseDashboard({ onDistrictClick }: { onDistrictClick: (district: string) => void }) {
  const [hoveredDistrict, setHoveredDistrict] = useState<DistrictKey | null>(null)

  const getColorClass = (color: string) => {
    switch (color) {
      case "green":
        return "fill-green-500 hover:fill-green-600"
      case "yellow":
        return "fill-yellow-500 hover:fill-yellow-600"
      case "red":
        return "fill-red-500 hover:fill-red-600"
      default:
        return "fill-gray-400 hover:fill-gray-500"
    }
  }

  const handleDistrictClick = (district: DistrictKey) => {
    onDistrictClick(districtData[district].name)
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto space-y-6">
        {/* 페이지 헤더 */}
        <h1 className="text-2xl font-bold text-gray-900">서울시 소음 지도</h1>

        {/* 지도 카드 - 전체 폭 사용 */}
        <Card className="w-full">
          <CardHeader>
            <CardTitle className="text-lg font-semibold">서울시 자치구별 소음 지도</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* 지도 영역 */}
            <div className="relative h-96 bg-gray-100 rounded-lg border overflow-hidden">
              {/* 간단한 서울시 지도 SVG */}
              <svg viewBox="0 0 400 300" className="w-full h-full">
                {/* 각 구를 간단한 사각형으로 표현 */}
                {Object.entries(districtData).map(([key, data], index) => {
                  const row = Math.floor(index / 5)
                  const col = index % 5
                  const x = col * 80 + 10
                  const y = row * 60 + 10
                  return (
                    <rect
                      key={key}
                      x={x}
                      y={y}
                      width="70"
                      height="50"
                      className={`${getColorClass(data.color)} cursor-pointer transition-all duration-200 stroke-white stroke-2`}
                      onMouseEnter={() => setHoveredDistrict(key as DistrictKey)}
                      onClick={() => handleDistrictClick(key as DistrictKey)} // Click to navigate
                    />
                  )
                })}
                {/* 구 이름 표시 */}
                {Object.entries(districtData).map(([key, data], index) => {
                  const row = Math.floor(index / 5)
                  const col = index % 5
                  const x = col * 80 + 45
                  const y = row * 60 + 40
                  return (
                    <text
                      key={`${key}-text`}
                      x={x}
                      y={y}
                      textAnchor="middle"
                      className="text-xs font-medium fill-white pointer-events-none"
                    >
                      {data.name}
                    </text>
                  )
                })}
              </svg>

              {/* Hover 툴팁 - 깜빡임 방지 및 상세보기 문구 포함 */}
              {hoveredDistrict && (
                <div
                  className="absolute inset-0 bg-black bg-opacity-75 rounded-lg flex items-center justify-center transition-opacity duration-200 pointer-events-auto"
                  onMouseLeave={() => setHoveredDistrict(null)} // Hide tooltip when mouse leaves the overlay
                >
                  <div className="text-center text-white p-4">
                    <h3 className="text-lg font-semibold mb-2">{districtData[hoveredDistrict].name} 소음 현황</h3>
                    <div className="flex justify-center items-center gap-4 mb-3">
                      <div>
                        <p className="text-sm text-gray-300">소음지수</p>
                        <p
                          className={`text-2xl font-bold ${
                            districtData[hoveredDistrict].color === "green"
                              ? "text-green-400"
                              : districtData[hoveredDistrict].color === "yellow"
                                ? "text-yellow-400"
                                : "text-red-400"
                          }`}
                        >
                          {districtData[hoveredDistrict].noise} dB
                        </p>
                      </div>
                      <div className="w-px h-10 bg-gray-600" />
                      <div className="relative group">
                        <p className="text-sm text-gray-300">소음온도</p>
                        <p className="text-2xl font-bold text-blue-400">{districtData[hoveredDistrict].noiseTemp} 점</p>
                        <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 w-48 p-2 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none">
                          소음온도 공식: (100 - (소음dB - 50) * 2)
                        </div>
                      </div>
                    </div>
                    <p className="text-sm text-gray-300 mb-4">
                      {districtData[hoveredDistrict].status} • 2024.07.07 14:30 기준
                    </p>
                    <p className="text-xs text-gray-400 mt-2">클릭하면 상세보기 화면으로 넘어갑니다</p>
                  </div>
                </div>
              )}
            </div>

            {/* 범례 */}
            <div className="space-y-2">
              <h4 className="font-medium text-gray-700">범례</h4>
              <div className="flex flex-wrap gap-4 text-sm">
                <div className="flex items-center gap-2">
                  <div className="w-4 h-4 bg-green-500 rounded"></div>
                  <span>70dB 미만</span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="w-4 h-4 bg-yellow-500 rounded"></div>
                  <span>70~79dB</span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="w-4 h-4 bg-red-500 rounded"></div>
                  <span>80dB 이상</span>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

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
              <div className="font-semibold text-gray-900">실시간 (30분 간격)</div>
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
  )
}
