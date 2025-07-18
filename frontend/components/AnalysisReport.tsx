'use client';

import { useState } from 'react';
import { CalendarIcon, TrendingUp, TrendingDown, Activity } from 'lucide-react';
import { Line, LineChart, XAxis, YAxis, CartesianGrid } from 'recharts';

import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { ChartContainer, ChartTooltip, ChartTooltipContent } from '@/components/ui/chart';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { Calendar } from '@/components/ui/calendar';
import { cn } from '@/lib/utils';

// Mock data


// 행정구 리스트
const seoulDistricts = [
  "강남구",
  "강동구",
  "강북구",
  "강서구",
  "관악구",
  "광진구",
  "구로구",
  "금천구",
  "노원구",
  "도봉구",
  "동대문구",
  "동작구",
  "마포구",
  "서대문구",
  "서초구",
  "성동구",
  "성북구",
  "송파구",
  "양천구",
  "영등포구",
  "용산구",
  "은평구",
  "종로구",
  "중구",
  "중랑구",
]

// 예시: 구별 동 목록
const dongListByDistrict: Record<string, string[]> = {
  "강남구": ["역삼동", "삼성동", "청담동"],
  "강동구": ["천호동", "길동", "둔촌동"],
  "서초구": ["반포동", "서초동", "방배동"],
  "마포구": ["공덕동", "서교동", "망원동"],
  // 나머지 구는 필요에 따라 추가
};

const hourlyData = [
  { hour: '00:00', noise: 45.2 },
  { hour: '01:00', noise: 42.8 },
  { hour: '02:00', noise: 41.5 },
  { hour: '03:00', noise: 40.9 },
  { hour: '04:00', noise: 41.2 },
  { hour: '05:00', noise: 43.8 },
  { hour: '06:00', noise: 52.4 },
  { hour: '07:00', noise: 58.9 },
  { hour: '08:00', noise: 61.7 },
  { hour: '09:00', noise: 59.3 },
  { hour: '10:00', noise: 57.8 },
  { hour: '11:00', noise: 58.9 },
  { hour: '12:00', noise: 60.4 },
  { hour: '13:00', noise: 59.7 },
  { hour: '14:00', noise: 58.2 },
  { hour: '15:00', noise: 59.8 },
  { hour: '16:00', noise: 61.9 },
  { hour: '17:00', noise: 64.2 },
  { hour: '18:00', noise: 67.8 },
  { hour: '19:00', noise: 69.4 },
  { hour: '20:00', noise: 68.9 },
  { hour: '21:00', noise: 65.7 },
  { hour: '22:00', noise: 58.4 },
  { hour: '23:00', noise: 51.2 },
];

const dailyData = [
  { day: 'Mon', noise: 58.4 },
  { day: 'Tue', noise: 59.7 },
  { day: 'Wed', noise: 61.2 },
  { day: 'Thu', noise: 62.8 },
  { day: 'Fri', noise: 65.3 },
  { day: 'Sat', noise: 67.9 },
  { day: 'Sun', noise: 54.2 },
];

