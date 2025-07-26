"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { createBoard } from "@/lib/boardApi";
import { getCurrentUser } from "@/lib/auth";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

const seoulDistricts = [
  "전체",
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
  강남구: ["전체", "역삼동", "신사동", "논현동", "압구정동", "청담동", "삼성동", "대치동", "개포동", "세곡동", "자곡동"],
  종로구: ["전체", "청운효자동", "사직동", "삼청동", "부암동", "평창동", "무악동", "교남동", "가회동", "종로1·2·3·4가동", "종로5·6가동", "이화동", "혜화동", "명륜3가동", "창신1동", "창신2동", "창신3동", "숭인1동", "숭인2동"],
  마포구: ["전체", "공덕동", "아현동", "도화동", "용강동", "대흥동", "염리동", "신수동", "서강동", "서교동", "합정동", "망원1동", "망원2동", "연남동", "성산1동", "성산2동", "상암동"],
  강동구: ["전체", "강일동", "상일동", "명일동", "고덕동", "암사동", "천호동", "성내동", "둔촌동", "길동"],
  강북구: ["전체", "미아동", "번동", "수유동", "우이동"],
  강서구: ["전체", "염창동", "등촌동", "화곡동", "가양동", "마곡동", "방화동", "공항동"],
  관악구: ["전체", "봉천동", "신림동", "남현동"],
  광진구: ["전체", "중곡동", "능동", "구의동", "광장동", "자양동", "화양동", "군자동"],
  구로구: ["전체", "신도림동", "구로동", "가리봉동", "고척동", "개봉동", "오류동", "천왕동", "항동"],
  금천구: ["전체", "가산동", "독산동", "시흥동"],
  노원구: ["전체", "월계동", "공릉동", "하계동", "상계동", "중계동"],
  도봉구: ["전체", "쌍문동", "방학동", "창동", "도봉동"],
  동대문구: ["전체", "회기동", "청량리동", "전농동", "답십리동", "장안동", "이문동", "휘경동", "제기동"],
  동작구: ["전체", "노량진동", "상도동", "흑석동", "동작동", "사당동", "대방동", "신대방동"],
  서대문구: ["전체", "충현동", "천연동", "북아현동", "신촌동", "연희동", "홍제동", "홍은동", "남가좌동", "북가좌동"],
  서초구: ["전체", "방배동", "양재동", "우면동", "원지동", "잠원동", "반포동", "서초동", "내곡동"],
  성동구: ["전체", "왕십리동", "마장동", "사근동", "행당동", "응봉동", "금호동", "옥수동", "성수동", "송정동", "용답동"],
  성북구: ["전체", "돈암동", "안암동", "보문동", "정릉동", "길음동", "종암동", "하월곡동", "상월곡동", "장위동", "석관동"],
  송파구: ["전체", "잠실동", "신천동", "풍납동", "송파동", "석촌동", "삼전동", "가락동", "문정동", "장지동", "방이동", "오금동", "거여동", "마천동"],
  양천구: ["전체", "신정동", "목동", "신월동"],
  영등포구: ["전체", "영등포동", "여의도동", "당산동", "도림동", "문래동", "양평동", "신길동", "대림동"],
  용산구: ["전체", "후암동", "용산동", "남영동", "청파동", "원효로동", "효창동", "이촌동", "이태원동", "한남동", "서빙고동", "보광동"],
  은평구: ["전체", "녹번동", "불광동", "갈현동", "구산동", "대조동", "응암동", "역촌동", "신사동", "증산동", "수색동", "진관동"],
  중구: ["전체", "회현동", "명동", "필동", "장충동", "광희동", "을지로동", "신당동", "다산동", "약수동", "청구동", "동화동", "황학동", "중림동"],
  중랑구: ["전체", "면목동", "상봉동", "중화동", "묵동", "망우동", "신내동"],
  전체: ["전체"],
};



export default function WritePost() {
  const router = useRouter();
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [autonomousDistrict, setAutonomousDistrict] = useState("");
  const [administrativeDistrict, setAdministrativeDistrict] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    const user = await getCurrentUser();
    if (!user) {
      setError("로그인 정보가 없습니다. 다시 로그인 해주세요.");
      return;
    }
    if (!title.trim() || !content.trim()) {
      setError("제목과 내용을 모두 입력해주세요.");
      return;
    }
    if (!autonomousDistrict || !administrativeDistrict) {
      setError("지역을 선택해주세요.");
      return;
    }
    setLoading(true);
    try {
      await createBoard({
        userId: user.userId,
        nickname: user.nickname,
        title,
        content,
        autonomousDistrict,
        administrativeDistrict,
        emotionalScore: 0
      });
      router.push("/boards");
    } catch (e) {
      setError("게시글 작성에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">게시글 작성</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          placeholder="제목을 입력하세요"
          value={title}
          onChange={e => setTitle(e.target.value)}
        />
        <Textarea
          placeholder="내용을 입력하세요"
          value={content}
          onChange={e => setContent(e.target.value)}
          rows={8}
        />
        <div className="flex gap-2">
          <Select
            value={autonomousDistrict || "전체"}
            onValueChange={value => {
              setAutonomousDistrict(value);
              setAdministrativeDistrict("전체");
            }}
          >
            <SelectTrigger className="w-full">
              <SelectValue placeholder="자치구 선택" />
            </SelectTrigger>
            <SelectContent>
              {seoulDistricts.map((district) => (
                <SelectItem key={district} value={district}>
                  {district}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <Select
            value={administrativeDistrict || "전체"}
            onValueChange={setAdministrativeDistrict}
            disabled={autonomousDistrict === "전체" || !dongsByDistrict[autonomousDistrict]}
          >
            <SelectTrigger className="w-full">
              <SelectValue placeholder="행정동 선택" />
            </SelectTrigger>
            <SelectContent>
              {dongsByDistrict[autonomousDistrict || "전체"]?.map((dong) => (
                <SelectItem key={dong} value={dong}>
                  {dong}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        {error && <div className="text-red-500 text-sm">{error}</div>}
        <Button type="submit" disabled={loading} className="w-full">
          {loading ? "작성 중..." : "작성하기"}
        </Button>
      </form>
    </div>
  );
}
