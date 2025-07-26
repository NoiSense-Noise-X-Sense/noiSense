"use client";
import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import { getBoardById, updateBoard, BoardPost } from "@/lib/boardApi";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

// 자치구 매핑 (한글명 → 코드)
const autonomousDistrictMapping: { [key: string]: string } = {
  "종로구": "11010",
  "중구": "11020",
  "용산구": "11030",
  "성동구": "11040",
  "광진구": "11050",
  "동대문구": "11060",
  "중랑구": "11070",
  "성북구": "11080",
  "강북구": "11090",
  "도봉구": "11100",
  "노원구": "11110",
  "은평구": "11120",
  "서대문구": "11130",
  "마포구": "11140",
  "양천구": "11150",
  "강서구": "11160",
  "구로구": "11170",
  "금천구": "11180",
  "영등포구": "11190",
  "동작구": "11200",
  "관악구": "11210",
  "서초구": "11220",
  "강남구": "11230",
  "송파구": "11240",
  "강동구": "11250"
};

// 행정동 매핑 (한글명 → 코드)
const administrativeDistrictMapping: { [key: string]: string } = {
  "사직동": "11010530",
  "삼청동": "11010540",
  "부암동": "11010550",
  "평창동": "11010560",
  "무악동": "11010570",
  "교남동": "11010580",
  "가회동": "11010600",
  "종로1·2·3·4가동": "11010610",
  "종로5·6가동": "11010630",
  "이화동": "11010640",
  "창신1동": "11010670",
  "창신2동": "11010680",
  "창신3동": "11010690",
  "숭인1동": "11010700",
  "숭인2동": "11010710",
  "청운효자동": "11010720",
  "혜화동": "11010730",
  "소공동": "11020520",
  "회현동": "11020540",
  "명동": "11020550",
  "필동": "11020570",
  "장충동": "11020580",
  "광희동": "11020590",
  "을지로동": "11020600",
  "신당5동": "11020650",
  "황학동": "11020670",
  "중림동": "11020680",
  "신당동": "11020690",
  "다산동": "11020700",
  "약수동": "11020710",
  "청구동": "11020720",
  "동화동": "11020730",
  "후암동": "11030510",
  "용산2가동": "11030520",
  "남영동": "11030530",
  "원효로2동": "11030570",
  "효창동": "11030580",
  "용문동": "11030590",
  "이촌1동": "11030630",
  "이촌2동": "11030640",
  "이태원1동": "11030650",
  "이태원2동": "11030660",
  "서빙고동": "11030690",
  "보광동": "11030700",
  "청파동": "11030710",
  "원효로1동": "11030720",
  "한강로동": "11030730",
  "한남동": "11030740",
  "왕십리2동": "11040520",
  "마장동": "11040540",
  "사근동": "11040550",
  "행당1동": "11040560",
  "행당2동": "11040570",
  "응봉동": "11040580",
  "금호1가동": "11040590",
  "금호4가동": "11040620",
  "성수1가1동": "11040650",
  "성수1가2동": "11040660",
  "성수2가1동": "11040670",
  "성수2가3동": "11040680",
  "송정동": "11040690",
  "용답동": "11040700",
  "왕십리도선동": "11040710",
  "금호2·3가동": "11040720",
  "옥수동": "11040730",
  "화양동": "11050530",
  "군자동": "11050540",
  "중곡1동": "11050550",
  "중곡2동": "11050560",
  "중곡3동": "11050570",
  "중곡4동": "11050580",
  "능동": "11050590",
  "구의1동": "11050600",
  "구의2동": "11050610",
  "구의3동": "11050620",
  "광장동": "11050630",
  "자양1동": "11050640",
  "자양2동": "11050650",
  "자양3동": "11050660",
  "자양4동": "11050670",
  "회기동": "11060710",
  "휘경1동": "11060720",
  "휘경2동": "11060730",
  "청량리동": "11060800",
  "용신동": "11060810",
  "제기동": "11060820",
  "전농1동": "11060830",
  "전농2동": "11060840",
  "답십리2동": "11060860",
  "장안1동": "11060870",
  "장안2동": "11060880",
  "이문1동": "11060890",
  "이문2동": "11060900",
  "답십리1동": "11060910",
  "면목2동": "11070520",
  "면목4동": "11070540",
  "면목5동": "11070550",
  "면목7동": "11070570",
  "역삼동": "1123010100",
  "신사동": "1123010200",
  "논현동": "1123010300",
  "압구정동": "1123010400",
  "청담동": "1123010500",
  "삼성동": "1123010600",
  "대치동": "1123010700",
  "개포동": "1123010800",
  "세곡동": "1123010900",
  "자곡동": "1123011000",
  "봉천동": "1121010100",
  "신림동": "1121010200",
  "남현동": "1121010300"
};

