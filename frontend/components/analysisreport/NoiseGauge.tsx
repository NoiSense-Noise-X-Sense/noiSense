// components/NoiseGauge.tsx

"use client"

import React, { useState } from "react"
import { PieChart, Pie, Cell, ResponsiveContainer } from "recharts"

// ✅ 1. props 인터페이스에 maxValue를 추가합니다.
interface GaugeProps {
    value: number;
    maxValue: number; // 예: 소음(120), 시간(23)
}

const COLORS = ["#22c55e", "#f59e0b", "#ef4444"];
const data = [
    { name: "Low", value: 50 },
    { name: "Medium", value: 25 },
    { name: "High", value: 25 },
];

// ✅ 2. 바늘을 그리는 로직이 maxValue를 사용하도록 수정.
const needle = (value: number, maxValue: number, cx: number, cy: number, iR: number, oR: number, color: string) => {
    const safeValue = (typeof value !== 'number' || isNaN(value)) ? 0 : value;
    // 고정된 GAUGE_MAX_VALUE 대신, prop으로 받은 maxValue를 사용.
    const angle = 180 - (safeValue / maxValue) * 180; 
    
    const length = (iR + oR) / 2;
    const sin = Math.sin(-Math.PI / 180 * angle);
    const cos = Math.cos(-Math.PI / 180 * angle);
    
    const x0 = cx; const y0 = cy;
    const x1 = x0 + length * cos; const y1 = y0 + length * sin;

    return (
        <g shapeRendering="geometricPrecision"> 
            <path d={`M ${x0} ${y0} L ${x1} ${y1}`} stroke={color} strokeWidth="2" strokeLinecap="round" />
            <circle cx={x0} cy={y0} r={4} fill={color} stroke="none" />
        </g>
    );
};

// ✅ 3. 컴포넌트가 value와 maxValue를 모두 props로 받도록 수정.
export default function NoiseGauge({ value, maxValue }: GaugeProps) {
    return (
        <div className="w-full h-20">
            <ResponsiveContainer width="100%" height="100%">
                <PieChart margin={{ top: 0, right: 0, bottom: -30, left: 0 }}>
                    <Pie
                        dataKey="value"
                        startAngle={180}
                        endAngle={0}
                        data={data}
                        cx="50%"
                        cy="60%"
                        innerRadius="55%"
                        outerRadius="100%"
                        isAnimationActive={false}
                        label={(props) => {
                            const { cx, cy, innerRadius, outerRadius } = props;
                            // needle 함수에 maxValue를 전달.
                            return needle(value, maxValue, cx as number, cy as number, innerRadius as number, outerRadius as number, "#374151");
                        }}
                        labelLine={false}
                    >
                        {data.map((entry, index) => (
                            <Cell 
                                key={`cell-${index}`} 
                                fill={COLORS[index % COLORS.length]} 
                                stroke={"#FFFFFF"}
                                strokeWidth={2}
                            />
                        ))}
                    </Pie>
                </PieChart>
            </ResponsiveContainer>
        </div>
    );
}
