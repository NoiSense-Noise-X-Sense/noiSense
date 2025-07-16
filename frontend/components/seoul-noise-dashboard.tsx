"use client"

import { useState } from "react"
import { Card, CardContent } from "@/components/ui/card"

// Seoul districts data with noise levels
const seoulDistricts = [
  { name: "강남구", noise: 78.5, level: "normal", row: 0, col: 0 },
  { name: "강동구", noise: 65.2, level: "low", row: 0, col: 1 },
  { name: "강북구", noise: 72.1, level: "normal", row: 0, col: 2 },
  { name: "강서구", noise: 82.3, level: "high", row: 0, col: 3 },
  { name: "관악구", noise: 75.8, level: "normal", row: 0, col: 4 },

  { name: "광진구", noise: 73.4, level: "normal", row: 1, col: 0 },
  { name: "구로구", noise: 76.9, level: "normal", row: 1, col: 1 },
  { name: "금천구", noise: 74.2, level: "normal", row: 1, col: 2 },
  { name: "노원구", noise: 69.8, level: "low", row: 1, col: 3 },
  { name: "도봉구", noise: 68.1, level: "low", row: 1, col: 4 },

  { name: "동대문구", noise: 77.3, level: "normal", row: 2, col: 0 },
  { name: "동작구", noise: 75.6, level: "normal", row: 2, col: 1 },
  { name: "마포구", noise: 79.2, level: "normal", row: 2, col: 2 },
  { name: "서대문구", noise: 73.8, level: "normal", row: 2, col: 3 },
  { name: "서초구", noise: 76.4, level: "normal", row: 2, col: 4 },

  { name: "성동구", noise: 74.7, level: "normal", row: 3, col: 0 },
  { name: "성북구", noise: 71.9, level: "normal", row: 3, col: 1 },
  { name: "송파구", noise: 77.8, level: "normal", row: 3, col: 2 },
  { name: "양천구", noise: 73.2, level: "normal", row: 3, col: 3 },
  { name: "영등포구", noise: 81.5, level: "high", row: 3, col: 4 },

  { name: "용산구", noise: 78.9, level: "normal", row: 4, col: 0 },
  { name: "은평구", noise: 72.6, level: "normal", row: 4, col: 1 },
  { name: "종로구", noise: 75.1, level: "normal", row: 4, col: 2 },
  { name: "중구", noise: 80.7, level: "high", row: 4, col: 3 },
  { name: "중랑구", noise: 71.4, level: "normal", row: 4, col: 4 },
]

const getDistrictColor = (level: string) => {
  switch (level) {
    case "low":
      return "bg-green-600"
    case "normal":
      return "bg-yellow-600"
    case "high":
      return "bg-red-600"
    default:
      return "bg-gray-600"
  }
}

const getNoiseColor = (level: string) => {
  switch (level) {
    case "low":
      return "text-green-400"
    case "normal":
      return "text-yellow-400"
    case "high":
      return "text-red-400"
    default:
      return "text-gray-400"
  }
}

const getStatusText = (level: string) => {
  switch (level) {
    case "low":
      return "양호"
    case "normal":
      return "보통"
    case "high":
      return "주의"
    default:
      return "보통"
  }
}

export default function SeoulNoiseDashboard() {
  const [hoveredDistrict, setHoveredDistrict] = useState<(typeof seoulDistricts)[0] | null>(null)
  const [showOverlay, setShowOverlay] = useState(false)

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-8">
      <div className="w-full max-w-4xl space-y-8">
        {/* Page Title */}
        <div className="text-center">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">서울시 자치구별 소음 지도</h1>
        </div>

        {/* Centered Map Card */}
        <div className="flex justify-center">
          <Card className="shadow-xl w-full max-w-3xl">
            <CardContent className="p-8">
              <div
                className="relative bg-gray-800 rounded-lg p-6 h-96"
                onMouseEnter={() => setShowOverlay(true)}
                onMouseLeave={() => setShowOverlay(false)}
              >
                {/* District Grid */}
                <div className="grid grid-cols-5 gap-3 h-full">
                  {Array.from({ length: 25 }, (_, index) => {
                    const district = seoulDistricts[index]
                    return (
                      <div
                        key={index}
                        className={`${getDistrictColor(district.level)} rounded-lg flex items-center justify-center text-white text-sm font-medium cursor-pointer hover:opacity-80 transition-all duration-200 hover:scale-105`}
                        onMouseEnter={() => setHoveredDistrict(district)}
                        onMouseLeave={() => setHoveredDistrict(null)}
                      >
                        {district.name}
                      </div>
                    )
                  })}
                </div>

                {/* Hover Overlay */}
                {showOverlay && hoveredDistrict && (
                  <div className="absolute inset-0 bg-black bg-opacity-60 flex items-center justify-center rounded-lg">
                    <div className="bg-gray-800 bg-opacity-95 text-white p-8 rounded-xl text-center border border-gray-600">
                      <h3 className="text-xl font-bold mb-3">{hoveredDistrict.name} 소음 현황</h3>
                      <div className={`text-5xl font-bold mb-3 ${getNoiseColor(hoveredDistrict.level)}`}>
                        {hoveredDistrict.noise} dB
                      </div>
                      <div className="text-base text-gray-300 mb-2">
                        {getStatusText(hoveredDistrict.level)} • 2024.07.07 14:30 기준
                      </div>
                      <div className="text-sm text-gray-400">클릭하여 상세보기</div>
                    </div>
                  </div>
                )}
              </div>

              {/* Legend */}
              <div className="mt-6">
                <h4 className="text-lg font-semibold mb-4 text-center">범례</h4>
                <div className="flex justify-center gap-8 text-base">
                  <div className="flex items-center gap-3">
                    <div className="w-5 h-5 bg-green-600 rounded"></div>
                    <span>70dB 미만</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="w-5 h-5 bg-yellow-600 rounded"></div>
                    <span>70~79dB</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="w-5 h-5 bg-red-600 rounded"></div>
                    <span>80dB 이상</span>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
