// components/CombinedDailyChart.tsx

"use client"

import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts"
import { Card } from "@/components/ui/card"

export default function CombinedDailyChart() {
  const data = [
    { day: "Mon", top1: 75, top2: 72, top3: 69, bottom1: 52, bottom2: 55, bottom3: 58 },
    { day: "Tue", top1: 77, top2: 74, top3: 71, bottom1: 54, bottom2: 57, bottom3: 60 },
    { day: "Wed", top1: 74, top2: 71, top3: 68, bottom1: 51, bottom2: 54, bottom3: 57 },
    { day: "Thu", top1: 76, top2: 73, top3: 70, bottom1: 53, bottom2: 56, bottom3: 59 },
    { day: "Fri", top1: 80, top2: 77, top3: 74, bottom1: 57, bottom2: 60, bottom3: 63 },
    { day: "Sat", top1: 78, top2: 75, top3: 72, bottom1: 55, bottom2: 58, bottom3: 61 },
    { day: "Sun", top1: 72, top2: 69, top3: 66, bottom1: 49, bottom2: 52, bottom3: 55 },
  ]

  return (
    <Card className="p-6">
      <h2 className="text-xl font-semibold mb-4">요일별 소음 순위</h2>
      <div className="h-80">
        <ResponsiveContainer width="100%" height="100%">
          {/* ✅ 1. margin을 추가하여 차트 우측에 여백을 확보합니다. */}
          <LineChart
            data={data}
            margin={{ top: 5, right: 20, left: 0, bottom: 5 }}
          >
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="day" tick={{ fontSize: 12 }} />
            <YAxis
              label={{ value: "Average Noise Level (dB)", angle: -90, position: "insideLeft" }}
              tick={{ fontSize: 12 }}
            />
            <Tooltip formatter={(value) => [`${value} dB`, "Average Noise Level"]} />
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
