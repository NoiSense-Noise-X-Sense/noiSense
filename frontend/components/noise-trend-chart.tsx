"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts"

const data = [
  { year: 2020, count: 2987 },
  { year: 2021, count: 3104 },
  { year: 2022, count: 3265 },
  { year: 2023, count: 3350 },
  { year: 2024, count: 3422 },
]

export default function NoiseTrendChart() {
  return (
    <Card className="w-full shadow-xl border-0 bg-white hover:shadow-2xl transition-all duration-300">
      <CardHeader>
        <CardTitle className="text-xl font-bold text-gray-900">2020 ~ 2024 소음진동 민원 추이</CardTitle>
        <p className="text-sm text-gray-600 mt-2">최근 5년간 소음 관련 민원 접수 현황</p>
      </CardHeader>
      <CardContent className="h-[400px]">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={data} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
            <XAxis dataKey="year" stroke="#6B7280" fontSize={12} tickLine={false} axisLine={false} />
            <YAxis
              stroke="#6B7280"
              fontSize={12}
              tickLine={false}
              axisLine={false}
              tickFormatter={(value) => `${value.toLocaleString()}`}
            />
            <Tooltip
              formatter={(value) => [`${value.toLocaleString()}건`, "민원 수"]}
              labelFormatter={(label) => `${label}년`}
              contentStyle={{
                backgroundColor: "white",
                border: "1px solid #E5E7EB",
                borderRadius: "8px",
                boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.1)",
              }}
            />
            <Bar dataKey="count" fill="#6366f1" radius={[4, 4, 0, 0]} stroke="#4f46e5" strokeWidth={1} />
          </BarChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  )
}
