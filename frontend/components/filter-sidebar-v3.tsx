"use client";

import { Slider } from "@/components/ui/slider";
import { Checkbox } from "@/components/ui/checkbox";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useState } from "react";

export default function FilterSidebar() {
  const [timeRange, setTimeRange] = useState([240]); // 04:00 in minutes
  const [noiseRange, setNoiseRange] = useState([70]);

  // Convert minutes to time format
  const formatTime = (minutes: number) => {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours.toString().padStart(2, "0")}:${mins
      .toString()
      .padStart(2, "0")}`;
  };

  const districts = [
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
  ];

  const dongsByDistrict: { [key: string]: string[] } = {
    강남구: [
      "역삼동",
      "신사동",
      "논현동",
      "압구정동",
      "청담동",
      "삼성동",
      "대치동",
      "개포동",
      "세곡동",
      "자곡동",
    ],
    종로구: [
      "청운효자동",
      "사직동",
      "삼청동",
      "부암동",
      "평창동",
      "무악동",
      "교남동",
      "가회동",
      "종로1·2·3·4가동",
      "종로5·6가동",
      "이화동",
      "혜화동",
      "명륜3가동",
      "창신1동",
      "창신2동",
      "창신3동",
      "숭인1동",
      "숭인2동",
    ],
    마포구: [
      "공덕동",
      "아현동",
      "도화동",
      "용강동",
      "대흥동",
      "염리동",
      "신수동",
      "서강동",
      "서교동",
      "합정동",
      "망원1동",
      "망원2동",
      "연남동",
      "성산1동",
      "성산2동",
      "상암동",
    ],
  };

  return (
    <div className="w-[360px] p-4 bg-white shadow-md overflow-y-auto h-screen">
      <h2 className="text-lg font-semibold mb-4">필터</h2>

      {/* 날짜 범위 */}
      <div className="mb-4">
        <Label className="block text-sm font-medium text-gray-700 mb-1">
          날짜 범위
        </Label>
        <div className="flex gap-2">
          <Input type="date" className="w-full" />
          <Input type="date" className="w-full" />
        </div>
      </div>

      {/* 시간대 슬라이더 */}
      <div className="mb-4">
        <Label className="block text-sm font-medium text-gray-700 mb-1">
          시간대 (예: {formatTime(timeRange[0])}~
          {formatTime(timeRange[0] + 300)})
        </Label>
        <Slider
          value={timeRange}
          onValueChange={setTimeRange}
          min={0}
          max={1440}
          step={30}
          className="w-full mt-2"
        />
        <div className="flex justify-between text-xs text-gray-500 mt-1">
          <span>00:00</span>
          <span>12:00</span>
          <span>24:00</span>
        </div>
      </div>

      {/* 자치구 */}
      <div className="mb-4">
        <Label className="block text-sm font-medium text-gray-700 mb-1">
          자치구
        </Label>
        <Select>
          <SelectTrigger className="w-full">
            <SelectValue placeholder="자치구 선택" />
          </SelectTrigger>
          <SelectContent>
            {districts.map((district) => (
              <SelectItem key={district} value={district}>
                {district}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {/* 행정동 */}
      <div className="mb-4">
        <Label className="block text-sm font-medium text-gray-700 mb-1">
          행정동
        </Label>
        <Select>
          <SelectTrigger className="w-full">
            <SelectValue placeholder="행정동 선택" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="yeoksam">역삼동</SelectItem>
            <SelectItem value="sinsa">신사동</SelectItem>
            <SelectItem value="nonhyeon">논현동</SelectItem>
            <SelectItem value="apgujeong">압구정동</SelectItem>
            <SelectItem value="cheongdam">청담동</SelectItem>
            <SelectItem value="samsung">삼성동</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* 소음 값 슬라이더 */}
      <div className="mb-4">
        <Label className="block text-sm font-medium text-gray-700 mb-1">
          소음 dB 범위 ({noiseRange[0]} dB 이상)
        </Label>
        <Slider
          value={noiseRange}
          onValueChange={setNoiseRange}
          min={40}
          max={100}
          step={5}
          className="w-full mt-2"
        />
        <div className="flex justify-between text-xs text-gray-500 mt-1">
          <span>40 dB</span>
          <span>70 dB</span>
          <span>100 dB</span>
        </div>
      </div>

      {/* 지역 유형 */}
      <div className="mb-6">
        <Label className="block text-sm font-medium text-gray-700 mb-2">
          지역 유형
        </Label>
        <div className="space-y-2">
          {[
            "상업 지역",
            "산업 지역",
            "주요 도로",
            "공원",
            "공공시설",
            "주거 지역",
            "도로 및 공원",
            "전통 시장",
          ].map((type, index) => (
            <div key={index} className="flex items-center space-x-2">
              <Checkbox id={`area-${index}`} defaultChecked />
              <Label
                htmlFor={`area-${index}`}
                className="text-sm cursor-pointer"
              >
                {type}
              </Label>
            </div>
          ))}
        </div>
      </div>

      <Button className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold">
        적용
      </Button>
    </div>
  );
}
