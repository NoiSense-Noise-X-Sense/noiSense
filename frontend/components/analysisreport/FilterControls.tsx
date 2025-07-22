"use client"

import { useState, useEffect } from "react"
import { Card } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import ReactDatePicker, { registerLocale } from "react-datepicker"
import "react-datepicker/dist/react-datepicker.css"
import { ko } from "date-fns/locale/ko"
import CustomDatePickerInput from "@/components/analysisreport/CustomDatePickerInput"
import { Button } from "@/components/ui/button"
import { Printer } from "lucide-react"
import jsPDF from "jspdf"
import html2canvas from "html2canvas"

registerLocale("ko", ko)

interface District {
  districtId: number;
  districtName: string;
}

interface FilterControlsProps {
  filters: {
    startDate: Date | null
    endDate: Date | null
    district: string
  }
  setFilters: (filters: any) => void
}

export default function FilterControls({ filters, setFilters }: FilterControlsProps) {
  const [districts, setDistricts] = useState<District[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchDistricts = async () => {
      try {
        // 1. 실제 컨트롤러 주소로 수정
        const response = await fetch("http://localhost:8080/api/v1/district/autonomousDistrict");
        if (!response.ok) {
          throw new Error("자치구 목록을 불러오는 데 실패했습니다.");
        }

        // 2. ResponseDto 구조에 맞게 데이터 파싱
        const result = await response.json();

        // 3. success가 true이고 data가 배열일 때만 상태 업데이트
        if (result.success && Array.isArray(result.data)) {
          setDistricts(result.data);
        } else {
          console.error("API 응답 데이터 형식이 올바르지 않습니다:", result.errorMessage);
        }
      } catch (error) {
        console.error(error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDistricts();
  }, []);

  const handlePrintPdf = () => {
    // PDF 변환 로직 (기존과 동일)
    const element = document.getElementById("pdf-content");
    if (!element) return;
    html2canvas(element, { scale: 2, useCORS: true }).then((canvas) => {
      const imgData = canvas.toDataURL('image/png');
      const pdf = new jsPDF('l', 'mm', 'a4');
      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = pdf.internal.pageSize.getHeight();
      const ratio = Math.min(pdfWidth / canvas.width, pdfHeight / canvas.height);
      const scaledWidth = canvas.width * ratio;
      const scaledHeight = canvas.height * ratio;
      const xOffset = (pdfWidth - scaledWidth) / 2;
      const yOffset = (pdfHeight - scaledHeight) / 2;
      pdf.addImage(imgData, 'PNG', xOffset, yOffset, scaledWidth, scaledHeight);
      pdf.save(`소음-리포트-${new Date().toLocaleDateString()}.pdf`);
    });
  };

  return (
    <Card className="p-6 bg-slate-50">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold">상세 검색</h2>
        <Button onClick={handlePrintPdf} variant="outline">
          <Printer className="mr-2 h-4 w-4" />
          PDF 변환
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 items-end">
        {/* 날짜 선택기는 변경 없음 */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">시작일</label>
          <ReactDatePicker
            locale="ko"
            selected={filters.startDate}
            onChange={(date) => setFilters({ ...filters, startDate: date })}
            selectsStart
            startDate={filters.startDate}
            endDate={filters.endDate}
            dateFormat="yyyy년 MM월 dd일"
            customInput={<CustomDatePickerInput placeholder="시작일을 선택하세요" />}
            wrapperClassName="w-full"
            {...(filters.endDate && { maxDate: filters.endDate })}
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">종료일</label>
          <ReactDatePicker
            locale="ko"
            selected={filters.endDate}
            onChange={(date) => setFilters({ ...filters, endDate: date })}
            selectsEnd
            startDate={filters.startDate}
            endDate={filters.endDate}
            dateFormat="yyyy년 MM월 dd일"
            customInput={<CustomDatePickerInput placeholder="종료일을 선택하세요" />}
            wrapperClassName="w-full"
            {...(filters.startDate && { minDate: filters.startDate })}
          />
        </div>

        {/* 구 선택 메뉴는 변경 없음 (데이터 소스만 바뀜) */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">지역구</label>
          <Select
            value={filters.district}
            onValueChange={(value) => setFilters({ ...filters, district: value })}
            disabled={isLoading}
          >
            <SelectTrigger className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 hover:border-blue-400 bg-white">
              <SelectValue placeholder={isLoading ? "불러오는 중..." : "구를 선택하세요"} />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">전체 구</SelectItem>
              {districts.map((district) => (
                <SelectItem key={district.districtId} value={district.districtName}>
                  {district.districtName}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>
    </Card>
  )
}
