'use client';

import { useState, useMemo } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Download,
  FileText,
  TrendingUp,
  TrendingDown,
  Clock,
  MapPin,
  BarChart3,
} from 'lucide-react';
import { BarChart, LineChart } from 'recharts';
import { Volume2, MessageSquare } from 'lucide-react';

// Dummy Data for Analysis Report
const monthlyNoiseData = [
  { month: '1월', avgNoise: 65, maxNoise: 78, minNoise: 55 },
  { month: '2월', avgNoise: 67, maxNoise: 80, minNoise: 58 },
  { month: '3월', avgNoise: 68, maxNoise: 82, minNoise: 59 },
  { month: '4월', avgNoise: 70, maxNoise: 85, minNoise: 60 },
  { month: '5월', avgNoise: 72, maxNoise: 88, minNoise: 62 },
  { month: '6월', avgNoise: 75, maxNoise: 90, minNoise: 65 },
  { month: '7월', avgNoise: 78, maxNoise: 92, minNoise: 68 },
  { month: '8월', avgNoise: 76, maxNoise: 91, minNoise: 67 },
  { month: '9월', avgNoise: 73, maxNoise: 89, minNoise: 64 },
  { month: '10월', avgNoise: 70, maxNoise: 86, minNoise: 61 },
  { month: '11월', avgNoise: 68, maxNoise: 84, minNoise: 59 },
  { month: '12월', avgNoise: 66, maxNoise: 80, minNoise: 57 },
];

const complaintCategories = [
  { name: '교통 소음', count: 320 },
  { name: '공사 소음', count: 280 },
  { name: '생활 소음', count: 250 },
  { name: '상업 소음', count: 180 },
  { name: '기타', count: 70 },
];

const peakTimeData = [
  { time: '00-06시', complaints: 50 },
  { time: '06-12시', complaints: 200 },
  { time: '12-18시', complaints: 350 },
  { time: '18-24시', complaints: 400 },
];

const topDistricts = [
  { name: '강남구', avgNoise: 78.5, complaints: 1500 },
  { name: '영등포구', avgNoise: 77.2, complaints: 1300 },
  { name: '종로구', avgNoise: 76.8, complaints: 1250 },
  { name: '마포구', avgNoise: 75.9, complaints: 1100 },
  { name: '구로구', avgNoise: 74.5, complaints: 1000 },
];

