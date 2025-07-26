"use client"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"
import FullMap from "@/components/map/FullMap";

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

export default function SeoulMap() {
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
        <CardTitle className="text-lg font-semibold">서울시 fghfhgf자치구별 소음 지도</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        {/* 소음 지도 지도 영역 */}
        <div className="relative h-100 bg-gray-100 rounded-lg border overflow-hidden">
          {/* 간단한 서울시 지도 SVG */}
          <FullMap />
        </div>
      </CardContent>
    </Card>
  )
}
