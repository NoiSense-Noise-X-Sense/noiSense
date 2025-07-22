// components/CombinedHourlyChart.tsx

"use client"

import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts"
import { Card } from "@/components/ui/card"

export default function CombinedHourlyChart() {
  const data = [
    { hour: "00:00", top1: 78, top2: 75, top3: 72, bottom1: 45, bottom2: 48, bottom3: 51 },
    { hour: "03:00", top1: 75, top2: 72, top3: 69, bottom1: 42, bottom2: 45, bottom3: 48 },
    { hour: "06:00", top1: 82, top2: 79, top3: 76, bottom1: 55, bottom2: 58, bottom3: 61 },
    { hour: "09:00", top1: 85, top2: 82, top3: 79, bottom1: 68, bottom2: 71, bottom3: 74 },
    { hour: "12:00", top1: 88, top2: 85, top3: 82, bottom1: 72, bottom2: 75, bottom3: 78 },
    { hour: "15:00", top1: 86, top2: 83, top3: 80, bottom1: 70, bottom2: 73, bottom3: 76 },
    { hour: "18:00", top1: 90, top2: 87, top3: 84, bottom1: 75, bottom2: 78, bottom3: 81 },
    { hour: "21:00", top1: 83, top2: 80, top3: 77, bottom1: 65, bottom2: 68, bottom3: 71 },
    { hour: "24:00", top1: 76, top2: 73, top3: 70, bottom1: 48, bottom2: 51, bottom3: 54 },
  ]

  return (
    <Card className="p-6">
      <h2 className="text-xl font-semibold mb-4">시간대별 소음 순위</h2>
      <div className="h-80">
        <ResponsiveContainer width="100%" height="100%">
          {/* ✅ 1. margin을 추가하여 차트 우측에 여백을 확보합니다. */}
          <LineChart
            data={data}
            margin={{ top: 5, right: 20, left: 0, bottom: 5 }}
          >
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis
              dataKey="hour"
              tick={{ fontSize: 12 }}
              // ✅ 2. interval을 "preserveEnd"로 설정하여 마지막 라벨을 항상 표시합니다.
              interval="preserveEnd"
            />
            <YAxis label={{ value: "Noise Level (dB)", angle: -90, position: "insideLeft" }} tick={{ fontSize: 12 }} />
            <Tooltip formatter={(value) => [`${value} dB`, "Noise Level"]} />
            <Legend />

            {/* Top 3 Lines - Solid */}
            <Line type="monotone" dataKey="top1" stroke="#dc2626" strokeWidth={2} name="Top 1" />
            <Line type="monotone" dataKey="top2" stroke="#ea580c" strokeWidth={2} name="Top 2" />
            <Line type="monotone" dataKey="top3" stroke="#f59e0b" strokeWidth={2} name="Top 3" />

            {/* Bottom 3 Lines - Dashed */}
            <Line
              type="monotone"
              dataKey="bottom1"
              stroke="#16a34a"
              strokeWidth={2}
              strokeDasharray="5 5"
              name="Bottom 1"
            />
            <Line
              type="monotone"
              dataKey="bottom2"
              stroke="#0891b2"
              strokeWidth={2}
              strokeDasharray="5 5"
              name="Bottom 2"
            />
            <Line
              type="monotone"
              dataKey="bottom3"
              stroke="#7c3aed"
              strokeWidth={2}
              strokeDasharray="5 5"
              name="Bottom 3"
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </Card>
  )
}
