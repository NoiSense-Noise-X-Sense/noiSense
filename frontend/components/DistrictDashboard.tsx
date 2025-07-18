"use client"

import { Button } from "@/components/ui/button"
import { useState, useEffect, useRef } from "react"
import { Card, CardTitle } from "@/components/ui/card"
import { BarChart, LineChart } from "@tremor/react"
import { Volume2, MessageSquare, Moon, TrendingUp } from "lucide-react"
import { Switch } from "@/components/ui/switch"
import { Label } from "@/components/ui/label"

// Dummy data for districts and their noise info
const allDistricts = [
  "강남구",
  "강동구",
  "강북구",
  "강서구",
  "관악구",
  "광진구",
  "구로구",
  "금천구",
  "노원구",
  "도봉구",
  "동대문구",
  "동작구",
  "마포구",
  "서대문구",
  "서초구",
  "성동구",
  "성북구",
  "송파구",
  "양천구",
  "영등포구",
  "용산구",
  "은평구",
  "종로구",
  "중구",
  "중랑구",
]

const generateDummyData = (districtName: string) => {
  const baseNoise = 60 + Math.random() * 20 // 60-80 dB
  const baseComplaints = 150 + Math.random() * 100 // 150-250 complaints

  const yearlyComplaints = {
    "2020": Math.floor(baseComplaints * (0.8 + Math.random() * 0.4)),
    "2021": Math.floor(baseComplaints * (0.9 + Math.random() * 0.4)),
    "2022": Math.floor(baseComplaints * (1.0 + Math.random() * 0.4)),
    "2023": Math.floor(baseComplaints * (1.1 + Math.random() * 0.4)),
    "2024": Math.floor(baseComplaints * (1.2 + Math.random() * 0.4)),
  }

  const noiseTrendData = Array.from({ length: 24 }, (_, i) => ({
    hour: `${i.toString().padStart(2, "0")}시`,
    실시간: Math.floor(baseNoise + Math.random() * 10 - 5),
    일주일평균: Math.floor(baseNoise + Math.random() * 5 - 2.5),
    한달평균: Math.floor(baseNoise + Math.random() * 3 - 1.5),
  }))

  const yearlyAvgNoise = {
    "2022": { seoul: 66 + Math.random() * 5, district: baseNoise * (0.9 + Math.random() * 0.2) },
    "2023": { seoul: 68 + Math.random() * 5, district: baseNoise * (0.95 + Math.random() * 0.2) },
    "2024": { seoul: 64 + Math.random() * 5, district: baseNoise * (1.0 + Math.random() * 0.2) },
    "2025": { seoul: 62 + Math.random() * 5, district: baseNoise * (1.05 + Math.random() * 0.2) },
  }

  const keywords = [
    { text: "시끄러움", sentiment: "negative", size: "text-3xl" },
    { text: "불쾌함", sentiment: "negative", size: "text-2xl" },
    { text: "스트레스", sentiment: "negative", size: "text-xl" },
    { text: "짜증남", sentiment: "negative", size: "text-lg" },
    { text: "피곤함", sentiment: "neutral", size: "text-base" },
    { text: "답답함", sentiment: "neutral", size: "text-base" },
    { text: "조용함", sentiment: "positive", size: "text-xl" },
    { text: "평화로움", sentiment: "positive", size: "text-lg" },
    { text: "편안함", sentiment: "positive", size: "text-base" },
  ]

  return {
    complaints: {
      "2024": yearlyComplaints["2024"],
      analysisPeriod: "2025.06.06 ~ 2025.07.06",
    },
    avgNoise: {
      value: (baseNoise + Math.random() * 5).toFixed(1),
      analysisPeriod: "2025.06.06 ~ 2025.07.06",
    },
    peakTime: {
      time: "오후 6시",
      noise: (baseNoise + Math.random() * 5 + 5).toFixed(0),
      analysisPeriod: "2025.06.06 ~ 2025.07.06",
    },
    quietTime: {
      time: "새벽 4시",
      noise: (baseNoise - Math.random() * 5 - 10).toFixed(0),
      analysisPeriod: "2025.06.06 ~ 2025.07.06",
    },
    yearlyComplaints,
    noiseTrendData,
    yearlyAvgNoise,
    keywords,
  }
}