// 코드 → 한글명 변환 함수
const getAutonomousDistrictName = (code: string): string => {
  for (const [name, districtCode] of Object.entries(autonomousDistrictMapping)) {
    if (districtCode === code) return name;
  }
  return code;
};

const getAdministrativeDistrictName = (code: string): string => {
  for (const [name, districtCode] of Object.entries(administrativeDistrictMapping)) {
    if (districtCode === code) return name;
  }
  return code;
};

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

import { getCurrentUser } from "@/lib/auth";

export default function EditPostPage() {
  const router = useRouter();
  const params = useParams();
  const postId = Number(params.id);

  const [post, setPost] = useState<BoardPost | null>(null);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [autonomousDistrict, setAutonomousDistrict] = useState("");
  const [administrativeDistrict, setAdministrativeDistrict] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [submitLoading, setSubmitLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    setError(null);
    getBoardById(postId)
      .then((data) => {
        setPost(data);
        setTitle(data.title);
        setContent(data.content);
        // 코드를 한글명으로 변환해서 표시
        setAutonomousDistrict(getAutonomousDistrictName(data.autonomousDistrict));
        setAdministrativeDistrict(getAdministrativeDistrictName(data.administrativeDistrict));
      })
      .catch(() => setError("게시글을 불러오지 못했습니다."))
      .finally(() => setLoading(false));
  }, [postId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    const user = await getCurrentUser();
    if (!user) {
      setError("로그인 정보가 없습니다. 다시 로그인 해주세요.");
      return;
    }
    if (!title.trim() || !content.trim() || !autonomousDistrict || !administrativeDistrict) {
      setError("모든 필드를 입력해주세요.");
      return;
    }
    if (!post) {
      setError("게시글 정보가 없습니다.");
      return;
    }
    setSubmitLoading(true);
    try {
      // 한글명을 코드로 변환
      const autonomousDistrictCode = autonomousDistrictMapping[autonomousDistrict] || autonomousDistrict;
      const administrativeDistrictCode = administrativeDistrictMapping[administrativeDistrict] || administrativeDistrict;

      await updateBoard(postId, user.userId, {
        title,
        content,
        autonomousDistrict: autonomousDistrictCode,
        administrativeDistrict: administrativeDistrictCode,
        viewCount: post.viewCount,
        empathyCount: post.empathyCount,
      });
      router.push("/boards");
    } catch (e) {
      setError("게시글 수정에 실패했습니다.");
    } finally {
      setSubmitLoading(false);
    }
  };

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center">불러오는 중...</div>;
  }
  if (error || !post) {
    return <div className="min-h-screen flex items-center justify-center text-red-500">{error || "게시글을 찾을 수 없습니다."}</div>;
  }

  return (
    <div className="max-w-2xl mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">게시글 수정</h1>
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
          <Select onValueChange={setAutonomousDistrict} value={autonomousDistrict}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="자치구 선택" />
            </SelectTrigger>
            <SelectContent>
              {seoulDistricts.map(district => (
                <SelectItem key={district} value={district}>{district}</SelectItem>
              ))}
            </SelectContent>
          </Select>
          <Select onValueChange={setAdministrativeDistrict} value={administrativeDistrict}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="행정동 선택" />
            </SelectTrigger>
            <SelectContent>
              {dongsByDistrict[autonomousDistrict]?.map(dong => (
                <SelectItem key={dong} value={dong}>{dong}</SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        {error && <div className="text-red-500 text-sm">{error}</div>}
        <Button type="submit" disabled={submitLoading} className="w-full">
          {submitLoading ? "수정 중..." : "수정하기"}
        </Button>
      </form>
    </div>
  );
}
