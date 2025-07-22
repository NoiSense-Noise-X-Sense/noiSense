// app/page.tsx (NoiseReport)

"use client"

import { useState, useEffect } from "react"
import FilterControls from "@/components/analysisreport/FilterControls"
import KpiCards from "@/components/analysisreport/KpiCards"
import RankingLists from "@/components/analysisreport/RankingLists"
import MainChart from "@/components/analysisreport/MainChart"
import CombinedHourlyChart from "@/components/analysisreport/CombinedHourlyChart"
import CombinedDailyChart from "@/components/analysisreport/CombinedDailyChart"

// ✅ 여기에 목업 데이터가 있습니다.
const mockReportDataForAll = {
    avgNoise: 55.8,
    perceivedNoise: 65.2,
    maxNoiseRegion: "강남구",
    maxNoiseTime: "2024-07-19 22:35:00",
    maxNoiseTimeValue: 78.5,
    topRankDtoList: [
        { region: "강남구", avgNoise: 70.1, deviation: 15.2 },
        { region: "서초구", avgNoise: 68.5, deviation: 12.1 },
        { region: "송파구", avgNoise: 67.2, deviation: 10.5 },
    ],
    bottomRankDtoList: [
        { region: "도봉구", avgNoise: 40.2, deviation: 5.1 },
        { region: "강북구", avgNoise: 42.1, deviation: 6.3 },
        { region: "중랑구", avgNoise: 43.5, deviation: 7.0 },
    ],
    deviationRankDtoList: [
        { region: "강남구", avgNoise: 70.1, deviation: 15.2 },
        { region: "영등포구", avgNoise: 60.1, deviation: 14.8 },
        { region: "관악구", avgNoise: 58.7, deviation: 13.9 },
    ]
};

const mockReportDataForDistrict = {
    avgNoise: 68.1,
    perceivedNoise: 72.4,
    maxNoiseRegion: "역삼동",
    maxNoiseTime: "2024-07-20 08:15:00",
    maxNoiseTimeValue: 85.2,
    topRankDtoList: [
        { region: "역삼동", avgNoise: 70.1, deviation: 15.2 },
        { region: "대치동", avgNoise: 68.5, deviation: 12.1 },
        { region: "삼성동", avgNoise: 67.0, deviation: 11.3 },
    ],
    bottomRankDtoList: [
        { region: "개포동", avgNoise: 40.2, deviation: 5.1 },
        { region: "수서동", avgNoise: 42.1, deviation: 6.3 },
        { region: "일원동", avgNoise: 43.0, deviation: 6.8 },
    ],
    deviationRankDtoList: [
        { region: "역삼동", avgNoise: 70.1, deviation: 15.2 },
        { region: "도곡동", avgNoise: 60.1, deviation: 14.8 },
        { region: "대치동", avgNoise: 58.9, deviation: 13.2 },
    ]
};

export default function NoiseReport() {
  const endDate = new Date();
  const startDate = new Date();
  startDate.setMonth(endDate.getMonth() - 1);

  const [filters, setFilters] = useState({
    startDate: startDate,
    endDate: endDate,
    district: "all",
  });

  const [reportData, setReportData] = useState<any | null>(null);

  useEffect(() => {
    console.log(`필터 변경: ${filters.district}. 데이터 다시 로드.`);
    if (filters.district === "all") {
        setReportData(mockReportDataForAll);
    } else {
        setReportData(mockReportDataForDistrict);
    }
  }, [filters]);

  return (
    <div className="min-h-screen bg-gray-50 p-6 select-none">
      <div className="max-w-7xl mx-auto space-y-6">
        <h1 className="text-4xl font-bold text-gray-900 text-center">소음 데이터 리포트</h1>

        {/* PDF로 변환할 모든 컨텐츠를 이 div로 감쌉니다. */}
        <div id="pdf-content">
          <FilterControls filters={filters} setFilters={setFilters} />

          {reportData ? (
            <div className="space-y-6 mt-6">
              <KpiCards
                avgNoise={reportData.avgNoise}
                perceivedNoise={reportData.perceivedNoise}
                maxNoiseRegion={reportData.maxNoiseRegion}
                maxNoiseTime={reportData.maxNoiseTime}
                maxNoiseTimeValue={reportData.maxNoiseTimeValue}
                selectedDistrict={filters.district}
              />
              <RankingLists
                topRankDtoList={reportData.topRankDtoList}
                bottomRankDtoList={reportData.bottomRankDtoList}
                deviationRankDtoList={reportData.deviationRankDtoList}
              />
              <MainChart />
              <CombinedHourlyChart />
              <CombinedDailyChart />
            </div>
          ) : (
            <div className="text-center py-10">Loading...</div>
          )}
        </div>
      </div>
    </div>
  )
}
