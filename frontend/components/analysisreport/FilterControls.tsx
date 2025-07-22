// components/FilterControls.tsx

"use client"

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

const districts = [
  { value: "all", label: "전체 구" },
  { value: "강동구", label: "강동구" },
  { value: "강서구", label: "강서구" },
  { value: "강남구", label: "강남구" },
  { value: "강북구", label: "강북구" },
]

interface FilterControlsProps {
  filters: {
    startDate: Date | null
    endDate: Date | null
    district: string
  }
  setFilters: (filters: any) => void
}

export default function FilterControls({ filters, setFilters }: FilterControlsProps) {

  const handlePrintPdf = () => {
    const element = document.getElementById("pdf-content");
    if (!element) return;

    html2canvas(element, { scale: 2, useCORS: true })
      .then((canvas) => {
        const imgData = canvas.toDataURL('image/png');

        // ✅ 1. 용지 방향을 가로(landscape)로 변경합니다.
        const pdf = new jsPDF('l', 'mm', 'a4');

        const pdfWidth = pdf.internal.pageSize.getWidth();
        const pdfHeight = pdf.internal.pageSize.getHeight();

        const imgWidth = canvas.width;
        const imgHeight = canvas.height;

        // ✅ 2. 이미지의 가로/세로 비율을 유지하면서 PDF 한 페이지에 맞게 크기를 계산합니다.
        const ratio = Math.min(pdfWidth / imgWidth, pdfHeight / imgHeight);
        const scaledWidth = imgWidth * ratio;
        const scaledHeight = imgHeight * ratio;

        // ✅ 3. 이미지를 페이지 중앙에 배치하기 위해 여백을 계산합니다.
        const xOffset = (pdfWidth - scaledWidth) / 2;
        const yOffset = (pdfHeight - scaledHeight) / 2;

        // ✅ 4. 다중 페이지 로직을 제거하고, 크기가 조절된 이미지를 한 페이지에 추가합니다.
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
        {/* 시작 날짜 선택기 */}
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
            calendarClassName="custom-calendar"
            customInput={<CustomDatePickerInput placeholder="시작일을 선택하세요" />}
            wrapperClassName="w-full"
            {...(filters.endDate && { maxDate: filters.endDate })}
          />
        </div>

        {/* 종료 날짜 선택기 */}
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
            calendarClassName="custom-calendar"
            customInput={<CustomDatePickerInput placeholder="종료일을 선택하세요" />}
            wrapperClassName="w-full"
            {...(filters.startDate && { minDate: filters.startDate })}
          />
        </div>

        {/* 구 선택 */}
        <div>
         <label className="block text-sm font-medium text-gray-700 mb-2">지역구</label>
          <Select
            value={filters.district}
            onValueChange={(value) => setFilters({ ...filters, district: value })}
          >
          <SelectTrigger className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 hover:border-blue-400 bg-white">
            <SelectValue placeholder="구를 선택하세요"/>
          </SelectTrigger>
            <SelectContent>
              {districts.map((district) => (
                <SelectItem key={district.value} value={district.value}>
                  {district.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>
    </Card>
  )
}
