"use client"

import { useState } from "react"
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts"
import { Card } from "@/components/ui/card"
import { ChartDataPoint } from "@/types/Report"

interface MainChartProps {
  hourlyData: ChartDataPoint[]  // overallHourAvgNoiseData
  dailyData: ChartDataPoint[]   // overallDayAvgNoiseData
}

export default function MainChart({ hourlyData, dailyData }: MainChartProps) {
  const [activeTab, setActiveTab] = useState("hourly")

  // 백엔드 데이터가 없으면 빈 배열로 초기화
  const safeHourlyData = hourlyData && hourlyData.length > 0 ? hourlyData : []
  const safeDailyData = dailyData && dailyData.length > 0 ? dailyData : []

  const currentData = activeTab === "hourly" ? safeHourlyData : safeDailyData
  const currentTitle = activeTab === "hourly" ? "시간대별 평균 소음" : "일별 평균 소음"

  // avgNoise 값이 null이 아닌 유효한 데이터가 하나라도 있는지 확인
  const hasValidData = currentData.some(d => d.avgNoise !== null);

  return (
    <Card className="p-6">
      <div className="mb-6">
        <div className="flex space-x-1 bg-gray-100 p-1 rounded-lg w-fit">
          <button
            onClick={() => setActiveTab("hourly")}
            className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
              activeTab === "hourly" ? "bg-white text-blue-600 shadow-sm" : "text-gray-600 hover:text-gray-900"
            }`}
          >
            시간대별 소음 그래프
          </button>
          <button
            onClick={() => setActiveTab("daily")}
            className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
              activeTab === "daily" ? "bg-white text-blue-600 shadow-sm" : "text-gray-600 hover:text-gray-900"
            }`}
          >
            일별 소음 그래프
          </button>
        </div>

        <div className="mt-4">
          <h3 className="text-lg font-medium">{currentTitle}</h3>
          <div className="text-sm text-gray-600">
            데이터 포인트: {currentData.length}개
          </div>
        </div>
      </div>

      {/* 데이터 배열 자체가 비거나, 유효한 avgNoise 값이 하나도 없을 때 메시지 표시 */}
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
            <LineChart
              data={currentData}
              margin={{ top: 5, right: 20, left: 0, bottom: 5 }}
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis
                dataKey="xAxis"
                tick={{ fontSize: 12 }}
                interval={activeTab === "hourly" ? "preserveEnd" : 0}
              />
              <YAxis
                label={{ value: "Noise Level (dB)", angle: -90, position: "insideLeft" }}
                tick={{ fontSize: 12 }}
                domain={['0', '90']} // Y축 범위를 좀 더 여유롭게 설정
              />
              <Tooltip
                // 값이 null일 경우 "데이터 없음"을 표시하도록 수정
                formatter={(value: number | null, name: string) => {
                  if (value === null) {
                    return ["데이터 없음", "평균 소음"];
                  }
                  return [`${value.toFixed(1)} dB`, "평균 소음"];
                }}
                labelFormatter={(label) => `${activeTab === "hourly" ? "시간" : "날짜"}: ${label}${activeTab === "hourly" ? ":00" : "일"}`}
              />
              <Line
                type="monotone"
                dataKey="avgNoise"
                stroke="#2563eb"
                strokeWidth={3}
                dot={{ fill: "#2563eb", strokeWidth: 2, r: 4 }}
                // connectNulls는 기본값이 false이므로, null 데이터에서 선이 끊어지는 것이 올바른 동작
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      )}
    </Card>
  )
}
