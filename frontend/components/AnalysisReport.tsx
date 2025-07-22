"use client"

import { useState, useEffect, useRef } from "react"
import { format } from "date-fns"

import FilterControls from "./analysisreport/FilterControls"
import KpiCards from "./analysisreport/KpiCards"
import RankingLists from "./analysisreport/RankingLists"
import MainChart from "./analysisreport/MainChart"
import CombinedHourlyChart from "./analysisreport/CombinedHourlyChart"
import CombinedDailyChart from "./analysisreport/CombinedDailyChart"

params.append("autonomousDistrict", filters.district);

export default function AnalysisReport() {
  // 상태 관리 (이전과 동일)
  const endDate = new Date();
  const startDate = new Date();
  startDate.setMonth(endDate.getMonth() - 1);

  const [filters, setFilters] = useState({
    startDate: startDate,
    endDate: endDate,
    district: "all",
  });

  const [reportData, setReportData] = useState<any | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const debounceTimeout = useRef<NodeJS.Timeout | null>(null);

  // 데이터 로딩 로직 (이전과 동일)
  useEffect(() => {
    if (debounceTimeout.current) clearTimeout(debounceTimeout.current);
    setIsLoading(true);
    setError(null);
    debounceTimeout.current = setTimeout(() => {
      const fetchReportData = async () => {
        if (!filters.startDate || !filters.endDate) {
          setError("날짜를 올바르게 선택해주세요.");
          setIsLoading(false);
          return;
        }
        const params = new URLSearchParams();
        params.append("startDate", format(filters.startDate, "yyyy-MM-dd"));
        params.append("endDate", format(filters.endDate, "yyyy-MM-dd"));
        params.append("autonomousDistrict", filters.district);

        try {
          const response = await fetch(`http://localhost:8080/api/v1/report/getReport?${params.toString()}`);
          if (!response.ok) {
            throw new Error(`서버 응답 오류: ${response.status}`);
          }
          const data = await response.json();
          setReportData(data);
        } catch (e: any) {
          setError(e.message || "데이터를 불러오는 데 실패했습니다.");
          setReportData(null);
        } finally {
          setIsLoading(false);
        }
      };
      fetchReportData();
    }, 300);
    return () => {
      if (debounceTimeout.current) clearTimeout(debounceTimeout.current);
    };
  }, [filters]);

  // UI 렌더링
  return (
    <div className="min-h-screen bg-gray-50 p-6 select-none">
      <div className="max-w-7xl mx-auto space-y-6">
        <h1 className="text-4xl font-bold text-gray-900 text-center">소음 데이터 리포트</h1>
        <div id="pdf-content">
          <FilterControls filters={filters} setFilters={setFilters} />
          <div className="space-y-6 mt-6">
            {isLoading ? (
              <div className="text-center py-20 text-gray-500">데이터를 불러오는 중입니다...</div>
            ) : error ? (
              <div className="text-center py-20 text-red-500 bg-red-50 rounded-lg p-4">오류: {error}</div>
            ) : reportData ? (
              <>
                <KpiCards
                  avgNoise={reportData.avgNoise}
                  perceivedNoise={reportData.perceivedNoise}
                  maxNoiseRegion={reportData.maxNoiseRegion}
                  maxNoiseTime={reportData.maxNoiseTime}
                  maxNoiseTimeValue={reportData.maxNoiseTimeValue}
                  selectedDistrict={filters.district}
                />
                {/* 데이터가 없을 경우를 대비해 || [] (기본값) 추가 */}
                <RankingLists
                  topRankDtoList={reportData.topRankDtoList || []}
                  bottomRankDtoList={reportData.bottomRankDtoList || []}
                  deviationRankDtoList={reportData.deviationRankDtoList || []}
                />

                {/* ✅ 데이터 접근 경로를 백엔드 DTO 구조에 정확하게 맞췄습니다. */}
                {/* ✅ Optional Chaining(?.)을 사용하여 reportData.totalChartDto가 null일 때 에러 방지 */}
                <MainChart data={reportData.totalChartDto?.overallDayAvgNoiseData || []} />
                <CombinedHourlyChart data={reportData.totalChartDto?.overallHourAvgNoiseData || []} />
                <CombinedDailyChart data={reportData.totalChartDto?.TrendPointDayOfWeekAvgNoiseData || []} />
              </>
            ) : (
              <div className="text-center py-20 text-gray-500">표시할 데이터가 없습니다.</div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
