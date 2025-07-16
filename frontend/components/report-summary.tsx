"use client"

import { useState, useMemo } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Download, FileText, TrendingUp, TrendingDown, BarChart3 } from "lucide-react"

const mockData = [
  { gu: "강남구", avgNoise: 73.2, complaints: 1245, trend: "up" },
  { gu: "마포구", avgNoise: 70.8, complaints: 987, trend: "up" },
  { gu: "송파구", avgNoise: 69.9, complaints: 856, trend: "down" },
  { gu: "영등포구", avgNoise: 68.5, complaints: 743, trend: "up" },
  { gu: "구로구", avgNoise: 67.3, complaints: 692, trend: "down" },
  { gu: "용산구", avgNoise: 66.8, complaints: 634, trend: "up" },
  { gu: "서초구", avgNoise: 65.4, complaints: 578, trend: "down" },
  { gu: "관악구", avgNoise: 64.2, complaints: 523, trend: "up" },
  { gu: "동작구", avgNoise: 63.7, complaints: 467, trend: "down" },
  { gu: "성동구", avgNoise: 62.9, complaints: 412, trend: "up" },
  { gu: "광진구", avgNoise: 62.1, complaints: 389, trend: "down" },
  { gu: "종로구", avgNoise: 61.8, complaints: 356, trend: "up" },
  { gu: "중구", avgNoise: 61.3, complaints: 334, trend: "down" },
  { gu: "성북구", avgNoise: 60.8, complaints: 298, trend: "up" },
  { gu: "강서구", avgNoise: 60.5, complaints: 276, trend: "down" },
  { gu: "양천구", avgNoise: 60.2, complaints: 245, trend: "up" },
  { gu: "은평구", avgNoise: 60.1, complaints: 223, trend: "down" },
  { gu: "서대문구", avgNoise: 59.7, complaints: 198, trend: "up" },
  { gu: "동대문구", avgNoise: 59.4, complaints: 187, trend: "down" },
  { gu: "금천구", avgNoise: 59.1, complaints: 165, trend: "up" },
  { gu: "강북구", avgNoise: 58.9, complaints: 143, trend: "down" },
  { gu: "노원구", avgNoise: 58.6, complaints: 132, trend: "up" },
  { gu: "도봉구", avgNoise: 59.3, complaints: 121, trend: "down" },
  { gu: "강동구", avgNoise: 58.4, complaints: 109, trend: "up" },
  { gu: "중랑구", avgNoise: 58.7, complaints: 98, trend: "down" },
]