const mockData = [
  {
    gu: '강남구',
    avgNoise: 73.2,
    complaints: 1245,
    peakTime: '18:00-21:00',
    mainType: '주요 도로',
    maxNoise: 85.4,
    minNoise: 62.1,
  },
  {
    gu: '마포구',
    avgNoise: 70.8,
    complaints: 987,
    peakTime: '17:00-20:00',
    mainType: '상업 지역',
    maxNoise: 82.3,
    minNoise: 58.9,
  },
  {
    gu: '송파구',
    avgNoise: 69.9,
    complaints: 856,
    peakTime: '19:00-22:00',
    mainType: '주요 도로',
    maxNoise: 81.7,
    minNoise: 59.2,
  },
  {
    gu: '영등포구',
    avgNoise: 68.5,
    complaints: 743,
    peakTime: '18:00-21:00',
    mainType: '상업 지역',
    maxNoise: 83.1,
    minNoise: 55.4,
  },
  {
    gu: '구로구',
    avgNoise: 67.3,
    complaints: 692,
    peakTime: '16:00-19:00',
    mainType: '산업 지역',
    maxNoise: 79.8,
    minNoise: 56.7,
  },
  {
    gu: '용산구',
    avgNoise: 66.8,
    complaints: 634,
    peakTime: '18:00-21:00',
    mainType: '주요 도로',
    maxNoise: 78.9,
    minNoise: 54.2,
  },
  {
    gu: '서초구',
    avgNoise: 65.4,
    complaints: 578,
    peakTime: '17:00-20:00',
    mainType: '주거 지역',
    maxNoise: 76.8,
    minNoise: 53.1,
  },
  {
    gu: '관악구',
    avgNoise: 64.2,
    complaints: 523,
    peakTime: '18:00-21:00',
    mainType: '주거 지역',
    maxNoise: 75.6,
    minNoise: 52.8,
  },
  {
    gu: '동작구',
    avgNoise: 63.7,
    complaints: 467,
    peakTime: '19:00-22:00',
    mainType: '주거 지역',
    maxNoise: 74.9,
    minNoise: 51.7,
  },
  {
    gu: '성동구',
    avgNoise: 62.9,
    complaints: 412,
    peakTime: '17:00-20:00',
    mainType: '주요 도로',
    maxNoise: 73.4,
    minNoise: 50.9,
  },
  {
    gu: '광진구',
    avgNoise: 62.1,
    complaints: 389,
    peakTime: '18:00-21:00',
    mainType: '주거 지역',
    maxNoise: 72.8,
    minNoise: 49.6,
  },
  {
    gu: '종로구',
    avgNoise: 61.8,
    complaints: 356,
    peakTime: '16:00-19:00',
    mainType: '상업 지역',
    maxNoise: 72.1,
    minNoise: 48.9,
  },
  {
    gu: '중구',
    avgNoise: 61.3,
    complaints: 334,
    peakTime: '18:00-21:00',
    mainType: '상업 지역',
    maxNoise: 71.7,
    minNoise: 48.2,
  },
  {
    gu: '성북구',
    avgNoise: 60.8,
    complaints: 298,
    peakTime: '17:00-20:00',
    mainType: '주거 지역',
    maxNoise: 71.2,
    minNoise: 47.8,
  },
  {
    gu: '강서구',
    avgNoise: 60.5,
    complaints: 276,
    peakTime: '19:00-22:00',
    mainType: '주요 도로',
    maxNoise: 70.9,
    minNoise: 47.3,
  },
  {
    gu: '양천구',
    avgNoise: 60.2,
    complaints: 245,
    peakTime: '18:00-21:00',
    mainType: '주거 지역',
    maxNoise: 70.6,
    minNoise: 46.9,
  },
  {
    gu: '은평구',
    avgNoise: 60.1,
    complaints: 223,
    peakTime: '17:00-20:00',
    mainType: '주거 지역',
    maxNoise: 70.3,
    minNoise: 46.5,
  },
  {
    gu: '서대문구',
    avgNoise: 59.7,
    complaints: 198,
    peakTime: '18:00-21:00',
    mainType: '주거 지역',
    maxNoise: 69.8,
    minNoise: 46.1,
  },
  {
    gu: '동대문구',
    avgNoise: 59.4,
    complaints: 187,
    peakTime: '16:00-19:00',
    mainType: '상업 지역',
    maxNoise: 69.5,
    minNoise: 45.8,
  },
  {
    gu: '금천구',
    avgNoise: 59.1,
    complaints: 165,
    peakTime: '18:00-21:00',
    mainType: '산업 지역',
    maxNoise: 69.2,
    minNoise: 45.4,
  },
  {
    gu: '강북구',
    avgNoise: 58.9,
    complaints: 143,
    peakTime: '17:00-20:00',
    mainType: '주거 지역',
    maxNoise: 68.9,
    minNoise: 45.1,
  },
  {
    gu: '노원구',
    avgNoise: 58.6,
    complaints: 132,
    peakTime: '19:00-22:00',
    mainType: '주거 지역',
    maxNoise: 68.6,
    minNoise: 44.7,
  },
  {
    gu: '도봉구',
    avgNoise: 59.3,
    complaints: 121,
    peakTime: '18:00-21:00',
    mainType: '주거 지역',
    maxNoise: 69.3,
    minNoise: 45.9,
  },
  {
    gu: '강동구',
    avgNoise: 58.4,
    complaints: 109,
    peakTime: '17:00-20:00',
    mainType: '주거 지역',
    maxNoise: 68.4,
    minNoise: 44.3,
  },
  {
    gu: '중랑구',
    avgNoise: 58.7,
    complaints: 98,
    peakTime: '18:00-21:00',
    mainType: '주거 지역',
    maxNoise: 68.7,
    minNoise: 44.6,
  },
];

const timeData = [
  { time: '00:00', noise: 58.2 },
  { time: '03:00', noise: 55.8 },
  { time: '06:00', noise: 58.2 },
  { time: '09:00', noise: 65.4 },
  { time: '12:00', noise: 68.7 },
  { time: '15:00', noise: 70.1 },
  { time: '18:00', noise: 74.3 },
  { time: '21:00', noise: 71.8 },
  { time: '24:00', noise: 62.5 },
];

