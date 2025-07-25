// components/KpiCards.tsx

"use client"

import { Card } from "@/components/ui/card"
import NoiseGauge from "./NoiseGauge";
import { MapPin, Clock, Volume2, Headphones, Sun, Moon, Sunrise } from 'lucide-react';
import React, { useState } from "react";

// --- Helper Data & Functions ---

const TIME_THEMES = {
  새벽: { start: 0, end: 6, range: "00:00 ~ 05:59", theme: 'dawn', icon: <Sunrise size={32} /> },
  오전: { start: 6, end: 12, range: "06:00 ~ 11:59", theme: 'day', icon: <Sun size={32} /> },
  오후: { start: 12, end: 20, range: "12:00 ~ 19:59", theme: 'day', icon: <Sun size={32} /> },
  밤:  { start: 20, end: 24, range: "20:00 ~ 23:59", theme: 'night', icon: <Moon size={32} /> }
};

const NOISE_LEVELS = {
  "안전": { range: "0 ~ 59.9 dB", color: "text-green-500" },
  "주의": { range: "60.0 ~ 89.9 dB", color: "text-yellow-500" },
  "경고": { range: "90.0 dB 이상", color: "text-red-500" },
};

const getDateTimeInfo = (dateTimeString: string) => {
  if (!dateTimeString) {
    return { dayType: '-', timeCategory: { name: '-', theme: 'day', icon: null }, hour: 0, date: new Date() };
  }
  const date = new Date(dateTimeString);
  if (isNaN(date.getTime())) {
    return { dayType: '-', timeCategory: { name: '-', theme: 'day', icon: null }, hour: 0, date: new Date() };
  }
  const day = date.getDay();
  const hour = date.getHours();
  const dayType = (day === 0 || day === 6) ? '주말' : '주중';

  for (const [name, themeData] of Object.entries(TIME_THEMES)) {
    if (hour >= themeData.start && hour < themeData.end) {
      return { dayType, timeCategory: { name, ...themeData }, hour, date };
    }
  }
  return { dayType, timeCategory: { name: '-', theme: 'day', icon: null }, hour, date };
};

const getNoiseLevel = (value: number): keyof typeof NOISE_LEVELS => {
  if (value < 60) return "안전";
  if (value < 90) return "주의";
  return "경고";
};


// --- Reusable UI Components ---

function ThemedLcdClock({ time, amPm, icon }: { time: string; amPm: string; icon: React.ReactNode; }) {
  return (

    <div className="grid grid-cols-[auto_1fr] place-items-center gap-2 h-20 w-full">
      {/* 왼쪽 셀: 아이콘과 AM/PM 그룹 */}
      <div className="flex flex-col items-center justify-center text-slate-500">
        {icon}
        <p className="font-sans font-bold text-xl">{amPm}</p>
      </div>

      {/* 오른쪽 셀: 시간 텍스트 */}
      <p className="font-dseg text-6xl text-slate-800">
        {time}
      </p>
    </div>
  );
}