export default function ReportSummary() {
  const [selectedGus, setSelectedGus] = useState<string[]>([])

  const filteredData = useMemo(() => {
    return selectedGus.length > 0 ? mockData.filter((d) => selectedGus.includes(d.gu)) : mockData
  }, [selectedGus])

  const top3 = useMemo(() => [...filteredData].sort((a, b) => b.avgNoise - a.avgNoise).slice(0, 3), [filteredData])

  const bottom3 = useMemo(() => [...filteredData].sort((a, b) => a.avgNoise - b.avgNoise).slice(0, 3), [filteredData])

  const totalComplaints = useMemo(() => filteredData.reduce((sum, item) => sum + item.complaints, 0), [filteredData])

  const avgNoise = useMemo(
    () => (filteredData.reduce((sum, item) => sum + item.avgNoise, 0) / filteredData.length).toFixed(1),
    [filteredData],
  )

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">분석 리포트</h1>
          <p className="text-sm text-gray-600 mt-1">
            {selectedGus.length > 0 ? `${selectedGus.length}개 구 선택됨` : "전체 25개 구 데이터"}
          </p>
        </div>
        <div className="flex gap-3">
          <Button variant="outline" className="flex items-center gap-2 bg-transparent">
            <Download className="h-4 w-4" />
            엑셀 다운로드
          </Button>
          <Button className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700">
            <FileText className="h-4 w-4" />
            PDF 리포트 생성
          </Button>
        </div>
      </div>

      {/* Summary Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card className="bg-gradient-to-r from-blue-50 to-blue-100 border-blue-200">
          <CardContent className="p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-blue-600 font-medium">전체 평균 소음</p>
                <p className="text-2xl font-bold text-blue-900">{avgNoise} dB</p>
              </div>
              <BarChart3 className="h-8 w-8 text-blue-500" />
            </div>
          </CardContent>
        </Card>

        <Card className="bg-gradient-to-r from-orange-50 to-orange-100 border-orange-200">
          <CardContent className="p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-orange-600 font-medium">총 민원 건수</p>
                <p className="text-2xl font-bold text-orange-900">{totalComplaints.toLocaleString()}건</p>
              </div>
              <FileText className="h-8 w-8 text-orange-500" />
            </div>
          </CardContent>
        </Card>

        <Card className="bg-gradient-to-r from-green-50 to-green-100 border-green-200">
          <CardContent className="p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-green-600 font-medium">분석 대상 구</p>
                <p className="text-2xl font-bold text-green-900">{filteredData.length}개</p>
              </div>
              <TrendingUp className="h-8 w-8 text-green-500" />
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Top/Bottom Rankings */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card className="shadow-lg">
          <CardHeader className="bg-red-50 border-b">
            <CardTitle className="text-red-700 flex items-center gap-2">
              <TrendingUp className="h-5 w-5" />
              TOP 3 시끄러운 구
            </CardTitle>
          </CardHeader>
          <CardContent className="p-6">
            <div className="space-y-4">
              {top3.map((item, idx) => (
                <div key={item.gu} className="flex items-center justify-between p-3 bg-red-50 rounded-lg">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 bg-red-500 text-white rounded-full flex items-center justify-center font-bold text-sm">
                      {idx + 1}
                    </div>
                    <div>
                      <span className="font-semibold text-gray-900">{item.gu}</span>
                      <p className="text-xs text-gray-600">{item.complaints}건 민원</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="font-bold text-red-600 text-lg">{item.avgNoise} dB</span>
                    {item.trend === "up" && <TrendingUp className="h-4 w-4 text-red-500 inline ml-1" />}
                    {item.trend === "down" && <TrendingDown className="h-4 w-4 text-green-500 inline ml-1" />}
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <Card className="shadow-lg">
          <CardHeader className="bg-blue-50 border-b">
            <CardTitle className="text-blue-700 flex items-center gap-2">
              <TrendingDown className="h-5 w-5" />
              TOP 3 조용한 구
            </CardTitle>
          </CardHeader>
          <CardContent className="p-6">
            <div className="space-y-4">
              {bottom3.map((item, idx) => (
                <div key={item.gu} className="flex items-center justify-between p-3 bg-blue-50 rounded-lg">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 bg-blue-500 text-white rounded-full flex items-center justify-center font-bold text-sm">
                      {idx + 1}
                    </div>
                    <div>
                      <span className="font-semibold text-gray-900">{item.gu}</span>
                      <p className="text-xs text-gray-600">{item.complaints}건 민원</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="font-bold text-blue-600 text-lg">{item.avgNoise} dB</span>
                    {item.trend === "up" && <TrendingUp className="h-4 w-4 text-red-500 inline ml-1" />}
                    {item.trend === "down" && <TrendingDown className="h-4 w-4 text-green-500 inline ml-1" />}
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* District Selection */}
      <Card className="shadow-lg">
        <CardHeader>
          <CardTitle className="text-gray-800">자치구 선택 필터</CardTitle>
          <p className="text-sm text-gray-600">분석하고 싶은 구를 선택하세요. (다중 선택 가능)</p>
        </CardHeader>
        <CardContent>
          <div className="flex flex-wrap gap-2">
            {mockData.map((item) => (
              <Button
                key={item.gu}
                variant={selectedGus.includes(item.gu) ? "default" : "outline"}
                size="sm"
                onClick={() => {
                  setSelectedGus((prev) =>
                    prev.includes(item.gu) ? prev.filter((g) => g !== item.gu) : [...prev, item.gu],
                  )
                }}
                className={`transition-all duration-200 ${
                  selectedGus.includes(item.gu)
                    ? "bg-blue-600 hover:bg-blue-700 text-white"
                    : "hover:bg-blue-50 hover:border-blue-300"
                }`}
              >
                {item.gu}
              </Button>
            ))}
          </div>
          {selectedGus.length > 0 && (
            <div className="mt-4 pt-4 border-t">
              <Button
                variant="outline"
                size="sm"
                onClick={() => setSelectedGus([])}
                className="text-gray-600 hover:text-gray-800"
              >
                전체 선택 해제
              </Button>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
