"use client"

import { useState } from "react"
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts"
import { Card } from "@/components/ui/card"
import { ChartDataPoint } from "@/types/Report"

interface MainChartProps {
  hourlyData: ChartDataPoint[]
  dailyData: ChartDataPoint[]
}

export default function MainChart({ hourlyData, dailyData }: MainChartProps) {
  const [activeTab, setActiveTab] = useState("hourly")

  const currentData = activeTab === "hourly" ? (hourlyData || []) : (dailyData || [])
  const currentTitle = activeTab === "hourly" ? "시간대별 평균 소음" : "일별 평균 소음"
  const hasValidData = currentData.some(d => d.avgNoise !== null);

  const xDataKey = activeTab === "hourly" ? "xaxis" : "xaxis" // 'dailyData'도 'xaxis' 키를 사용하므로 통일

  return (
    <Card className="p-6">
      <div className="mb-6">
        <div className="flex space-x-1 bg-gray-100 p-1 rounded-lg w-fit">
          <button onClick={() => setActiveTab("hourly")} className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${activeTab === "hourly" ? "bg-white text-blue-600 shadow-sm" : "text-gray-600 hover:text-gray-900"}`}>
            시간대별 소음 그래프
          </button>
          <button onClick={() => setActiveTab("daily")} className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${activeTab === "daily" ? "bg-white text-blue-600 shadow-sm" : "text-gray-600 hover:text-gray-900"}`}>
            일별 소음 그래프
          </button>
        </div>

        <div className="mt-4">
          <h3 className="text-lg font-medium">{currentTitle}</h3>
          <div className="text-sm text-gray-600">데이터 포인트: {currentData.length}개</div>
        </div>
      </div>

      {!hasValidData ? (
        <div className="h-80 flex items-center justify-center bg-gray-50 rounded-lg">
          <div className="text-center text-gray-500">
            <div className="text-xl mb-2">📊</div>
            <div>선택하신 기간에 유효한 소음 데이터가 없습니다.</div>
            <div className="text-sm mt-1">다른 날짜 범위나 지역을 선택해보세요.</div>
          </div>
        </div>
      ) : (
        <div className="h-80">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={currentData} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="xaxis" tick={{ fontSize: 12 }} interval={activeTab === "hourly" ? "preserveEnd" : 0} />
              <YAxis
                type="number"
                domain={['dataMin - 5', 'dataMax + 5']}
                tickCount={8}
                allowDecimals={false}
                label={{ value: "Noise Level (dB)", angle: -90, position: "insideLeft" }}
              />
              <Tooltip
                formatter={(value) => {
                  if (typeof value !== "number") return ["데이터 없음", "평균 소음"];
                  return [Math.round(value).toString(), "평균 소음"];
                }}
                labelFormatter={(label) => `${activeTab === "hourly" ? "시간" : "날짜"}: ${label}${activeTab === "hourly" ? ":00" : "일"}`}
              />
              <Line type="monotone" dataKey="avgNoise" stroke="#2563eb" strokeWidth={3} dot={{ fill: "#2563eb", strokeWidth: 2, r: 4 }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      )}
    </Card>
  )
}
