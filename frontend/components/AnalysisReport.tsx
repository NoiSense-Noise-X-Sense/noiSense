"use client"

import { useState, useEffect, useRef } from "react"
import { format } from "date-fns"

// 중앙 타입 파일에서 모든 설계도를 가져옵니다.
import { ReportDataDto, DistrictDto } from "@/types/Report";

import FilterControls from "./analysisreport/FilterControls"
import KpiCards from "./analysisreport/KpiCards"
import RankingLists from "./analysisreport/RankingLists"
import MainChart from "./analysisreport/MainChart"
import CombinedHourlyChart from "./analysisreport/CombinedHourlyChart"
import CombinedDailyChart from "./analysisreport/CombinedDailyChart"

export default function AnalysisReport() {
  const endDate = new Date();
  const startDate = new Date();
  startDate.setMonth(endDate.getMonth() - 1);

  const [filters, setFilters] = useState({
    startDate: startDate,
    endDate: endDate,
    district: "all",
  });

  const [districts, setDistricts] = useState<DistrictDto[]>([]);
  const [isDistrictsLoading, setIsDistrictsLoading] = useState(true);

  const [reportData, setReportData] = useState<ReportDataDto | null>(null);
  const [isReportLoading, setIsReportLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const debounceTimeout = useRef<NodeJS.Timeout | null>(null);

  // 자치구 목록을 불러오는 로직
  useEffect(() => {
    const fetchDistricts = async () => {
      setIsDistrictsLoading(true);
      try {
        const response = await fetch("http://localhost:8080/api/v1/district/autonomousDistrict");
        if (!response.ok) throw new Error(`자치구 목록 로딩 실패: ${response.status}`);

        const result = await response.json();
        if (result.data && Array.isArray(result.data) && result.data.length > 0) {
          setDistricts(result.data);
          setError(null);
        } else if (result.success === false) {
          setError(`API 오류: ${result.message || "알 수 없는 오류"}`);
        } else {
          setError("자치구 데이터 형식이 올바르지 않습니다.");
        }
      } catch (e: any) {
        setError(e.message || "자치구 목록을 불러오는 중 에러 발생");
      } finally {
        setIsDistrictsLoading(false);
      }
    };
    fetchDistricts();
  }, []);

  // 리포트 데이터를 불러오는 로직
  useEffect(() => {
    if (debounceTimeout.current) clearTimeout(debounceTimeout.current);
    setIsReportLoading(true);
    setError(null);
    debounceTimeout.current = setTimeout(() => {
      const fetchReportData = async () => {
        if (!filters.startDate || !filters.endDate) {
          setError("날짜를 올바르게 선택해주세요.");
          setIsReportLoading(false);
          return;
        }
        const params = new URLSearchParams();
        params.append("startDate", format(filters.startDate, "yyyy-MM-dd"));
        params.append("endDate", format(filters.endDate, "yyyy-MM-dd"));
        params.append("autonomousDistrictCode", filters.district === "all" ? "all" : filters.district);

        try {
          const response = await fetch(`http://localhost:8080/api/v1/report/getReport?${params.toString()}`);
          if (!response.ok) throw new Error(`서버 응답 오류: ${response.status}`);
          const result = await response.json();
          setReportData(result.data || result);
        } catch (e: any) {
          setError(e.message || "리포트 데이터를 불러오는 데 실패했습니다.");
          setReportData(null);
        } finally {
          setIsReportLoading(false);
        }
      };
      fetchReportData();
    }, 300);

    return () => {
      if (debounceTimeout.current) clearTimeout(debounceTimeout.current);
    };
  }, [filters]);

  return (
    <div className="min-h-screen bg-gray-50 p-6 select-none">
      <div className="max-w-7xl mx-auto space-y-6">
        <h1 className="text-4xl font-bold text-gray-900 text-center">소음 데이터 리포트</h1>

        <div id="pdf-content">
          <FilterControls
            filters={filters}
            setFilters={setFilters}
            districts={districts}
            isLoading={isDistrictsLoading}
          />
          <div className="space-y-6 mt-6">
            {isReportLoading ? (
              <div className="text-center py-20 text-gray-500">리포트 데이터를 불러오는 중입니다...</div>
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
                <RankingLists
                  topRankDtoList={reportData.topRankDtoList || []}
                  bottomRankDtoList={reportData.bottomRankDtoList || []}
                  deviationRankDtoList={reportData.deviationRankDtoList || []}
                />
                {/* 1. 종합 데이터 차트: MainChart */}
                {/* hourlyData와 dailyData를 각각 전달하여 탭 기능을 활성화합니다. */}
                <MainChart
                  hourlyData={reportData.totalChartDto?.overallHourAvgNoiseData || []}
                  dailyData={reportData.totalChartDto?.overallDayAvgNoiseData || []}
                />

                {/* 2. 비교용 차트: CombinedHourlyChart (기존과 동일) */}
                {/* 이 차트들은 원래대로 둡니다. */}
                <CombinedHourlyChart
                  data={reportData.totalChartDto?.overallHourAvgNoiseData || []}
                />

                {/* 3. 비교용 차트: CombinedDailyChart (기존과 동일) */}
                <CombinedDailyChart
                  data={reportData.totalChartDto?.TrendPointDayOfWeekAvgNoiseData || []}
                />
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
