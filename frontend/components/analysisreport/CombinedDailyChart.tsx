"use client"

import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts"
import { Card } from "@/components/ui/card"

interface ChartData {
  day: string;
  [key: string]: any;
}

interface CombinedDailyChartProps {
  data: ChartData[];
}

export default function CombinedDailyChart({ data }: CombinedDailyChartProps) {

  console.log("CombinedDailyChart data:", data);


  const hasData = data && data.length > 0;

  return (
    <Card className="p-6">

      <h2 className="text-xl font-semibold mb-4">요일별 소음 순위</h2>
      <div className="h-80">
        {hasData ? (
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={data} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="xaxis" tick={{ fontSize: 12 }} />
              <YAxis label={{ value: "Average Noise Level (dB)", angle: -90, position: "insideLeft" }} tick={{ fontSize: 12 }} />
              <Tooltip formatter={(value: number) => [`${value.toFixed(1)} dB`, "평균 소음"]} />
              <Legend />
              <Line type="monotone" dataKey="top1" stroke="#dc2626" strokeWidth={2} name="Top 1" connectNulls />
              <Line type="monotone" dataKey="top2" stroke="#ea580c" strokeWidth={2} name="Top 2" connectNulls />
              <Line type="monotone" dataKey="top3" stroke="#f59e0b" strokeWidth={2} name="Top 3" connectNulls />
              <Line type="monotone" dataKey="bottom1" stroke="#16a34a" strokeWidth={2} strokeDasharray="5 5" name="Bottom 1" connectNulls />
              <Line type="monotone" dataKey="bottom2" stroke="#0891b2" strokeWidth={2} strokeDasharray="5 5" name="Bottom 2" connectNulls />
              <Line type="monotone" dataKey="bottom3" stroke="#7c3aed" strokeWidth={2} strokeDasharray="5 5" name="Bottom 3" connectNulls />
            </LineChart>
          </ResponsiveContainer>
        ) : (
          <div className="flex items-center justify-center h-full text-gray-500">
            요일별 비교 데이터가 없습니다.
          </div>
        )}
      </div>
    </Card>
  )
}