const areaTypeData = [
  { name: '주요 도로', value: 72.5, color: '#EF4444' },
  { name: '상업 지역', value: 68.3, color: '#F59E0B' },
  { name: '산업 지역', value: 69.1, color: '#8B5CF6' },
  { name: '주거 지역', value: 61.2, color: '#10B981' },
  { name: '공원', value: 55.8, color: '#06B6D4' },
  { name: '공공시설', value: 63.4, color: '#84CC16' },
];

const dailyTrendData = [
  { date: '06-01', noise: 65.2 },
  { date: '06-05', noise: 67.1 },
  { date: '06-10', noise: 66.8 },
  { date: '06-15', noise: 68.3 },
  { date: '06-20', noise: 69.7 },
  { date: '06-25', noise: 68.9 },
  { date: '06-30', noise: 67.4 },
];

const complaintTypeData = [
  { name: '공사장 소음', value: 350 },
  { name: '교통 소음', value: 280 },
  { name: '생활 소음', value: 200 },
  { name: '확성기 소음', value: 120 },
  { name: '기타', value: 50 },
];

const dailyNoiseTrendData = [
  { hour: '00시', noise: 55 },
  { hour: '03시', noise: 50 },
  { hour: '06시', noise: 60 },
  { hour: '09시', noise: 75 },
  { hour: '12시', noise: 80 },
  { hour: '15시', noise: 78 },
  { hour: '18시', noise: 82 },
  { hour: '21시', noise: 70 },
];

const topDistrictsByNoise = [
  { district: '중구', noise: 82.1 },
  { district: '강서구', noise: 81.2 },
  { district: '영등포구', noise: 80.3 },
  { district: '구로구', noise: 79.4 },
  { district: '종로구', noise: 78.9 },
];

const noiseSourceData = [
  { name: '공사장 소음', value: 350 },
  { name: '교통 소음', value: 280 },
  { name: '생활 소음', value: 200 },
  { name: '확성기 소음', value: 120 },
  { name: '기타', value: 50 },
];

const timeOfDayData = [
  { time: '00:00', avgNoise: 58.2 },
  { time: '03:00', avgNoise: 55.8 },
  { time: '06:00', avgNoise: 58.2 },
  { time: '09:00', avgNoise: 65.4 },
  { time: '12:00', avgNoise: 68.7 },
  { time: '15:00', avgNoise: 70.1 },
  { time: '18:00', avgNoise: 74.3 },
  { time: '21:00', avgNoise: 71.8 },
  { time: '24:00', avgNoise: 62.5 },
];

export default function AnalysisReport() {
  const [selectedGu, setSelectedGu] = useState<string>('전체');
  const [dateRange, setDateRange] = useState({ start: '2025-06-01', end: '2025-06-30' });

  const filteredData = useMemo(() => {
    return selectedGu === '전체' ? mockData : mockData.filter(d => d.gu === selectedGu);
  }, [selectedGu]);

  const top3 = useMemo(
    () => [...filteredData].sort((a, b) => b.avgNoise - a.avgNoise).slice(0, 3),
    [filteredData]
  );
  const bottom3 = useMemo(
    () => [...filteredData].sort((a, b) => a.avgNoise - b.avgNoise).slice(0, 3),
    [filteredData]
  );

  const overallAvg = useMemo(
    () =>
      (filteredData.reduce((sum, item) => sum + item.avgNoise, 0) / filteredData.length).toFixed(1),
    [filteredData]
  );

  const mostCommonPeakTime = useMemo(() => {
    const timeCount: { [key: string]: number } = {};
    filteredData.forEach(item => {
      timeCount[item.peakTime] = (timeCount[item.peakTime] || 0) + 1;
    });
    return Object.entries(timeCount).sort(([, a], [, b]) => b - a)[0]?.[0] || '18:00-21:00';
  }, [filteredData]);

  const mostCommonAreaType = useMemo(() => {
    const typeCount: { [key: string]: number } = {};
    filteredData.forEach(item => {
      typeCount[item.mainType] = (typeCount[item.mainType] || 0) + 1;
    });
    return Object.entries(typeCount).sort(([, a], [, b]) => b - a)[0]?.[0] || '주거 지역';
  }, [filteredData]);

  const highestVarianceDistricts = useMemo(() => {
    return [...filteredData]
      .map(item => ({
        ...item,
        variance: item.maxNoise - item.minNoise,
      }))
      .sort((a, b) => b.variance - a.variance)
      .slice(0, 3);
  }, [filteredData]);

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
      </div>
    </div>
  );
}
