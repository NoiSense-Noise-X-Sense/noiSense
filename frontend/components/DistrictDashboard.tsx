// 통합된 버전: 더미데이터 제거 + API 연동 적용
'use client';

import { useState, useEffect, useRef, useMemo } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardTitle } from '@/components/ui/card';
import { Volume2, Moon, TrendingUp } from 'lucide-react';
import { Switch } from '@/components/ui/switch';
import { Label as UILabel } from '@/components/ui/label';
import {
  BarChart,
  Bar,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import {
  fetchDistrictList,
  fetchSummary,
  fetchHourly,
  fetchYearly,
  fetchComplaints,
} from '@/lib/api/dashboard';

type KeywordCount = {
  keyword: string;
  count: number;
};

type District = {
  code: string;
  nameKo: string;
  nameEn: string;
};

export default function DistrictDashboard({ selectedDistrict: initialDistrict }: { selectedDistrict: string }) {
  // DB에서 받아온 자치구 목록
  const [districts, setDistricts] = useState<District[]>([]);
  // 현재 선택된 자치구(이름, nameKo)
  const [selectedDistrict, setSelectedDistrict] = useState(initialDistrict);
  const [autoScroll, setAutoScroll] = useState(true);
  const scrollContainerRef = useRef<HTMLDivElement>(null);
  const scrollIntervalRef = useRef<NodeJS.Timeout | null>(null);
  const [districtData, setDistrictData] = useState<any>(null);

  // 자치구 목록 fetch (최초 1회)
  useEffect(() => {
    fetchDistrictList().then((list: District[]) => {
      list.sort((a, b) => a.nameKo.localeCompare(b.nameKo, 'ko-KR'));
      setDistricts(list);
    });
  }, []);


  // allDistricts 동적 생성 (항상 최신 구 이름 목록)
  const allDistricts = useMemo(() => districts.map(d => d.nameKo), [districts]);

  // nameKo → code 변환 함수
  const getDistrictCode = (nameKo: string) => {
    const found = districts.find(d => d.nameKo === nameKo);
    return found?.code ?? '';
  };

  // 데이터 fetch (선택된 구가 바뀌거나 구 목록이 바뀔 때)
  useEffect(() => {
    if (districts.length === 0) return;
    const fetchData = async () => {
      try {
        const code = getDistrictCode(selectedDistrict);
        if (!code) return;
        const [summary, hourly, yearly, complaints] = await Promise.all([
          fetchSummary(code),
          fetchHourly(code),
          fetchYearly(code),
          fetchComplaints(code),
        ]);
        setDistrictData({
          avgNoise: {
            value: parseFloat(summary.avgNoise),
            analysisPeriod: `${summary.startDate} ~ ${summary.endDate}`,
          },
          peakTime: {
            time: `${summary.peakHour}시`,
            noise: summary.peakNoise,
            analysisPeriod: `${summary.startDate} ~ ${summary.endDate}`,
          },
          quietTime: {
            time: `${summary.calmHour}시`,
            noise: summary.calmNoise,
            analysisPeriod: `${summary.startDate} ~ ${summary.endDate}`,
          },
          keywords: summary.topKeywords,
          noiseTrendData: hourly.map((h: any) => ({
            hour: `${h.hour.toString().padStart(2, '0')}시`,
            실시간: h.avgDay,
            일주일평균: h.avgWeek,
            한달평균: h.avgMonth,
          })),
          yearlyAvgNoise: yearly.reduce((acc: any, cur: any) => {
            acc[cur.year] = { seoul: cur.avgSeoul, district: cur.avgDistrict };
            return acc;
          }, {}),
          yearlyComplaints: complaints.reduce((acc: any, cur: any) => {
            acc[cur.year] = cur.count;
            return acc;
          }, {}),
        });
      } catch (err) {
        console.error('데이터 로딩 실패:', err);
      }
    };
    fetchData();
  }, [selectedDistrict, districts]);

  // 자동 순환 로직
  useEffect(() => {
    if (autoScroll && allDistricts.length > 0) {
      scrollIntervalRef.current = setInterval(() => {
        setSelectedDistrict(prev => {
          const idx = allDistricts.indexOf(prev);
          const nextIdx = (idx + 1) % allDistricts.length;
          return allDistricts[nextIdx] ?? prev;
        });
      }, 5000);
    } else {
      if (scrollIntervalRef.current) clearInterval(scrollIntervalRef.current);
    }
    return () => {
      if (scrollIntervalRef.current) clearInterval(scrollIntervalRef.current);
    };
  }, [autoScroll, allDistricts]);

  const handleDistrictClick = (district: string) => {
    setAutoScroll(false);
    setSelectedDistrict(district);
  };

  if (!districtData)
    return (
      <div className="flex flex-col items-center justify-center min-h-[300px] w-full">
        <div className="relative mb-3 flex items-center justify-center">
          <img
            src="/placeholder-logo.png"
            alt="로딩"
            className="w-20 h-20 opacity-60 animate-pulse"
          />
          <svg
            className="absolute animate-spin h-10 w-10 text-blue-400"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            ></circle>
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"></path>
          </svg>
        </div>
        <div className="text-gray-400 mt-2 text-base font-medium">
          소음 데이터를 불러오는 중입니다...
        </div>
        <div className="text-xs text-gray-300 mt-1">잠시만 기다려주세요</div>
      </div>
    );

  const yearlyComplaintsChartData = Object.entries(districtData.yearlyComplaints).map(
    ([year, count]) => ({ year, '민원 건수': count })
  );

  const yearlyAvgNoiseChartData = Object.entries(districtData.yearlyAvgNoise).map(
    ([year, data]: any) => ({
      year,
      '서울시 평균': data.seoul,
      [`${selectedDistrict} 평균`]: data.district,
    })
  );

  return (
    <div className="flex h-[calc(100vh-64px)] bg-gray-50">
      {/* Left Sidebar */}
      <div className="w-64 bg-white border-r border-gray-200 p-4 flex flex-col">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold text-gray-800">서울시 25개구</h2>
          <div className="flex items-center space-x-2">
            <UILabel htmlFor="auto-scroll" className="text-sm text-gray-600">
              자동 순환
            </UILabel>
            <Switch id="auto-scroll" checked={autoScroll} onCheckedChange={setAutoScroll} />
          </div>
        </div>
        <div ref={scrollContainerRef} className="flex-1 overflow-y-auto space-y-1">
          {allDistricts.map(district => (
            <Button
              key={district}
              variant="ghost"
              className={`w-full justify-start text-left px-3 py-2 rounded-lg ${
                selectedDistrict === district
                  ? 'bg-gradient-to-r from-blue-500 to-purple-600 text-white shadow-md'
                  : 'text-gray-700 hover:bg-gray-100'
              }`}
              onClick={() => handleDistrictClick(district)}
            >
              <Volume2 className="h-4 w-4 mr-2" />
              {district}
            </Button>
          ))}
        </div>
      </div>

      {/* Right Main Dashboard */}
      <div className="flex-1 p-4 flex flex-col gap-3 overflow-auto">
        {/* Main Grid Layout with explicit rows */}
        <div
          className="grid grid-cols-1 lg:grid-cols-5 gap-4"
          style={{ gridTemplateRows: 'auto auto' }}
        >
          {/* Selected District Info - spans 2 rows */}
          <Card className="col-span-1 lg:col-span-2 row-span-2 flex flex-col items-center justify-center p-4">
            <div className="flex items-center gap-2 mb-3">
              <Volume2 className="h-6 w-6 text-gray-600" />
              <CardTitle className="text-lg font-semibold">{selectedDistrict}</CardTitle>
            </div>
            <div className="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center text-gray-400 text-3xl font-bold mb-3">
              {selectedDistrict.charAt(0)}
            </div>
            <p className="text-sm text-gray-500 text-center">
              {selectedDistrict}의 소음 현황을 분석합니다.
            </p>
          </Card>

          {/* Key Metrics - Top Row */}
          <Card className="col-span-1 p-4">
            <div className="flex items-center gap-2 mb-2">
              <div className="flex items-center justify-center w-8 h-8 rounded-full bg-green-100 text-green-600">
                <Volume2 className="h-4 w-4" />
              </div>
              <p className="text-sm text-gray-600">한달 평균 소음</p>
            </div>
            <p className="text-2xl font-bold text-gray-900 mb-1">
              {districtData.avgNoise.value} dB
            </p>
            <p className="text-xs text-gray-500">{districtData.avgNoise.analysisPeriod}</p>
          </Card>

          <Card className="col-span-1 p-4">
            <div className="flex items-center gap-2 mb-2">
              <div className="flex items-center justify-center w-8 h-8 rounded-full bg-orange-100 text-orange-600">
                <TrendingUp className="h-4 w-4" />
              </div>
              <p className="text-sm text-gray-600">소음 집중 시간</p>
            </div>
            <p className="text-2xl font-bold text-gray-900 mb-1">
              {districtData.peakTime.time} ({districtData.peakTime.noise} dB)
            </p>
            <p className="text-xs text-gray-500">{districtData.peakTime.analysisPeriod}</p>
          </Card>

          <Card className="col-span-1 p-4">
            <div className="flex items-center gap-2 mb-2">
              <div className="flex items-center justify-center w-8 h-8 rounded-full bg-purple-100 text-purple-600">
                <Moon className="h-4 w-4" />
              </div>
              <p className="text-sm text-gray-600">소음 안정 시간</p>
            </div>
            <p className="text-2xl font-bold text-gray-900 mb-1">
              {districtData.quietTime.time} ({districtData.quietTime.noise} dB)
            </p>
            <p className="text-xs text-gray-500">{districtData.quietTime.analysisPeriod}</p>
          </Card>

          {/* TOP Keywords - Second Row */}
          <Card className="col-span-1 lg:col-span-1 py-1 px-2 min-h-[60px] h-36 flex flex-col">
            <CardTitle className="text-sm font-semibold mb-1 text-left">
              {selectedDistrict}의 TOP 키워드
            </CardTitle>
            <div className="flex-1 flex flex-wrap gap-2 justify-center items-center">
              {(() => {
                if (!districtData.keywords || districtData.keywords.length === 0)
                  return <span>데이터 없음</span>;

                const max = Math.max(...districtData.keywords.map((k: KeywordCount) => k.count));
                const min = Math.min(...districtData.keywords.map((k: KeywordCount) => k.count));

                const range = max - min || 1;

                return districtData.keywords.map((kw: any, idx: number) => {
                  const norm = (kw.count - min) / range;
                  let textSize = 'text-base';
                  let color = 'text-gray-500';

                  if (norm >= 0.8) {
                    textSize = 'text-3xl';
                    color = 'text-red-600';
                  } else if (norm >= 0.6) {
                    textSize = 'text-2xl';
                    color = 'text-orange-500';
                  } else if (norm >= 0.4) {
                    textSize = 'text-xl';
                    color = 'text-yellow-500';
                  } else if (norm >= 0.2) {
                    textSize = 'text-lg';
                    color = 'text-green-500';
                  }

                  return (
                    <span key={idx} className={`${textSize} ${color} font-semibold`}>
                      {kw.keyword}
                    </span>
                  );
                });
              })()}
            </div>
          </Card>

          {/* Yearly Complaints Chart - Second Row */}
          <Card className="col-span-1 lg:col-span-2 py-1 px-2 min-h-[60px] h-36">
            <CardTitle className="text-sm font-semibold mb-0">2020 ~ 2024 소음민원 추이</CardTitle>
            <p className="text-xs text-gray-500 mb-1">최근 5년간 소음 관련 민원 접수 현황</p>
            <div className="h-full mt-1">
              {yearlyComplaintsChartData.length > 0 ? (
                <ResponsiveContainer width="100%" height="75%">
                  <BarChart data={yearlyComplaintsChartData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="year" />
                    <YAxis />
                    <Tooltip formatter={value => [`${value}건`, '민원 건수']} />
                    <Bar
                      dataKey="민원 건수"
                      fill="#3b82f6"
                      label={{
                        position: 'inside',
                        fontSize: 10,
                        fill: 'white',
                        formatter: value => `${value}건`,
                      }}
                    />
                  </BarChart>
                </ResponsiveContainer>
              ) : (
                <div className="flex items-center justify-center h-full text-gray-500">
                  데이터 로딩 중...
                </div>
              )}
            </div>
          </Card>
        </div>

        {/* Bottom Row: 24h Noise Trend + Seoul vs District Avg Noise */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
          <Card className="col-span-1 p-4 flex flex-col">
            <CardTitle className="text-base font-semibold mb-3">
              {selectedDistrict} 소음 추이
            </CardTitle>
            <div className="h-48">
              {districtData.noiseTrendData.length > 0 ? (
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={districtData.noiseTrendData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                    <XAxis
                      dataKey="hour"
                      tick={{ fontSize: 10 }}
                      tickLine={false}
                      axisLine={false}
                    />
                    <YAxis
                      tick={{ fontSize: 10 }}
                      tickLine={false}
                      axisLine={false}
                      domain={['dataMin - 5', 'dataMax + 5']}
                    />
                    <Tooltip
                      formatter={value => [`${value} dB`, '소음']}
                      labelStyle={{ color: '#374151' }}
                      contentStyle={{
                        backgroundColor: 'white',
                        border: '1px solid #e5e7eb',
                        borderRadius: '8px',
                        boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
                      }}
                    />
                    <Line
                      type="monotone"
                      dataKey="실시간"
                      stroke="#f97316"
                      strokeWidth={3}
                      dot={{ fill: '#f97316', strokeWidth: 2, r: 4 }}
                      activeDot={{ r: 6, stroke: '#f97316', strokeWidth: 2 }}
                    />
                    <Line
                      type="monotone"
                      dataKey="일주일평균"
                      stroke="#eab308"
                      strokeWidth={3}
                      dot={{ fill: '#eab308', strokeWidth: 2, r: 4 }}
                      activeDot={{ r: 6, stroke: '#eab308', strokeWidth: 2 }}
                    />
                    <Line
                      type="monotone"
                      dataKey="한달평균"
                      stroke="#a855f7"
                      strokeWidth={3}
                      dot={{ fill: '#a855f7', strokeWidth: 2, r: 4 }}
                      activeDot={{ r: 6, stroke: '#a855f7', strokeWidth: 2 }}
                    />
                  </LineChart>
                </ResponsiveContainer>
              ) : (
                <div className="flex items-center justify-center h-full text-gray-500">
                  데이터 로딩 중...
                </div>
              )}
            </div>
            <div className="flex justify-center gap-6 mt-3 text-xs">
              <div className="flex items-center gap-2">
                <span className="w-3 h-3 bg-orange-500 rounded-full"></span>
                <span className="font-medium">전일</span>
              </div>
              <div className="flex items-center gap-2">
                <span className="w-3 h-3 bg-yellow-500 rounded-full"></span>
                <span className="font-medium">일주일 평균</span>
              </div>
              <div className="flex items-center gap-1">
                <span className="w-3 h-3 bg-purple-500 rounded-full"></span>
                <span className="font-medium">한달 평균</span>
              </div>
            </div>
          </Card>

          <Card className="col-span-1 p-4 flex flex-col h-74">
            <CardTitle className="text-base font-semibold mb-2">
              연도별 평균 소음 (서울시 vs {selectedDistrict})
            </CardTitle>
            <div className="h-full mt-2">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart
                  data={yearlyAvgNoiseChartData}
                  margin={{ top: 10, right: 20, left: 0, bottom: 0 }}
                >
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="year" tick={{ fontSize: 12 }} />
                  <YAxis tick={{ fontSize: 12 }} />
                  <Tooltip formatter={value => [`${value} dB`, '평균 소음']} />
                  <Bar dataKey="서울시 평균" fill="#3b82f6" name="서울시" barSize={18} />
                  <Bar
                    dataKey={`${selectedDistrict} 평균`}
                    fill="#a855f7"
                    name={selectedDistrict}
                    barSize={18}
                  />
                </BarChart>
              </ResponsiveContainer>
            </div>
            <div className="flex justify-center gap-4 mt-2 text-xs">
              <div className="flex items-center gap-1">
                <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
                <span className="font-medium">서울시</span>
              </div>
              <div className="flex items-center gap-1">
                <div className="w-2 h-2 bg-purple-500 rounded-full"></div>
                <span className="font-medium">{selectedDistrict}</span>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
}
