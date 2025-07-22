// components/MainChart.tsx

"use client"

import { useState } from "react"
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts"
import { Card } from "@/components/ui/card"

export default function MainChart() {
  const [activeTab, setActiveTab] = useState("hourly")

  const noiseValues = [
    45, 42, 40, 42, 45, 48, 55, 68, 70, 72, 72, 74,
    76, 70, 72, 74, 75, 78, 80, 65, 48, 70, 24, 45 // 총 24개
  ];

  const times = Array.from({ length: 24 }, (_, i) =>
    i.toString().padStart(2, "0") + ":00"
  );

  const hourlyData = times.map((time, idx) => ({
    time,
    noise: noiseValues[idx],
  }));

  const dailyData = [
    { day: "Mon", noise: 65 },
    { day: "Tue", noise: 67 },
    { day: "Wed", noise: 64 },
    { day: "Thu", noise: 66 },
    { day: "Fri", noise: 70 },
    { day: "Sat", noise: 68 },
    { day: "Sun", noise: 62 },
  ]

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
            요일별 소음 그래프
          </button>
        </div>
      </div>

      <div className="h-80">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart
            data={activeTab === "hourly" ? hourlyData : dailyData}
            margin={{ top: 5, right: 20, left: 0, bottom: 5 }}
          >
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis
              dataKey={activeTab === "hourly" ? "time" : "day"}
              tick={{ fontSize: 12 }}
              // ✅ "preserveEnd"를 사용하여 마지막 레이블("23:00")을 항상 표시합니다.
              interval={activeTab === "hourly" ? "preserveEnd" : 0}
            />
            <YAxis
              label={{ value: "Noise Level (dB)", angle: -90, position: "insideLeft" }}
              tick={{ fontSize: 12 }}
            />
            <Tooltip
              formatter={(value) => [`${value} dB`, "Noise Level"]}
              labelFormatter={(label) => `${activeTab === "hourly" ? "Time" : "Day"}: ${label}`}
            />
            <Line
              type="monotone"
              dataKey="noise"
              stroke="#2563eb"
              strokeWidth={3}
              dot={{ fill: "#2563eb", strokeWidth: 2, r: 4 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </Card>
  )
}
