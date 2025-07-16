import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

export default function SeoulMap() {
  // 서울 25개 구 데이터 (소음 레벨에 따른 색상)
  const districts = [
    { name: "강남구", level: "high", x: 60, y: 65 },
    { name: "강동구", level: "medium", x: 75, y: 55 },
    { name: "강북구", level: "low", x: 45, y: 25 },
    { name: "강서구", level: "medium", x: 15, y: 50 },
    { name: "관악구", level: "high", x: 45, y: 75 },
    { name: "광진구", level: "medium", x: 65, y: 45 },
    { name: "구로구", level: "high", x: 25, y: 65 },
    { name: "금천구", level: "medium", x: 35, y: 70 },
    { name: "노원구", level: "low", x: 55, y: 15 },
    { name: "도봉구", level: "low", x: 50, y: 20 },
    { name: "동대문구", level: "medium", x: 55, y: 35 },
    { name: "동작구", level: "high", x: 40, y: 70 },
    { name: "마포구", level: "high", x: 35, y: 45 },
    { name: "서대문구", level: "medium", x: 40, y: 40 },
    { name: "서초구", level: "high", x: 50, y: 70 },
    { name: "성동구", level: "medium", x: 60, y: 40 },
    { name: "성북구", level: "low", x: 50, y: 30 },
    { name: "송파구", level: "medium", x: 70, y: 60 },
    { name: "양천구", level: "medium", x: 25, y: 55 },
    { name: "영등포구", level: "high", x: 30, y: 60 },
    { name: "용산구", level: "high", x: 45, y: 55 },
    { name: "은평구", level: "low", x: 35, y: 30 },
    { name: "종로구", level: "medium", x: 45, y: 45 },
    { name: "중구", level: "high", x: 50, y: 50 },
    { name: "중랑구", level: "medium", x: 60, y: 30 },
  ]

  const getColor = (level: string) => {
    switch (level) {
      case "high":
        return "#EF4444" // 빨간색 - 높은 소음
      case "medium":
        return "#F59E0B" // 주황색 - 중간 소음
      case "low":
        return "#10B981" // 초록색 - 낮은 소음
      default:
        return "#6B7280"
    }
  }

  return (
    <Card className="w-full shadow-xl border-0 bg-white hover:shadow-2xl transition-all duration-300">
      <CardHeader>
        <CardTitle className="text-xl font-bold text-gray-900">서울시 소음 분포 지도</CardTitle>
        <p className="text-sm text-gray-600">실시간 구별 소음 현황</p>
      </CardHeader>
      <CardContent>
        <div className="relative bg-gradient-to-br from-blue-50 to-indigo-100 rounded-xl p-6 h-[500px]">
          {/* 서울 지도 SVG */}
          <svg className="w-full h-full" viewBox="0 0 100 100" preserveAspectRatio="xMidYMid meet">
            {/* 한강 표시 */}
            <path
              d="M10 45 Q30 50 50 48 Q70 46 90 50 Q85 55 65 53 Q45 51 25 55 Q15 52 10 45"
              fill="#3B82F6"
              opacity="0.3"
              stroke="#2563EB"
              strokeWidth="0.5"
            />
            <text x="50" y="52" fontSize="2" fill="#2563EB" textAnchor="middle" fontWeight="bold">
              한강
            </text>

            {/* 구별 원형 마커 */}
            {districts.map((district, index) => (
              <g key={index}>
                <circle
                  cx={district.x}
                  cy={district.y}
                  r="3"
                  fill={getColor(district.level)}
                  stroke="white"
                  strokeWidth="0.5"
                  className="hover:r-4 transition-all duration-200 cursor-pointer"
                  opacity="0.8"
                />
                <text
                  x={district.x}
                  y={district.y - 4}
                  fontSize="2.5"
                  fill="#374151"
                  textAnchor="middle"
                  fontWeight="bold"
                  className="pointer-events-none"
                >
                  {district.name}
                </text>
              </g>
            ))}

            {/* 서울 경계선 (간단한 형태) */}
            <path
              d="M15 20 Q25 15 35 18 Q45 12 55 15 Q65 12 75 18 Q85 25 88 35 Q90 45 85 55 Q80 65 70 70 Q60 75 50 73 Q40 75 30 70 Q20 65 15 55 Q10 45 12 35 Q13 25 15 20 Z"
              fill="none"
              stroke="#6B7280"
              strokeWidth="0.8"
              strokeDasharray="2,1"
              opacity="0.6"
            />
          </svg>

          {/* 범례 */}
          <div className="absolute bottom-4 left-4 bg-white/90 backdrop-blur-sm p-3 rounded-lg shadow-lg">
            <h4 className="text-sm font-semibold mb-2">소음 수준</h4>
            <div className="space-y-1">
              <div className="flex items-center space-x-2">
                <div className="w-3 h-3 bg-red-500 rounded-full"></div>
                <span className="text-xs">높음 (70+ dB)</span>
              </div>
              <div className="flex items-center space-x-2">
                <div className="w-3 h-3 bg-amber-500 rounded-full"></div>
                <span className="text-xs">보통 (60-70 dB)</span>
              </div>
              <div className="flex items-center space-x-2">
                <div className="w-3 h-3 bg-green-500 rounded-full"></div>
                <span className="text-xs">낮음 (50-60 dB)</span>
              </div>
            </div>
          </div>

          {/* 현재 시간 표시 */}
          <div className="absolute top-4 right-4 bg-white/90 backdrop-blur-sm p-2 rounded-lg shadow-lg">
            <div className="text-xs text-gray-600">업데이트</div>
            <div className="text-sm font-semibold">2025.01.07 15:30</div>
          </div>
        </div>

        {/* 상세 정보 */}
        <div className="mt-4 grid grid-cols-3 gap-4 text-center">
          <div className="p-3 bg-red-50 rounded-lg">
            <div className="text-lg font-bold text-red-600">8개구</div>
            <div className="text-xs text-gray-600">높은 소음</div>
          </div>
          <div className="p-3 bg-amber-50 rounded-lg">
            <div className="text-lg font-bold text-amber-600">12개구</div>
            <div className="text-xs text-gray-600">보통 소음</div>
          </div>
          <div className="p-3 bg-green-50 rounded-lg">
            <div className="text-lg font-bold text-green-600">5개구</div>
            <div className="text-xs text-gray-600">낮은 소음</div>
          </div>
        </div>
      </CardContent>
    </Card>
  )
}
