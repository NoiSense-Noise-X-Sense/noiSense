"use client"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"

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

export default function SeoulMapV3() {
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

  return (
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
            <TooltipProvider>
              {Object.entries(districtData).map(([key, data], index) => {
                const row = Math.floor(index / 5)
                const col = index % 5
                const x = col * 80 + 10
                const y = row * 60 + 10
                return (
                  <Tooltip key={key}>
                    <TooltipTrigger asChild>
                      <rect
                        x={x}
                        y={y}
                        width="70"
                        height="50"
                        className={`${getColorClass(data.color)} cursor-pointer transition-all duration-200 stroke-white stroke-2`}
                      />
                    </TooltipTrigger>
                    <TooltipContent className="bg-black text-white p-3 rounded-lg shadow-lg">
                      <h3 className="text-lg font-semibold mb-2">{data.name} 소음 현황</h3>
                      <div className="flex justify-between items-center gap-4 mb-2">
                        <div>
                          <p className="text-sm text-gray-300">소음지수</p>
                          <p
                            className={`text-xl font-bold ${
                              data.color === "green"
                                ? "text-green-400"
                                : data.color === "yellow"
                                  ? "text-yellow-400"
                                  : "text-red-400"
                            }`}
                          >
                            {data.noise} dB
                          </p>
                        </div>
                        <div className="w-px h-10 bg-gray-600" />
                        <div className="relative group">
                          <p className="text-sm text-gray-300">소음온도</p>
                          <p className="text-xl font-bold text-blue-400">{data.noiseTemp} 점</p>
                          <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 w-48 p-2 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none">
                            소음온도 공식: (100 - (소음dB - 50) * 2)
                          </div>
                        </div>
                      </div>
                      <p className="text-xs text-gray-400 mt-2">클릭하여 상세보기 화면으로 이동</p>
                    </TooltipContent>
                  </Tooltip>
                )
              })}
            </TooltipProvider>
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
  )
}
