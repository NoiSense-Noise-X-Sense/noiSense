"use client"

import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts"
import { Card } from "@/components/ui/card"

interface ChartData {
  xaxis: string; // 반드시 'xaxis' 필드
  [region: string]: number | string; // 동적으로 지역명 key 허용
}

interface CombinedHourlyChartProps {
  data: ChartData[];
}

export default function CombinedHourlyChart({ data }: CombinedHourlyChartProps) {
  // 데이터가 있는지 확인
  const hasData = Array.isArray(data) && data.length > 0;

  // 동적 자치구 리스트 추출 
  const regionList = hasData ? Object.keys(data[0]).filter(key => key !== "xaxis") : [];

  return (
    <Card className="p-6">
      <h2 className="text-xl font-semibold mb-4">시간대별 소음 순위</h2>
      <div className="h-80">
        {hasData ? (
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={data} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" />
              {/* X축 key는 반드시 'xaxis' */}
              <XAxis dataKey="xaxis" tick={{ fontSize: 12 }} interval="preserveEnd" />
              <YAxis label={{ value: "Noise Level (dB)", angle: -90, position: "insideLeft" }} tick={{ fontSize: 12 }} />
              <Tooltip formatter={(value: number) => [`${value} dB`, "Noise Level"]} />
              <Legend />
              {/* 모든 지역(key)에 대해 Line 컴포넌트 자동 생성 */}
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
            시간대별 비교 데이터가 없습니다.
          </div>
        )}
      </div>
    </Card>
  );
}

// 색상 팔레트
function getColor(index: number): string {
  const colors = [
    "#dc2626", "#ea580c", "#2563eb", "#f59e0b", "#16a34a",
    "#0891b2", "#7c3aed", "#be185d", "#84cc16", "#d946ef",
    "#14b8a6", "#facc15",
  ];
  return colors[index % colors.length];
}
