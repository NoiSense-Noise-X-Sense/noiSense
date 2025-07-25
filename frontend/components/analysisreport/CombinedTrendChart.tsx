"use client"

import {CartesianGrid, Legend, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts"
import {Card} from "@/components/ui/card"
import {TrendChartItem} from "@/types/ReportIndex" // 타입 경로는 맞게 수정해주세요

const chartConfigs = {
    hourly: {
        title: "시간대별 지역별 소음 추이",
        xAxisLabel: "시간",
    },
    dayOfWeek: {
        title: "요일별 지역별 소음 추이",
        xAxisLabel: "요일",
    },
}

interface CombinedTrendChartProps {
    readonly type: "hourly" | "dayOfWeek";
    readonly data: TrendChartItem[];
}

const dayOfWeekMap = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];

// Recharts에 사용하기 유용한 포맷으로 데이터 변경
const transformDataForChart = (type: string, data: TrendChartItem[]) => {
    if (!data || data.length === 0) {
        return {chartData: [], lineKeys: []};
    }
    const chartData = data.map(item => ({
        name: type === 'dayOfWeek' ? dayOfWeekMap[item.xaxis] : item.xaxis+'시',
        ...item.avgNoiseByRegion,
    }));
    const lineKeys = Object.keys(data[0].avgNoiseByRegion);

    return {chartData, lineKeys};
};

export default function CombinedHourlyChart({type, data}: CombinedTrendChartProps) {

    const config = chartConfigs[type];

    const {chartData, lineKeys} = transformDataForChart(type, data);

    const colors = ["#dc2626", "#ea580c", "#f59e0b", "#16a34a", "#0891b2", "#7c3aed"];

    return (
        <Card className="p-6">
            <h2 className="text-xl font-semibold mb-4">{config.title}</h2>
            <div className="h-96">
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={chartData}>
                        <CartesianGrid strokeDasharray="3 3"/>
                        <XAxis dataKey="name" tick={{fontSize: 12}}
                               label={{value: config.xAxisLabel, position: "insideBottomRight", offset: -5}}/>
                        <YAxis
                            domain={[
                                (dataMin:number) => (dataMin - 5),
                                (dataMax: number) => (dataMax * 1.1)
                            ]}
                            label={{value: "평균 소음 (dB)", angle: -90, position: "insideLeft"}}
                            tick={{fontSize: 12}}
                        />
                        <Tooltip
                            formatter={(value: number, name: string) => [`${value.toFixed(2)} dB`, name]}
                        />
                        <Legend/>

                        {/* 4. lineKeys 배열을 map으로 순회하며 동적으로 Line 컴포넌트를 그립니다. */}
                        {lineKeys.map((key, index) => (
                            <Line
                                key={key}
                                type="monotone"
                                dataKey={key}
                                name={key} // 범례에 표시될 이름
                                stroke={colors[index % colors.length]} // 순환하며 색상 적용
                                strokeWidth={2}
                            />
                        ))}
                    </LineChart>
                </ResponsiveContainer>
            </div>
        </Card>
    )
}