"use client"

import { Card } from "@/components/ui/card";
import { RankItem, DeviationRankItem } from "@/types/ReportIndex";
import { TrendingUp, TrendingDown, TrendingUpDown } from 'lucide-react';
import React from "react";


type ListType = 'top' | 'bottom' | 'deviation';

// 순위 항목의 스타일을 반환하는 함수
function getRankItemStyle(listType: ListType, rank: number): string {

    const baseStyle = "flex items-center justify-between p-3 rounded-md font-medium text-gray-800 transition-all duration-200 ease-in-out hover:scale-[1.02] hover:shadow-md border";

    if (listType === 'top') {
        if (rank === 1) return `${baseStyle} bg-red-200 border-red-300`;
        if (rank === 2) return `${baseStyle} bg-red-100 border-red-200`;
        if (rank === 3) return `${baseStyle} bg-red-50 border-red-100`;
    }
    if (listType === 'bottom') {
        if (rank === 1) return `${baseStyle} bg-green-200 border-green-300`;
        if (rank === 2) return `${baseStyle} bg-green-100 border-green-200`;
        if (rank === 3) return `${baseStyle} bg-green-50 border-green-100`;
    }
    if (listType === 'deviation') {
        if (rank === 1) return `${baseStyle} bg-blue-200 border-blue-300`;
        if (rank === 2) return `${baseStyle} bg-blue-100 border-blue-200`;
        if (rank === 3) return `${baseStyle} bg-blue-50 border-blue-100`;
    }
    return "flex items-center justify-between p-3 rounded-md border border-transparent"; // 4위 이하는 투명 테두리
}

// 순위 숫자의 스타일을 반환하는 함수
function getRankBadgeStyle(listType: ListType): string {
    const baseStyle = "flex items-center justify-center w-6 h-6 rounded-full text-sm font-bold";
    if (listType === 'top') return `${baseStyle} bg-red-500/15 text-red-700`;
    if (listType === 'bottom') return `${baseStyle} bg-green-500/15 text-green-700`;
    if (listType === 'deviation') return `${baseStyle} bg-blue-500/15 text-blue-700`;
    return `${baseStyle} bg-gray-200 text-gray-800`;
}


// --- 이 파일 안에서만 사용할 내부 컴포넌트 ---

interface RankingProps {
  title: string;
  items: {
    rank: number;
    name: string;
    value: string;
  }[];
  valueLabel: string;
  listType: ListType;
}

function Ranking({ title, items, valueLabel, listType }: RankingProps) {
  const getIcon = () => {
    if (listType === 'top') return <TrendingUp className="text-red-500" size={22} />;
    if (listType === 'bottom') return <TrendingDown className="text-green-600" size={22} />;
    if (listType === 'deviation') return <TrendingUpDown className="text-blue-600" size={22} />;
    return null;
  };

  return (
    <Card className="p-6">
      <div className="flex items-center mb-4">
        <span className="mr-2">{getIcon()}</span>
        <h3 className="text-lg font-semibold text-gray-800">{title}</h3>
      </div>
      
      <div className="space-y-2">
        {items.map((item) => (
          <div key={item.rank} className={getRankItemStyle(listType, item.rank)}>
            <div className="flex items-center space-x-3">
              <span className={getRankBadgeStyle(listType)}>
                {item.rank}
              </span>
              <span>{item.name}</span>
            </div>
            <span className="font-semibold">
              {item.value} {valueLabel}
            </span>
          </div>
        ))}
      </div>
    </Card>
  )
}


// --- 최종적으로 export할 메인 컴포넌트 ---

interface RankingListsProps {
  topRankDtoList: RankItem[];
  bottomRankDtoList: RankItem[];
  deviationRankDtoList: DeviationRankItem[];
}

export default function RankingLists({ topRankDtoList, bottomRankDtoList, deviationRankDtoList }: RankingListsProps) {
  const topAreas = (topRankDtoList ?? []).map((item, index) => ({
    rank: index + 1,
    name: item.region,
    value: (item.avgNoise ?? 0).toFixed(1),
  }));

  const bottomAreas = (bottomRankDtoList ?? []).map((item, index) => ({
    rank: index + 1,
    name: item.region,
    value: (item.avgNoise ?? 0).toFixed(1),
  }));

  const deviationAreas = (deviationRankDtoList ?? []).map((item, index) => ({
    rank: index + 1,
    name: item.region,
    value: (item.deviation ?? 0).toFixed(1),
  }));

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <Ranking title="소음 상위 3개 지역" items={topAreas} valueLabel="dB" listType="top" />
      <Ranking title="소음 하위 3개 지역" items={bottomAreas} valueLabel="dB" listType="bottom" />
      <Ranking title="소음 편차 상위 3개 지역" items={deviationAreas} valueLabel="dB" listType="deviation" />
    </div>
  )
}
