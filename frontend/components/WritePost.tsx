"use client";

import type React from "react";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ArrowLeft, Send } from "lucide-react";

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
  강동구: [
    "강일동",
    "상일동",
    "명일동",
    "고덕동",
    "암사동",
    "천호동",
    "성내동",
    "둔촌동",
    "길동",
  ],
  강북구: ["미아동", "번동", "수유동", "우이동"],
  강서구: [
    "염창동",
    "등촌동",
    "화곡동",
    "가양동",
    "마곡동",
    "방화동",
    "공항동",
  ],
  관악구: ["봉천동", "신림동", "남현동"],
  광진구: ["중곡동", "능동", "구의동", "광장동", "자양동", "화양동", "군자동"],
  구로구: [
    "신도림동",
    "구로동",
    "가리봉동",
    "고척동",
    "개봉동",
    "오류동",
    "천왕동",
    "항동",
  ],
  금천구: ["가산동", "독산동", "시흥동"],
  노원구: ["월계동", "공릉동", "하계동", "상계동", "중계동"],
  도봉구: ["쌍문동", "방학동", "창동", "도봉동"],
  동대문구: [
    "회기동",
    "청량리동",
    "전농동",
    "답십리동",
    "장안동",
    "이문동",
    "휘경동",
    "제기동",
  ],
  동작구: [
    "노량진동",
    "상도동",
    "흑석동",
    "동작동",
    "사당동",
    "대방동",
    "신대방동",
  ],
  서대문구: [
    "충현동",
    "천연동",
    "북아현동",
    "신촌동",
    "연희동",
    "홍제동",
    "홍은동",
    "남가좌동",
    "북가좌동",
  ],
  서초구: [
    "방배동",
    "양재동",
    "우면동",
    "원지동",
    "잠원동",
    "반포동",
    "서초동",
    "내곡동",
  ],
  성동구: [
    "왕십리동",
    "마장동",
    "사근동",
    "행당동",
    "응봉동",
    "금호동",
    "옥수동",
    "성수동",
    "송정동",
    "용답동",
  ],
  성북구: [
    "돈암동",
    "안암동",
    "보문동",
    "정릉동",
    "길음동",
    "종암동",
    "하월곡동",
    "상월곡동",
    "장위동",
    "석관동",
  ],
  송파구: [
    "잠실동",
    "신천동",
    "풍납동",
    "송파동",
    "석촌동",
    "삼전동",
    "가락동",
    "문정동",
    "장지동",
    "방이동",
    "오금동",
    "거여동",
    "마천동",
  ],
  양천구: ["신정동", "목동", "신월동"],
  영등포구: [
    "영등포동",
    "여의도동",
    "당산동",
    "도림동",
    "문래동",
    "양평동",
    "신길동",
    "대림동",
  ],
  용산구: [
    "후암동",
    "용산동",
    "남영동",
    "청파동",
    "원효로동",
    "효창동",
    "이촌동",
    "이태원동",
    "한남동",
    "서빙고동",
    "보광동",
  ],
  은평구: [
    "녹번동",
    "불광동",
    "갈현동",
    "구산동",
    "대조동",
    "응암동",
    "역촌동",
    "신사동",
    "증산동",
    "수색동",
    "진관동",
  ],
  중구: [
    "회현동",
    "명동",
    "필동",
    "장충동",
    "광희동",
    "을지로동",
    "신당동",
    "다산동",
    "약수동",
    "청구동",
    "동화동",
    "황학동",
    "중림동",
  ],
  중랑구: ["면목동", "상봉동", "중화동", "묵동", "망우동", "신내동"],
};

export default function WritePost({
  onBack,
  onSubmit,
}: {
  onBack: () => void;
  onSubmit: () => void;
}) {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [selectedDistrict, setSelectedDistrict] = useState("");
  const [selectedDong, setSelectedDong] = useState("");

  const handleDistrictChange = (value: string) => {
    setSelectedDistrict(value);
    setSelectedDong(""); // Reset dong when district changes
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title || !content || !selectedDistrict || !selectedDong) {
      alert("모든 필드를 채워주세요.");
      return;
    }
    console.log("New post submitted:", {
      title,
      content,
      district: selectedDistrict,
      dong: selectedDong,
    });
    alert("게시글이 성공적으로 작성되었습니다!");
    onSubmit(); // Navigate back to board or show success
  };

  return (
    <div className="min-h-screen bg-gray-50 p-4 md:p-8">
      <div className="max-w-4xl mx-auto space-y-8">
        {/* Back Button */}
        <Button
          variant="ghost"
          onClick={onBack}
          className="flex items-center gap-2 text-gray-600 hover:text-gray-900"
        >
          <ArrowLeft className="h-5 w-5" />
          목록으로 돌아가기
        </Button>

        {/* Write Post Card */}
        <Card className="shadow-lg">
          <CardHeader>
            <CardTitle className="text-2xl font-bold">새 게시글 작성</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <Label htmlFor="title">제목</Label>
                <Input
                  id="title"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="게시글 제목을 입력하세요"
                  required
                />
              </div>

              <div>
                <Label htmlFor="district">자치구</Label>
                <Select
                  value={selectedDistrict}
                  onValueChange={handleDistrictChange}
                  required
                >
                  <SelectTrigger>
                    <SelectValue placeholder="자치구를 선택하세요" />
                  </SelectTrigger>
                  <SelectContent>
                    {seoulDistricts.map((district) => (
                      <SelectItem key={district} value={district}>
                        {district}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              {selectedDistrict && (
                <div>
                  <Label htmlFor="dong">행정동</Label>
                  <Select
                    value={selectedDong}
                    onValueChange={setSelectedDong}
                    required
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="행정동을 선택하세요" />
                    </SelectTrigger>
                    <SelectContent>
                      {dongsByDistrict[selectedDistrict]?.map((dong) => (
                        <SelectItem key={dong} value={dong}>
                          {dong}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              )}

              <div>
                <Label htmlFor="content">내용</Label>
                <Textarea
                  id="content"
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                  placeholder="게시글 내용을 입력하세요. (최소 10자)"
                  rows={10}
                  required
                  minLength={10}
                />
              </div>

              <Button type="submit" className="w-full flex items-center gap-2">
                <Send className="h-5 w-5" />
                게시글 작성 완료
              </Button>
            </form>
          </CardContent>
        </Card>

        {/* Writing Guidelines (Optional) */}
        <Card className="shadow-sm border-l-4 border-blue-500">
          <CardHeader>
            <CardTitle className="text-lg font-semibold text-blue-700">
              게시글 작성 가이드라인
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm text-gray-700 space-y-2">
            <p>• 구체적인 소음 발생 시간, 장소, 유형을 명시해주세요.</p>
            <p>• 불필요한 비방이나 욕설은 삼가주세요.</p>
            <p>• 개인 정보는 포함하지 않도록 주의해주세요.</p>
            <p>• 다른 사용자의 게시글을 존중하고 건설적인 의견을 나눠주세요.</p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
