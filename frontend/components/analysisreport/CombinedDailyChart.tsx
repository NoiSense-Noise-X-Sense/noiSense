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
  // 지역명 리스트 추출
  const regionList = hasData ? Object.keys(data[0]).filter(key => key !== "xaxis") : [];

  // 요일 숫자를 한글 요일로 변환하는 매핑
  const dayMap: { [key: string]: string } = {
    "1": "월",
    "2": "화",
    "3": "수",
    "4": "목",
    "5": "금",
    "6": "토",
    "7": "일",
  };

  return (
    <Card className="p-6">

      <h2 className="text-xl font-semibold mb-4">요일별 소음 순위</h2>
      <div className="h-80">
        {hasData ? (
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={data} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis
                dataKey="xaxis"
                tick={{ fontSize: 12 }}
                tickFormatter={(value) => dayMap[value] || value}
              />
              <YAxis
                type="number"
                domain={['dataMin - 5', 'dataMax + 5']}
                tickCount={8}
                allowDecimals={false}
                label={{ value: "Noise Level (dB)", angle: -90, position: "insideLeft" }}
              />
              <Tooltip formatter={(value: number) => [`${value.toFixed(1)} dB`, "평균 소음"]} />
              <Legend />
              {/* 지역명별로 Line 생성 */}
              {regionList.map((regionName, idx) => (
                <Line
                  key={regionName}
                  type="monotone"
                  dataKey={regionName}
                  strokeWidth={2}
                  connectNulls
                  stroke={getColor(idx)}
                  name={regionName}
                />
              ))}
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

// 색상 팔레트 함수
function getColor(index: number): string {
  const colors = [
    "#dc2626", "#ea580c", "#2563eb", "#f59e0b", "#16a34a",
    "#0891b2", "#7c3aed", "#be185d", "#84cc16", "#d946ef",
    "#14b8a6", "#facc15",
  ];
  return colors[index % colors.length];
}