const combinedHourlyData = [
  {
    hour: '00:00',
    top1: 52.1,
    top2: 48.9,
    top3: 47.2,
    bottom1: 38.4,
    bottom2: 36.7,
    bottom3: 35.1,
  },
  {
    hour: '01:00',
    top1: 49.8,
    top2: 46.3,
    top3: 44.7,
    bottom1: 36.2,
    bottom2: 34.8,
    bottom3: 33.4,
  },
  {
    hour: '02:00',
    top1: 48.5,
    top2: 45.1,
    top3: 43.2,
    bottom1: 35.1,
    bottom2: 33.7,
    bottom3: 32.3,
  },
  {
    hour: '03:00',
    top1: 47.9,
    top2: 44.6,
    top3: 42.8,
    bottom1: 34.8,
    bottom2: 33.2,
    bottom3: 31.9,
  },
  {
    hour: '04:00',
    top1: 48.2,
    top2: 44.9,
    top3: 43.1,
    bottom1: 35.2,
    bottom2: 33.6,
    bottom3: 32.1,
  },
  {
    hour: '05:00',
    top1: 50.8,
    top2: 47.4,
    top3: 45.6,
    bottom1: 37.1,
    bottom2: 35.4,
    bottom3: 34.2,
  },
  {
    hour: '06:00',
    top1: 59.4,
    top2: 56.2,
    top3: 54.1,
    bottom1: 44.8,
    bottom2: 42.9,
    bottom3: 41.2,
  },
  {
    hour: '07:00',
    top1: 65.9,
    top2: 62.7,
    top3: 60.4,
    bottom1: 50.2,
    bottom2: 48.1,
    bottom3: 46.7,
  },
  {
    hour: '08:00',
    top1: 68.7,
    top2: 65.4,
    top3: 63.2,
    bottom1: 52.9,
    bottom2: 50.8,
    bottom3: 49.1,
  },
  {
    hour: '09:00',
    top1: 66.3,
    top2: 63.1,
    top3: 60.8,
    bottom1: 50.7,
    bottom2: 48.9,
    bottom3: 47.2,
  },
  {
    hour: '10:00',
    top1: 64.8,
    top2: 61.6,
    top3: 59.4,
    bottom1: 49.2,
    bottom2: 47.6,
    bottom3: 45.9,
  },
  {
    hour: '11:00',
    top1: 65.9,
    top2: 62.7,
    top3: 60.5,
    bottom1: 50.3,
    bottom2: 48.7,
    bottom3: 47.1,
  },
  {
    hour: '12:00',
    top1: 67.4,
    top2: 64.2,
    top3: 61.9,
    bottom1: 51.8,
    bottom2: 50.1,
    bottom3: 48.4,
  },
  {
    hour: '13:00',
    top1: 66.7,
    top2: 63.5,
    top3: 61.2,
    bottom1: 51.1,
    bottom2: 49.4,
    bottom3: 47.8,
  },
  {
    hour: '14:00',
    top1: 65.2,
    top2: 62.0,
    top3: 59.8,
    bottom1: 49.6,
    bottom2: 48.0,
    bottom3: 46.4,
  },
  {
    hour: '15:00',
    top1: 66.8,
    top2: 63.6,
    top3: 61.4,
    bottom1: 51.2,
    bottom2: 49.6,
    bottom3: 48.0,
  },
  {
    hour: '16:00',
    top1: 68.9,
    top2: 65.7,
    top3: 63.5,
    bottom1: 53.3,
    bottom2: 51.7,
    bottom3: 50.1,
  },
  {
    hour: '17:00',
    top1: 71.2,
    top2: 68.0,
    top3: 65.8,
    bottom1: 55.6,
    bottom2: 54.0,
    bottom3: 52.4,
  },
  {
    hour: '18:00',
    top1: 74.8,
    top2: 71.6,
    top3: 69.4,
    bottom1: 59.2,
    bottom2: 57.6,
    bottom3: 56.0,
  },
  {
    hour: '19:00',
    top1: 76.4,
    top2: 73.2,
    top3: 71.0,
    bottom1: 60.8,
    bottom2: 59.2,
    bottom3: 57.6,
  },
  {
    hour: '20:00',
    top1: 75.9,
    top2: 72.7,
    top3: 70.5,
    bottom1: 60.3,
    bottom2: 58.7,
    bottom3: 57.1,
  },
  {
    hour: '21:00',
    top1: 72.7,
    top2: 69.5,
    top3: 67.3,
    bottom1: 57.1,
    bottom2: 55.5,
    bottom3: 53.9,
  },
  {
    hour: '22:00',
    top1: 65.4,
    top2: 62.2,
    top3: 60.0,
    bottom1: 49.8,
    bottom2: 48.2,
    bottom3: 46.6,
  },
  {
    hour: '23:00',
    top1: 58.2,
    top2: 55.0,
    top3: 52.8,
    bottom1: 42.6,
    bottom2: 41.0,
    bottom3: 39.4,
  },
];

const combinedDailyData = [
  { day: 'Mon', top1: 65.4, top2: 62.1, top3: 59.8, bottom1: 51.8, bottom2: 49.2, bottom3: 47.6 },
  { day: 'Tue', top1: 66.7, top2: 63.4, top3: 61.1, bottom1: 53.1, bottom2: 50.5, bottom3: 48.9 },
  { day: 'Wed', top1: 68.2, top2: 64.9, top3: 62.6, bottom1: 54.6, bottom2: 52.0, bottom3: 50.4 },
  { day: 'Thu', top1: 69.8, top2: 66.5, top3: 64.2, bottom1: 56.2, bottom2: 53.6, bottom3: 52.0 },
  { day: 'Fri', top1: 72.3, top2: 69.0, top3: 66.7, bottom1: 58.7, bottom2: 56.1, bottom3: 54.5 },
  { day: 'Sat', top1: 74.9, top2: 71.6, top3: 69.3, bottom1: 61.3, bottom2: 58.7, bottom3: 57.1 },
  { day: 'Sun', top1: 61.2, top2: 57.9, top3: 55.6, bottom1: 47.6, bottom2: 45.0, bottom3: 43.4 },
];

