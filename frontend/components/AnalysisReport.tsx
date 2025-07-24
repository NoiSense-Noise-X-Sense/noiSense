"use client"

import { useState, useEffect, useRef } from "react"
import { format } from "date-fns"

import { ReportDataDto, DistrictDto, ChartDataPoint } from "@/types/Report";

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

  // 원본 데이터를 저장할 상태
  const [reportData, setReportData] = useState<ReportDataDto | null>(null);

  const [isReportLoading, setIsReportLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const debounceTimeout = useRef<NodeJS.Timeout | null>(null);



  // 자치구 목록 로딩 (기존과 동일)
  useEffect(() => {
    const fetchDistricts = async () => {
      setIsDistrictsLoading(true);
      try {
        const response = await fetch("http://localhost:8080/api/v1/district/autonomousDistrict");
        if (!response.ok) throw new Error(`자치구 목록 로딩 실패: ${response.status}`);
        const result = await response.json();
        setDistricts(result.data || []);
      } catch (e: any) {
        setError(e.message || "자치구 목록을 불러오는 중 에러 발생");
      } finally {
        setIsDistrictsLoading(false);
      }
    };
    fetchDistricts();
  }, []);

  // 리포트 데이터 로딩 로직 (가공 부분 제거)
  useEffect(() => {
    if (debounceTimeout.current) clearTimeout(debounceTimeout.current);

    const fetchReportData = async () => {
      setIsReportLoading(true);
      setError(null);
      setReportData(null); // 데이터 초기화

      if (!filters.startDate || !filters.endDate) {
        setError("날짜를 올바르게 선택해주세요.");
        setIsReportLoading(false);
        return;
      }

      const params = new URLSearchParams({
        startDate: format(filters.startDate, "yyyy-MM-dd"),
        endDate: format(filters.endDate, "yyyy-MM-dd"),
        autonomousDistrictCode: filters.district,
      });

      try {
        const response = await fetch(`http://localhost:8080/api/v1/report/getReport?${params.toString()}`);
        if (!response.ok) throw new Error(`서버 응답 오류: ${response.status}`);
        const result = await response.json();
        setReportData(result.data || result); // 원본 데이터를 그대로 저장

      } catch (e: any) {
        setError(e.message || "리포트 데이터를 불러오는 데 실패했습니다.");
        setReportData(null);
      } finally {
        setIsReportLoading(false);
      }
    };

    debounceTimeout.current = setTimeout(fetchReportData, 300);

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

                {/* --- ✨✨✨ 여기가 최종 수정 부분입니다 ✨✨✨ --- */}
                {/*
                  데이터를 MainChart로 전달하기 직전에, .map()을 사용하여
                  MainChart가 기대하는 'xAxis' (대문자 A) 키를 만들어줍니다.
                */}
                <MainChart
                  hourlyData={reportData.totalChartDto?.overallHourAvgNoiseData?.map(d => ({ ...d, xAxis: d.hour })) || []}
                  dailyData={reportData.totalChartDto?.overallDayAvgNoiseData?.map(d => ({ ...d, xAxis: d.day })) || []}
                />
                {/* --- 여기까지가 최종 수정 부분입니다 --- */}

                {/* 다른 차트들은 원본 데이터를 그대로 사용해도 괜찮습니다. */}
                <CombinedHourlyChart
                  data={
                    (reportData.totalChartDto?.trendPointHourAvgNoiseData || [])
                      .map(item =>
                        item && item.avgNoiseByRegion
                          ? { ...item.avgNoiseByRegion, xaxis: item.xaxis }
                          : {}
                      )
                  }
                />
                <CombinedDailyChart
                  data={
                    (reportData.totalChartDto?.trendPointDayOfWeekAvgNoiseData || [])
                      .map(item =>
                        item && item.avgNoiseByRegion
                          ? { ...item.avgNoiseByRegion, xaxis: item.xaxis }
                          : {}
                      )
                  }
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