export default function DistrictDashboard({ selectedDistrict: initialDistrict }: { selectedDistrict: string }) {
  const [selectedDistrict, setSelectedDistrict] = useState(initialDistrict)
  const [autoScroll, setAutoScroll] = useState(true)
  const scrollContainerRef = useRef<HTMLDivElement>(null)
  const scrollIntervalRef = useRef<NodeJS.Timeout | null>(null)

  const [districtData, setDistrictData] = useState(() => generateDummyData(initialDistrict))

  useEffect(() => {
    setDistrictData(generateDummyData(selectedDistrict))
  }, [selectedDistrict])

  useEffect(() => {
    if (autoScroll) {
      scrollIntervalRef.current = setInterval(() => {
        setSelectedDistrict((prevDistrict) => {
          const currentIndex = allDistricts.indexOf(prevDistrict)
          const nextIndex = (currentIndex + 1) % allDistricts.length
          return allDistricts[nextIndex]
        })
      }, 5000)
    } else {
      if (scrollIntervalRef.current) {
        clearInterval(scrollIntervalRef.current)
      }
    }
    return () => {
      if (scrollIntervalRef.current) {
        clearInterval(scrollIntervalRef.current)
      }
    }
  }, [autoScroll])

  const handleDistrictClick = (district: string) => {
    setAutoScroll(false)
    setSelectedDistrict(district)
  }

  const getSentimentColor = (sentiment: string) => {
    switch (sentiment) {
      case "negative":
        return "text-red-500"
      case "neutral":
        return "text-yellow-500"
      case "positive":
        return "text-green-500"
      default:
        return "text-gray-700"
    }
  }

  const yearlyComplaintsChartData = Object.entries(districtData.yearlyComplaints).map(([year, count]) => ({
    year,
    "민원 건수": count,
  }))

  const yearlyAvgNoiseChartData = Object.entries(districtData.yearlyAvgNoise).map(([year, data]) => ({
    year,
    "서울시 평균": data.seoul,
    [`${selectedDistrict} 평균`]: data.district,
  }))

  return (
    <div className="flex min-h-[calc(100vh-64px)] bg-gray-50">
      {/* Left Sidebar */}
      <div className="w-64 bg-white border-r border-gray-200 p-4 flex flex-col">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold text-gray-800">서울시 25개구</h2>
          <div className="flex items-center space-x-2">
            <Label htmlFor="auto-scroll" className="text-sm text-gray-600">
              자동 순환
            </Label>
            <Switch id="auto-scroll" checked={autoScroll} onCheckedChange={setAutoScroll} />
          </div>
        </div>
        <div ref={scrollContainerRef} className="flex-1 overflow-y-auto space-y-1">
          {allDistricts.map((district) => (
            <Button
              key={district}
              variant="ghost"
              className={`w-full justify-start text-left px-3 py-2 rounded-lg ${
                selectedDistrict === district
                  ? "bg-gradient-to-r from-blue-500 to-purple-600 text-white shadow-md"
                  : "text-gray-700 hover:bg-gray-100"
              }`}
              onClick={() => handleDistrictClick(district)}
            >
              <Volume2 className="h-4 w-4 mr-2" />
              {district}
            </Button>
          ))}
        </div>
      </div>

      {/* Right Main Dashboard */}
      <div className="flex-1 p-6 space-y-6">
        <h1 className="text-3xl font-bold text-gray-900">{selectedDistrict} 소음 현황</h1>

        {/* Summary Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card className="flex flex-col items-center justify-center p-4 text-center">
            <div className="flex items-center justify-center w-12 h-12 rounded-full bg-blue-100 text-blue-600 mb-2">
              <MessageSquare className="h-6 w-6" />
            </div>
            <p className="text-sm text-gray-600">2024년도 민원 수</p>
            <p className="text-3xl font-bold text-gray-900">{districtData.complaints["2024"]}건</p>
            <p className="text-xs text-gray-500 mt-1">분석 기간: {districtData.complaints.analysisPeriod}</p>
          </Card>
          <Card className="flex flex-col items-center justify-center p-4 text-center">
            <div className="flex items-center justify-center w-12 h-12 rounded-full bg-green-100 text-green-600 mb-2">
              <Volume2 className="h-6 w-6" />
            </div>
            <p className="text-sm text-gray-600">한달 평균 소음</p>
            <p className="text-3xl font-bold text-gray-900">{districtData.avgNoise.value} dB</p>
            <p className="text-xs text-gray-500 mt-1">분석 기간: {districtData.avgNoise.analysisPeriod}</p>
          </Card>
          <Card className="flex flex-col items-center justify-center p-4 text-center">
            <div className="flex items-center justify-center w-12 h-12 rounded-full bg-orange-100 text-orange-600 mb-2">
              <TrendingUp className="h-6 w-6" />
            </div>
            <p className="text-sm text-gray-600">소음 집중 시간</p>
            <p className="text-3xl font-bold text-gray-900">
              {districtData.peakTime.time} ({districtData.peakTime.noise} dB)
            </p>
            <p className="text-xs text-gray-500 mt-1">분석 기간: {districtData.peakTime.analysisPeriod}</p>
          </Card>
          <Card className="flex flex-col items-center justify-center p-4 text-center">
            <div className="flex items-center justify-center w-12 h-12 rounded-full bg-purple-100 text-purple-600 mb-2">
              <Moon className="h-6 w-6" />
            </div>
            <p className="text-sm text-gray-600">소음 안정 시간</p>
            <p className="text-3xl font-bold text-gray-900">
              {districtData.quietTime.time} ({districtData.quietTime.noise} dB)
            </p>
            <p className="text-xs text-gray-500 mt-1">분석 기간: {districtData.quietTime.analysisPeriod}</p>
          </Card>
        </div>

        {/* Middle Section: District Icon, TOP Keywords, Yearly Complaints Chart */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
          <Card className="col-span-1 flex flex-col items-center justify-center p-6">
            <CardTitle className="text-lg font-semibold mb-4">
              <Volume2 className="inline-block h-5 w-5 mr-2 text-gray-600" />
              {selectedDistrict}
            </CardTitle>
            <div className="w-32 h-32 bg-gray-100 rounded-full flex items-center justify-center text-gray-400 text-5xl font-bold">
              {selectedDistrict.charAt(0)}
            </div>
            <p className="text-sm text-gray-500 mt-4">{selectedDistrict}의 소음 현황을 분석합니다.</p>
          </Card>

          <Card className="col-span-1 lg:col-span-1 p-6">
            <CardTitle className="text-lg font-semibold mb-4">
              <MessageSquare className="inline-block h-5 w-5 mr-2 text-gray-600" />
              {selectedDistrict}의 TOP 키워드
            </CardTitle>
            <div className="flex flex-wrap gap-2 justify-center items-center min-h-[120px]">
              {districtData.keywords.map((keyword, index) => (
                <span key={index} className={`${getSentimentColor(keyword.sentiment)} ${keyword.size} font-bold`}>
                  {keyword.text}
                </span>
              ))}
            </div>
            <div className="flex justify-center gap-4 mt-4 text-sm">
              <div className="flex items-center gap-1">
                <span className="w-3 h-3 bg-red-500 rounded-full"></span>
                <span>부정적</span>
              </div>
              <div className="flex items-center gap-1">
                <span className="w-3 h-3 bg-yellow-500 rounded-full"></span>
                <span>중립적</span>
              </div>
              <div className="flex items-center gap-1">
                <span className="w-3 h-3 bg-green-500 rounded-full"></span>
                <span>긍정적</span>
              </div>
            </div>
          </Card>

          <Card className="col-span-1 lg:col-span-1 p-6">
            <CardTitle className="text-lg font-semibold mb-4">2020 ~ 2024 소음민원 추이</CardTitle>
            <p className="text-sm text-gray-500 mb-4">최근 5년간 소음 관련 민원 접수 현황</p>
            <div className="h-48">
              <BarChart
                data={yearlyComplaintsChartData}
                index="year"
                categories={["민원 건수"]}
                colors={["blue"]}
                yAxisWidth={48}
                showAnimation={true}
                valueFormatter={(number: number) => `${number}건`}
              />
            </div>
          </Card>
        </div>

        {/* Bottom Section: 24h Noise Trend, Seoul vs District Avg Noise */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
          <Card className="col-span-1 p-6">
            <CardTitle className="text-lg font-semibold mb-4">{selectedDistrict} 소음 추이</CardTitle>
            <div className="h-64">
              <LineChart
                data={districtData.noiseTrendData}
                index="hour"
                categories={["실시간", "일주일평균", "한달평균"]}
                colors={["orange", "yellow", "purple"]}
                yAxisWidth={48}
                showAnimation={true}
                valueFormatter={(number: number) => `${number} dB`}
              />
            </div>
            <div className="flex justify-center gap-4 mt-4 text-sm">
              <div className="flex items-center gap-1">
                <span className="w-3 h-3 bg-orange-500 rounded-full"></span>
                <span>실시간</span>
              </div>
              <div className="flex items-center gap-1">
                <span className="w-3 h-3 bg-yellow-500 rounded-full"></span>
                <span>일주일 평균</span>
              </div>
              <div className="flex items-center gap-1">
                <span className="w-3 h-3 bg-purple-500 rounded-full"></span>
                <span>한달 평균</span>
              </div>
            </div>
          </Card>

          <Card className="col-span-1 p-6">
            <CardTitle className="text-lg font-semibold mb-4">
              연도별 평균 소음 (서울시 vs {selectedDistrict})
            </CardTitle>
            <div className="space-y-4 mt-6">
              {yearlyAvgNoiseChartData.map((data) => (
                <div key={data.year} className="flex flex-col">
                  <span className="text-sm font-medium text-gray-700 mb-1">{data.year}</span>
                  <div className="flex items-center gap-2">
                    <div className="w-full bg-gray-200 rounded-full h-2.5 dark:bg-gray-700">
                      <div
                        className="bg-blue-600 h-2.5 rounded-full"
                        style={{ width: `${(data["서울시 평균"] / 100) * 100}%` }}
                      ></div>
                    </div>
                    <span className="text-xs font-medium text-blue-600">{data["서울시 평균"].toFixed(0)} dB</span>
                  </div>
                  <div className="flex items-center gap-2 mt-1">
                    <div className="w-full bg-gray-200 rounded-full h-2.5 dark:bg-gray-700">
                      <div
                        className="bg-purple-600 h-2.5 rounded-full"
                        style={{ width: `${(data[`${selectedDistrict} 평균`] / 100) * 100}%` }}
                      ></div>
                    </div>
                    <span className="text-xs font-medium text-purple-600">
                      {data[`${selectedDistrict} 평균`].toFixed(0)} dB
                    </span>
                  </div>
                </div>
              ))}
            </div>
            <div className="flex justify-center gap-4 mt-6 text-sm">
              <div className="flex items-center gap-1">
                <span className="w-3 h-3 bg-blue-600 rounded-full"></span>
                <span>서울시 평균</span>
              </div>
              <div className="flex items-center gap-1">
                <span className="w-3 h-3 bg-purple-600 rounded-full"></span>
                <span>{selectedDistrict} 평균</span>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  )
}