const loudestAreas = [
  { rank: 1, area: 'Industrial Zone A', noise: 74.2 },
  { rank: 2, area: 'Highway Junction', noise: 71.8 },
  { rank: 3, area: 'Commercial District', noise: 68.9 },
];

const quietestAreas = [
  { rank: 1, area: 'Residential Park', noise: 42.1 },
  { rank: 2, area: 'Suburban Area', noise: 45.7 },
  { rank: 3, area: 'Green Belt Zone', noise: 48.3 },
];

const varianceAreas = [
  { rank: 1, area: 'Entertainment District', variance: 15.8 },
  { rank: 2, area: 'School Zone', variance: 12.4 },
  { rank: 3, area: 'Mixed Use Area', variance: 10.7 },
];

export default function AnalysisReport() {
  const [startDate, setStartDate] = useState<Date>();
  const [endDate, setEndDate] = useState<Date>();

  // District와 Dong 기본값 설정
  const [selectedDistrict, setSelectedDistrict] = useState<string>(seoulDistricts[0]);
  const [selectedDong, setSelectedDong] = useState<string>(
    dongListByDistrict[seoulDistricts[0]]?.[0] || ''
  );

  // 구를 변경하면 해당 구의 첫번째 동으로 자동 설정
  const handleDistrictChange = (district: string) => {
    setSelectedDistrict(district);
    setSelectedDong(dongListByDistrict[district]?.[0] || '');
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto space-y-8">
        {/* Header */}
        <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-3">
              📊 분석 리포트
            </h1>
            <p className="text-gray-600 mt-2">서울시 소음 데이터 종합 분석 보고서</p>
          </div>
        </div>
        <div className="max-w-7xl mx-auto space-y-6">
          {/* Filter Section */}
          <Card>
            <CardHeader>
              <CardTitle>Data Filters</CardTitle>
              <CardDescription>Select date range and area to filter the noise data</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex flex-wrap gap-4">

                <div className="mb-4">
                  <label className="block text-sm font-medium text-gray-700 mb-1">날짜 범위</label>
                  <div className="flex gap-2">
                    <input
                      type="date"
                      className="w-full border rounded px-3 py-2"
                      value={startDate}
                      onChange={e => setStartDate(e.target.value)}
                    />
                    <span className="mx-1 text-gray-500">~</span>
                    <input
                      type="date"
                      className="w-full border rounded px-3 py-2"
                      value={endDate}
                      onChange={e => setEndDate(e.target.value)}
                    />
                  </div>
                </div>

                {/* 동/구 SelectBox */}
                <div className="flex flex-col space-y-2">
                  <label className="text-sm font-medium">내 지역 선택</label>
                  <div className="flex gap-2">
                    {/* 구 Select */}
                    <Select value={selectedDistrict} onValueChange={handleDistrictChange}>
                      <SelectTrigger className="w-[140px]">
                        <SelectValue placeholder="구 선택" />
                      </SelectTrigger>
                      <SelectContent>
                        {seoulDistricts.map(district => (
                          <SelectItem key={district} value={district}>{district}</SelectItem>
                        ))}
                      </SelectContent>
                    </Select>

                    {/* 동 Select */}
                    <Select value={selectedDong} onValueChange={setSelectedDong}>
                      <SelectTrigger className="w-[140px]">
                        <SelectValue placeholder="동 선택" />
                      </SelectTrigger>
                      <SelectContent>
                        {dongListByDistrict[selectedDistrict]?.map(dong => (
                          <SelectItem key={dong} value={dong}>{dong}</SelectItem>
                        ))}
                      </SelectContent>
                    </Select>

                  </div>
                </div>

              </div>
            </CardContent>
          </Card>

          {/* KPI Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Overall Average Noise</CardTitle>
                <Activity className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">62.9 dB</div>
                <p className="text-xs text-muted-foreground">+2.1% from last month</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Top Noise Area Type</CardTitle>
                <TrendingUp className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">Residential Area</div>
                <p className="text-xs text-muted-foreground">Most affected zone type</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Peak Noise Time Slot</CardTitle>
                <TrendingUp className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">18:00-21:00</div>
                <p className="text-xs text-muted-foreground">Evening rush hours</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Overall Average Noise</CardTitle>
                <Activity className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">62.9 dB</div>
                <p className="text-xs text-muted-foreground">Consistent measurement</p>
              </CardContent>
            </Card>
          </div>

          {/* Area Rankings */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <TrendingUp className="h-5 w-5 text-red-500" />
                  Top 3 Loudest Areas
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {loudestAreas.map(area => (
                    <div key={area.rank} className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className="w-6 h-6 rounded-full bg-red-100 text-red-600 flex items-center justify-center text-sm font-bold">
                          {area.rank}
                        </div>
                        <span className="font-medium">{area.area}</span>
                      </div>
                      <span className="text-red-600 font-bold">{area.noise} dB</span>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <TrendingDown className="h-5 w-5 text-green-500" />
                  Top 3 Quietest Areas
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {quietestAreas.map(area => (
                    <div key={area.rank} className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className="w-6 h-6 rounded-full bg-green-100 text-green-600 flex items-center justify-center text-sm font-bold">
                          {area.rank}
                        </div>
                        <span className="font-medium">{area.area}</span>
                      </div>
                      <span className="text-green-600 font-bold">{area.noise} dB</span>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Activity className="h-5 w-5 text-blue-500" />
                  Top 3 Areas with Largest Noise Variance
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {varianceAreas.map(area => (
                    <div key={area.rank} className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className="w-6 h-6 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center text-sm font-bold">
                          {area.rank}
                        </div>
                        <span className="font-medium">{area.area}</span>
                      </div>
                      <span className="text-blue-600 font-bold">{area.variance}</span>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Main Noise Trend Graph */}
          <Card>
            <CardHeader>
              <CardTitle>Noise Trend Analysis</CardTitle>
              <CardDescription>Switch between hourly and daily noise patterns</CardDescription>
            </CardHeader>
            <CardContent>
              <Tabs defaultValue="hourly" className="w-full">
                <TabsList className="grid w-full grid-cols-2">
                  <TabsTrigger value="hourly">Hourly Noise Trend</TabsTrigger>
                  <TabsTrigger value="daily">Daily Noise Trend</TabsTrigger>
                </TabsList>

                <TabsContent value="hourly" className="space-y-4">
                  <ChartContainer
                    config={{
                      noise: {
                        label: 'Noise Level (dB)',
                        color: 'hsl(var(--chart-1))',
                      },
                    }}
                    className="h-[400px]"
                  >
                    <LineChart data={hourlyData}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="hour" tick={{ fontSize: 12 }} interval={2} />
                      <YAxis domain={['dataMin - 5', 'dataMax + 5']} tick={{ fontSize: 12 }} />
                      <ChartTooltip content={<ChartTooltipContent />} />
                      <Line
                        type="monotone"
                        dataKey="noise"
                        stroke="var(--color-noise)"
                        strokeWidth={2}
                        dot={{ r: 4 }}
                      />
                    </LineChart>
                  </ChartContainer>
                </TabsContent>

                <TabsContent value="daily" className="space-y-4">
                  <ChartContainer
                    config={{
                      noise: {
                        label: 'Noise Level (dB)',
                        color: 'hsl(var(--chart-2))',
                      },
                    }}
                    className="h-[400px]"
                  >
                    <LineChart data={dailyData}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="day" tick={{ fontSize: 12 }} />
                      <YAxis domain={['dataMin - 5', 'dataMax + 5']} tick={{ fontSize: 12 }} />
                      <ChartTooltip content={<ChartTooltipContent />} />
                      <Line
                        type="monotone"
                        dataKey="noise"
                        stroke="var(--color-noise)"
                        strokeWidth={2}
                        dot={{ r: 6 }}
                      />
                    </LineChart>
                  </ChartContainer>
                </TabsContent>
              </Tabs>
            </CardContent>
          </Card>

          {/* Combined Hourly Noise Graph */}
          <Card>
            <CardHeader>
              <CardTitle>Combined Hourly Noise Analysis</CardTitle>
              <CardDescription>Top 3 vs Bottom 3 hourly noise levels comparison</CardDescription>
            </CardHeader>
            <CardContent>
              <ChartContainer
                config={{
                  top1: { label: 'Top Area 1', color: 'hsl(0, 70%, 50%)' },
                  top2: { label: 'Top Area 2', color: 'hsl(15, 70%, 50%)' },
                  top3: { label: 'Top Area 3', color: 'hsl(30, 70%, 50%)' },
                  bottom1: { label: 'Bottom Area 1', color: 'hsl(120, 50%, 50%)' },
                  bottom2: { label: 'Bottom Area 2', color: 'hsl(135, 50%, 50%)' },
                  bottom3: { label: 'Bottom Area 3', color: 'hsl(150, 50%, 50%)' },
                }}
                className="h-[400px]"
              >
                <LineChart data={combinedHourlyData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="hour" tick={{ fontSize: 12 }} interval={2} />
                  <YAxis tick={{ fontSize: 12 }} />
                  <ChartTooltip content={<ChartTooltipContent />} />

                  {/* Top 3 lines - solid */}
                  <Line
                    type="monotone"
                    dataKey="top1"
                    stroke="var(--color-top1)"
                    strokeWidth={2}
                    dot={false}
                  />
                  <Line
                    type="monotone"
                    dataKey="top2"
                    stroke="var(--color-top2)"
                    strokeWidth={2}
                    dot={false}
                  />
                  <Line
                    type="monotone"
                    dataKey="top3"
                    stroke="var(--color-top3)"
                    strokeWidth={2}
                    dot={false}
                  />

                  {/* Bottom 3 lines - dashed */}
                  <Line
                    type="monotone"
                    dataKey="bottom1"
                    stroke="var(--color-bottom1)"
                    strokeWidth={2}
                    strokeDasharray="5 5"
                    dot={false}
                  />
                  <Line
                    type="monotone"
                    dataKey="bottom2"
                    stroke="var(--color-bottom2)"
                    strokeWidth={2}
                    strokeDasharray="5 5"
                    dot={false}
                  />
                  <Line
                    type="monotone"
                    dataKey="bottom3"
                    stroke="var(--color-bottom3)"
                    strokeWidth={2}
                    strokeDasharray="5 5"
                    dot={false}
                  />
                </LineChart>
              </ChartContainer>
            </CardContent>
          </Card>

          {/* Combined Daily Average Noise Graph */}
          <Card>
            <CardHeader>
              <CardTitle>Combined Daily Average Noise Analysis</CardTitle>
              <CardDescription>
                Top 3 vs Bottom 3 daily average noise levels by day of week
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ChartContainer
                config={{
                  top1: { label: 'Top Area 1', color: 'hsl(0, 70%, 50%)' },
                  top2: { label: 'Top Area 2', color: 'hsl(15, 70%, 50%)' },
                  top3: { label: 'Top Area 3', color: 'hsl(30, 70%, 50%)' },
                  bottom1: { label: 'Bottom Area 1', color: 'hsl(120, 50%, 50%)' },
                  bottom2: { label: 'Bottom Area 2', color: 'hsl(135, 50%, 50%)' },
                  bottom3: { label: 'Bottom Area 3', color: 'hsl(150, 50%, 50%)' },
                }}
                className="h-[400px]"
              >
                <LineChart data={combinedDailyData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="day" tick={{ fontSize: 12 }} />
                  <YAxis tick={{ fontSize: 12 }} />
                  <ChartTooltip content={<ChartTooltipContent />} />

                  {/* Top 3 lines - solid */}
                  <Line
                    type="monotone"
                    dataKey="top1"
                    stroke="var(--color-top1)"
                    strokeWidth={3}
                    dot={{ r: 5 }}
                  />
                  <Line
                    type="monotone"
                    dataKey="top2"
                    stroke="var(--color-top2)"
                    strokeWidth={3}
                    dot={{ r: 5 }}
                  />
                  <Line
                    type="monotone"
                    dataKey="top3"
                    stroke="var(--color-top3)"
                    strokeWidth={3}
                    dot={{ r: 5 }}
                  />

                  {/* Bottom 3 lines - dashed */}
                  <Line
                    type="monotone"
                    dataKey="bottom1"
                    stroke="var(--color-bottom1)"
                    strokeWidth={3}
                    strokeDasharray="8 4"
                    dot={{ r: 5 }}
                  />
                  <Line
                    type="monotone"
                    dataKey="bottom2"
                    stroke="var(--color-bottom2)"
                    strokeWidth={3}
                    strokeDasharray="8 4"
                    dot={{ r: 5 }}
                  />
                  <Line
                    type="monotone"
                    dataKey="bottom3"
                    stroke="var(--color-bottom3)"
                    strokeWidth={3}
                    strokeDasharray="8 4"
                    dot={{ r: 5 }}
                  />
                </LineChart>
              </ChartContainer>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
