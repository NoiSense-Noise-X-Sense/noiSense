'use client';

import { Button } from '@/components/ui/button';
import { useState, useEffect, useRef } from 'react';
import { Card, CardTitle } from '@/components/ui/card';
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
  Label,
} from 'recharts';
import { Volume2, MessageSquare, Moon, TrendingUp } from 'lucide-react';
import { Switch } from '@/components/ui/switch';
import { Label as UILabel } from '@/components/ui/label';

// Dummy data for districts and their noise info
const allDistricts = [
  '강남구',
  '강동구',
  '강북구',
  '강서구',
  '관악구',
  '광진구',
  '구로구',
  '금천구',
  '노원구',
  '도봉구',
  '동대문구',
  '동작구',
  '마포구',
  '서대문구',
  '서초구',
  '성동구',
  '성북구',
  '송파구',
  '양천구',
  '영등포구',
  '용산구',
  '은평구',
  '종로구',
  '중구',
  '중랑구',
];

const generateDummyData = (districtName: string) => {
  const baseNoise = 60 + Math.random() * 20; // 60-80 dB
  const baseComplaints = 150 + Math.random() * 100; // 150-250 complaints

  const yearlyComplaints = {
    '2020': Math.floor(baseComplaints * (0.8 + Math.random() * 0.4)),
    '2021': Math.floor(baseComplaints * (0.9 + Math.random() * 0.4)),
    '2022': Math.floor(baseComplaints * (1.0 + Math.random() * 0.4)),
    '2023': Math.floor(baseComplaints * (1.1 + Math.random() * 0.4)),
    '2024': Math.floor(baseComplaints * (1.2 + Math.random() * 0.4)),
  };

  const noiseTrendData = Array.from({ length: 24 }, (_, i) => ({
    hour: `${i.toString().padStart(2, '0')}시`,
    실시간: Math.floor(baseNoise + Math.random() * 10 - 5),
    일주일평균: Math.floor(baseNoise + Math.random() * 5 - 2.5),
    한달평균: Math.floor(baseNoise + Math.random() * 3 - 1.5),
  }));

  const yearlyAvgNoise = {
    '2022': { seoul: 66 + Math.random() * 5, district: baseNoise * (0.9 + Math.random() * 0.2) },
    '2023': { seoul: 68 + Math.random() * 5, district: baseNoise * (0.95 + Math.random() * 0.2) },
    '2024': { seoul: 64 + Math.random() * 5, district: baseNoise * (1.0 + Math.random() * 0.2) },
    '2025': { seoul: 62 + Math.random() * 5, district: baseNoise * (1.05 + Math.random() * 0.2) },
  };

  const keywords = [
    { text: '시끄러움', sentiment: 'negative', size: 'text-2xl' },
    { text: '불쾌함', sentiment: 'negative', size: 'text-xl' },
    { text: '스트레스', sentiment: 'negative', size: 'text-lg' },
    { text: '짜증남', sentiment: 'negative', size: 'text-base' },
    { text: '피곤함', sentiment: 'negative', size: 'text-base' },
    { text: '답답함', sentiment: 'positive', size: 'text-base' },
    { text: '조용함', sentiment: 'positive', size: 'text-lg' },
    { text: '평화로움', sentiment: 'positive', size: 'text-base' },
    { text: '편안함', sentiment: 'positive', size: 'text-base' },
  ];

  return {
    complaints: {
      '2024': yearlyComplaints['2024'],
      analysisPeriod: '2025.06.06 ~ 2025.07.06',
    },
    avgNoise: {
      value: (baseNoise + Math.random() * 5).toFixed(1),
      analysisPeriod: '2025.06.06 ~ 2025.07.06',
    },
    peakTime: {
      time: '오후 6시',
      noise: (baseNoise + Math.random() * 5 + 5).toFixed(0),
      analysisPeriod: '2025.06.06 ~ 2025.07.06',
    },
    quietTime: {
      time: '새벽 4시',
      noise: (baseNoise - Math.random() * 5 - 10).toFixed(0),
      analysisPeriod: '2025.06.06 ~ 2025.07.06',
    },
    yearlyComplaints,
    noiseTrendData,
    yearlyAvgNoise,
    keywords,
  };
};

export default function DistrictDashboard({
  selectedDistrict: initialDistrict,
}: {
  selectedDistrict: string;
}) {
  const [selectedDistrict, setSelectedDistrict] = useState(initialDistrict);
  const [autoScroll, setAutoScroll] = useState(true);
  const scrollContainerRef = useRef<HTMLDivElement>(null);
  const scrollIntervalRef = useRef<NodeJS.Timeout | null>(null);

  const [districtData, setDistrictData] = useState(() => generateDummyData(initialDistrict));

  useEffect(() => {
    setDistrictData(generateDummyData(selectedDistrict));
  }, [selectedDistrict]);

  useEffect(() => {
    if (autoScroll) {
      scrollIntervalRef.current = setInterval(() => {
        setSelectedDistrict(prevDistrict => {
          const currentIndex = allDistricts.indexOf(prevDistrict);
          const nextIndex = (currentIndex + 1) % allDistricts.length;
          return allDistricts[nextIndex];
        });
      }, 5000);
    } else {
      if (scrollIntervalRef.current) {
        clearInterval(scrollIntervalRef.current);
      }
    }
    return () => {
      if (scrollIntervalRef.current) {
        clearInterval(scrollIntervalRef.current);
      }
    };
  }, [autoScroll]);

  const handleDistrictClick = (district: string) => {
    setAutoScroll(false);
    setSelectedDistrict(district);
  };

  const getSentimentColor = (sentiment: string) => {
    switch (sentiment) {
      case 'negative':
        return 'text-red-500';
      case 'neutral':
        return 'text-yellow-500';
      case 'positive':
        return 'text-green-500';
      default:
        return 'text-gray-700';
    }
  };

  const yearlyComplaintsChartData = Object.entries(districtData.yearlyComplaints).map(
    ([year, count]) => ({
      year,
      '민원 건수': count,
    })
  );

  const yearlyAvgNoiseChartData = Object.entries(districtData.yearlyAvgNoise).map(
    ([year, data]) => ({
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
      <div className="flex-1 p-4 flex flex-col gap-3 overflow-hidden">
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
          <Card className="col-span-1 lg:col-span-1 py-1 px-2 min-h-[60px] h-36">
            <CardTitle className="text-sm font-semibold mb-0">
              {selectedDistrict}의 TOP 키워드
            </CardTitle>
            <div className="flex flex-wrap gap-1 justify-center items-start min-h-[24px] text-xs">
              {districtData.keywords.slice(0, 1).map((keyword, idx) => (
                <span key={idx} className="text-base font-bold text-red-500 mr-2">
                  {keyword.text}
                </span>
              ))}
              {districtData.keywords.slice(1, 3).map((keyword, idx) => (
                <span
                  key={idx}
                  className={`text-base font-bold ${
                    idx === 0 ? 'text-blue-500' : 'text-green-500'
                  } mr-2`}
                >
                  {keyword.text}
                </span>
              ))}
              {districtData.keywords.slice(3).map((keyword, idx) => (
                <span key={idx} className="text-base font-bold text-gray-500 mr-2">
                  {keyword.text}
                </span>
              ))}
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
                <span className="font-medium">실시간</span>
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