function DateTimeTooltip({ dateTime, noiseValue, classification, position }: {
  dateTime: Date;
  noiseValue: number;
  classification: string;
  position: { x: number; y: number };
}) {
  const formattedDateTime = dateTime.toLocaleString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' });
  const timeTooltipData = Object.entries(TIME_THEMES).map(([name, data]) => ({ name, range: data.range }));

  return (
    <div
      className="absolute p-3 bg-gray-800 bg-opacity-90 text-white rounded-md shadow-lg text-xs z-10 pointer-events-none w-max"
      style={{ top: position.y, left: position.x, transform: 'translate(10px, 10px)' }}
    >
      <p className="whitespace-nowrap font-bold">최대 소음: {(noiseValue ?? 0).toFixed(1)} dB</p>
      <hr className="border-gray-600 my-1.5" />
      <p className="whitespace-nowrap">발생 시각: {formattedDateTime}</p>
      <p className="whitespace-nowrap font-bold mt-1">분류: {classification}</p>
      <hr className="border-gray-600 my-1.5" />
      <div className="space-y-1 mt-1.5">
        {timeTooltipData.map((cat) => (
          <div key={cat.name} className="flex items-center justify-between">
            <span className="text-gray-400">{cat.name}:</span>
            <span className="ml-4 text-gray-300">{cat.range}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

function GaugeTooltip({ title, value, level, description, position }: {
  title: string;
  value: number;
  level: keyof typeof NOISE_LEVELS;
  description: string;
  position: { x: number; y: number };
}) {
  const currentLevelInfo = NOISE_LEVELS[level];

  return (
    <div
      className="absolute p-3 bg-gray-800 bg-opacity-90 text-white rounded-md shadow-lg text-xs z-10 pointer-events-none w-64"
      style={{ top: position.y, left: position.x, transform: 'translate(10px, 10px)' }}
    >
      <p className="font-bold text-sm">{title}: {(value ?? 0).toFixed(1)} dB</p>
      <p className="font-bold">분류: <span className={currentLevelInfo.color}>{level}</span></p>
      <hr className="border-gray-600 my-1.5" />
      <div className="space-y-1">
        {Object.entries(NOISE_LEVELS).map(([levelName, { range, color }]) => (
          <div key={levelName} className="flex items-center justify-between">
            <span className="text-gray-400">{levelName}:</span>
            <span className={color}>{range}</span>
          </div>
        ))}
      </div>
      <hr className="border-gray-600 my-1.5" />
      <p className="whitespace-pre-wrap text-gray-300 mt-1.5">{description}</p>
    </div>
  );
}


// --- KPI Card Components ---

function LocationKpi({ icon, title, primaryLocation, secondaryLocation }: {
  icon: React.ReactNode;
  title: string;
  primaryLocation: string;
  secondaryLocation: string;
}) {
  return (
    <Card className="p-6 flex flex-col h-full">
      <div className="flex items-center justify-center">
        <span className="text-red-500">{icon}</span>
        <h3 className="text-xl font-medium ml-2 text-gray-800">{title}</h3>
      </div>
      <div className="flex flex-col items-center justify-center flex-grow text-center">
        <div className="text-lg text-gray-500">{primaryLocation}</div>
        <div className="text-3xl font-bold text-gray-900 mt-1">{secondaryLocation}</div>
      </div>
    </Card>
  );
}

function GaugeKpi({ icon, title, value, description }: { icon: React.ReactNode; title: string; value: number; description: string; }) {
  const [isTooltipVisible, setIsTooltipVisible] = useState(false);
  const [mousePosition, setMousePosition] = useState({ x: 0, y: 0 });
  const level = getNoiseLevel(value || 0);

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
    const rect = e.currentTarget.getBoundingClientRect();
    setMousePosition({ x: e.clientX - rect.left, y: e.clientY - rect.top });
  };

  return (
    <div
      className="relative h-full"
      onMouseEnter={() => setIsTooltipVisible(true)}
      onMouseLeave={() => setIsTooltipVisible(false)}
      onMouseMove={handleMouseMove}
    >
      <Card className="p-6 flex flex-col h-full">
        <div className="flex items-center justify-center text-gray-700">
          <span className="mr-2 text-red-500">{icon}</span>
          <h3 className="text-xl font-medium">{title}</h3>
        </div>
        <div className="flex flex-col items-center justify-center flex-grow">
          <NoiseGauge value={value || 0} maxValue={120} />
          <div className="-mt-2">
            <span className="text-3xl font-bold text-gray-900">{(value ?? 0).toFixed(1)}</span>
            <span className="text-lg text-gray-600 ml-1">dB</span>
          </div>
        </div>
      </Card>
      {isTooltipVisible && <GaugeTooltip title={title} value={value || 0} level={level} description={description} position={mousePosition} />}
    </div>
  );
}

function TimeKpi({ icon, title, dateTimeString, noiseValue }: { icon: React.ReactNode; title: string; dateTimeString: string; noiseValue: number; }) {
  const [isTooltipVisible, setIsTooltipVisible] = useState(false);
  const [mousePosition, setMousePosition] = useState({ x: 0, y: 0 });

  const { dayType, timeCategory, hour, date } = getDateTimeInfo(dateTimeString);
  const classification = `${dayType} ${timeCategory.name}`;

  const hour12 = hour % 12 === 0 ? 12 : hour % 12;
  const amPm = hour < 12 ? 'AM' : 'PM';
  const formattedTime = `${String(hour12).padStart(2, '0')}:00`;

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
    const rect = e.currentTarget.getBoundingClientRect();
    setMousePosition({ x: e.clientX - rect.left, y: e.clientY - rect.top });
  };

  return (
    <div
      className="relative h-full"
      onMouseEnter={() => setIsTooltipVisible(true)}
      onMouseLeave={() => setIsTooltipVisible(false)}
      onMouseMove={handleMouseMove}
    >
      <Card className="p-6 flex flex-col h-full">
        <div className="flex items-center justify-center">
          <span className="text-red-500">{icon}</span>
          <h3 className="text-xl font-medium ml-2 text-gray-800">{title}</h3>
        </div>
        <div className="flex flex-col items-center justify-center flex-grow">
          <ThemedLcdClock time={formattedTime} amPm={amPm} icon={timeCategory.icon} />
          <div className="text-xl font-bold text-gray-800 -mt-1">
            {classification}
          </div>
        </div>
      </Card>
      {isTooltipVisible && <DateTimeTooltip dateTime={date} noiseValue={noiseValue} classification={classification} position={mousePosition} />}
    </div>
  );
}


// --- Main Component ---
interface KpiCardsProps {
  avgNoise: number;
  maxNoiseRegion: string;
  maxNoiseTime: string;
  maxNoiseTimeValue: number;
  perceivedNoise: number;
  selectedDistrict: string;
}

export default function KpiCards({ avgNoise, maxNoiseRegion, maxNoiseTime, maxNoiseTimeValue, perceivedNoise, selectedDistrict }: KpiCardsProps) {

  const avgNoiseDesc = "선택된 지역과 기간 동안 측정된 소음 데이터의 산술 평균값입니다.";
  const perceivedNoiseDesc = "'체감 소음'은 AI가 측정한 게시글의 감정 지수와 측정된 환경 데이터, 소음을 종합적으로 분석하여 산출한 '도시 온'만의 감정 지수입니다.";

  let primaryLocation = "";
  const secondaryLocation = maxNoiseRegion;

  if (selectedDistrict === "all") {
    primaryLocation = "서울특별시";
  } else {
    primaryLocation = selectedDistrict;
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <GaugeKpi icon={<Volume2 size={21} />} title="평균 소음" value={avgNoise} description={avgNoiseDesc} />
      <GaugeKpi icon={<Headphones size={21} />} title="체감 소음" value={perceivedNoise} description={perceivedNoiseDesc} />
      <LocationKpi
        icon={<MapPin size={21} />}
        title="최대 소음 발생 지역"
        primaryLocation={primaryLocation}
        secondaryLocation={secondaryLocation}
      />
      <TimeKpi icon={<Clock size={21} />} title="최대 소음 발생 시간대" dateTimeString={maxNoiseTime} noiseValue={maxNoiseTimeValue} />
    </div>
  );
}
