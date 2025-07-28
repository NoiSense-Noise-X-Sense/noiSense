"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { createBoard, updateBoard, getBoardById, BoardPost } from "@/lib/boardApi";
import { getCurrentUser } from "@/lib/auth";
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
  "강동구": "11250",
};

// 행정동 매핑 (구별로 구분)
const administrativeDistrictMapping: { [key: string]: { [key: string]: string } } = {
  "종로구": {
    "청운효자동": "11010510",
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
    "혜화동": "11010650",
    "명륜3가동": "11010660",
    "창신1동": "11010670",
    "창신2동": "11010680",
    "창신3동": "11010690",
    "숭인1동": "11010700",
    "숭인2동": "11010710",
  },
  "강남구": {
    "역삼동": "11230101",
    "신사동": "11230102",
    "논현동": "11230103",
    "압구정동": "11230104",
    "청담동": "11230105",
    "삼성동": "11230106",
    "대치동": "11230107",
    "개포동": "11230108",
    "세곡동": "11230109",
    "자곡동": "11230110",
  },
  "강동구": {
    "강일동": "11250101",
    "상일동": "11250102",
    "명일동": "11250103",
    "고덕동": "11250104",
    "암사동": "11250105",
    "천호동": "11250106",
    "성내동": "11250107",
    "둔촌동": "11250108",
    "길동": "11250109",
  },
  "강북구": {
    "미아동": "11090101",
    "번동": "11090102",
    "수유동": "11090103",
    "우이동": "11090104",
  },
  "강서구": {
    "염창동": "11160101",
    "등촌동": "11160102",
    "화곡동": "11160103",
    "가양동": "11160104",
    "마곡동": "11160105",
    "방화동": "11160106",
    "공항동": "11160107",
  },
  "관악구": {
    "봉천동": "11210101",
    "신림동": "11210102",
    "남현동": "11210103",
  },
  "광진구": {
    "중곡동": "11050101",
    "능동": "11050102",
    "구의동": "11050103",
    "광장동": "11050104",
    "자양동": "11050105",
    "화양동": "11050106",
    "군자동": "11050107",
  },
  "구로구": {
    "신도림동": "11170101",
    "구로동": "11170102",
    "가리봉동": "11170103",
    "고척동": "11170104",
    "개봉동": "11170105",
    "오류동": "11170106",
    "천왕동": "11170107",
    "항동": "11170108",
  },
  "금천구": {
    "가산동": "11180101",
    "독산동": "11180102",
    "시흥동": "11180103",
  },
  "노원구": {
    "월계동": "11110101",
    "공릉동": "11110102",
    "하계동": "11110103",
    "상계동": "11110104",
    "중계동": "11110105",
  },
  "도봉구": {
    "쌍문동": "11100101",
    "방학동": "11100102",
    "창동": "11100103",
    "도봉동": "11100104",
  },
  "동대문구": {
    "회기동": "11060101",
    "청량리동": "11060102",
    "전농동": "11060103",
    "답십리동": "11060104",
    "장안동": "11060105",
    "이문동": "11060106",
    "휘경동": "11060107",
    "제기동": "11060108",
  },
  "동작구": {
    "노량진동": "11200101",
    "상도동": "11200102",
    "흑석동": "11200103",
    "동작동": "11200104",
    "사당동": "11200105",
    "대방동": "11200106",
    "신대방동": "11200107",
  },
  "서대문구": {
    "충현동": "11130101",
    "천연동": "11130102",
    "북아현동": "11130103",
    "신촌동": "11130104",
    "연희동": "11130105",
    "홍제동": "11130106",
    "홍은동": "11130107",
    "남가좌동": "11130108",
    "북가좌동": "11130109",
  },
  "서초구": {
    "방배동": "11220101",
    "양재동": "11220102",
    "우면동": "11220103",
    "원지동": "11220104",
    "잠원동": "11220105",
    "반포동": "11220106",
    "서초동": "11220107",
    "내곡동": "11220108",
  },
  "성동구": {
    "왕십리동": "11040101",
    "마장동": "11040102",
    "사근동": "11040103",
    "행당동": "11040104",
    "응봉동": "11040105",
    "금호동": "11040106",
    "옥수동": "11040107",
    "성수동": "11040108",
    "송정동": "11040109",
    "용답동": "11040110",
  },
  "성북구": {
    "돈암동": "11080101",
    "안암동": "11080102",
    "보문동": "11080103",
    "정릉동": "11080104",
    "길음동": "11080105",
    "종암동": "11080106",
    "하월곡동": "11080107",
    "상월곡동": "11080108",
    "장위동": "11080109",
    "석관동": "11080110",
  },
  "송파구": {
    "잠실동": "11240101",
    "신천동": "11240102",
    "풍납동": "11240103",
    "송파동": "11240104",
    "석촌동": "11240105",
    "삼전동": "11240106",
    "가락동": "11240107",
    "문정동": "11240108",
    "장지동": "11240109",
    "방이동": "11240110",
    "오금동": "11240111",
    "거여동": "11240112",
    "마천동": "11240113",
  },
  "양천구": {
    "신정동": "11150101",
    "목동": "11150102",
    "신월동": "11150103",
  },
  "영등포구": {
    "영등포동": "11190101",
    "여의도동": "11190102",
    "당산동": "11190103",
    "도림동": "11190104",
    "문래동": "11190105",
    "양평동": "11190106",
    "신길동": "11190107",
    "대림동": "11190108",
  },
  "용산구": {
    "후암동": "11030101",
    "용산동": "11030102",
    "남영동": "11030103",
    "청파동": "11030104",
    "원효로동": "11030105",
    "효창동": "11030106",
    "이촌동": "11030107",
    "이태원동": "11030108",
    "한남동": "11030109",
    "서빙고동": "11030110",
    "보광동": "11030111",
  },
  "은평구": {
    "녹번동": "11120101",
    "불광동": "11120102",
    "갈현동": "11120103",
    "구산동": "11120104",
    "대조동": "11120105",
    "응암동": "11120106",
    "역촌동": "11120107",
    "신사동": "11120108",
    "증산동": "11120109",
    "수색동": "11120110",
    "진관동": "11120111",
  },
  "중구": {
    "회현동": "11020101",
    "명동": "11020102",
    "필동": "11020103",
    "장충동": "11020104",
    "광희동": "11020105",
    "을지로동": "11020106",
    "신당동": "11020107",
    "다산동": "11020108",
    "약수동": "11020109",
    "청구동": "11020110",
    "동화동": "11020111",
    "황학동": "11020112",
    "중림동": "11020113",
  },
  "중랑구": {
    "면목동": "11070101",
    "상봉동": "11070102",
    "중화동": "11070103",
    "묵동": "11070104",
    "망우동": "11070105",
    "신내동": "11070106",
  },
  "마포구": {
    "공덕동": "11140101",
    "아현동": "11140102",
    "도화동": "11140103",
    "용강동": "11140104",
    "대흥동": "11140105",
    "염리동": "11140106",
    "신수동": "11140107",
    "서강동": "11140108",
    "서교동": "11140109",
    "합정동": "11140110",
    "망원1동": "11140111",
    "망원2동": "11140112",
    "연남동": "11140113",
    "성산1동": "11140114",
    "성산2동": "11140115",
    "상암동": "11140116",
  },
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

export default function WritePost({
                                    onBack,
                                    onSubmit,
                                    boardId,
                                  }: {
  onBack: () => void;
  onSubmit: () => void;
  boardId?: number;
}) {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [autonomousDistrict, setAutonomousDistrict] = useState("");
  const [administrativeDistrict, setAdministrativeDistrict] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [post, setPost] = useState<BoardPost | null>(null);
  const [initialLoading, setInitialLoading] = useState(false);

  // Determine if we're in edit mode
  const editBoardId = boardId || (searchParams?.get('boardId') ? Number(searchParams.get('boardId')) : null);
  const isEditMode = !!editBoardId;

  // Helper functions to convert district codes to names
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

  // Fetch existing post data when in edit mode
  useEffect(() => {
    if (isEditMode && editBoardId) {
      setInitialLoading(true);
      setError(null);
      getBoardById(editBoardId)
        .then((data) => {
          setPost(data);
          setTitle(data.title);
          setContent(data.content);
          // Convert codes to names for display
          setAutonomousDistrict(getAutonomousDistrictName(data.autonomousDistrict));
          setAdministrativeDistrict(getAdministrativeDistrictName(data.administrativeDistrict));
        })
        .catch(() => setError("게시글을 불러오지 못했습니다."))
        .finally(() => setInitialLoading(false));
    }
  }, [isEditMode, editBoardId]);

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

    // 지역구 코드 매핑
    const autonomousDistrictCode = autonomousDistrictMapping[autonomousDistrict];
    const administrativeDistrictCode = administrativeDistrictMapping[autonomousDistrict]?.[administrativeDistrict] as string | undefined;

    if (!autonomousDistrictCode || !administrativeDistrictCode) {
      setError("지역 코드를 찾을 수 없습니다.");
      return;
    }

    setLoading(true);
    try {
      if (isEditMode && editBoardId && post) {
        // Update existing post
        await updateBoard(editBoardId, user.userId, {
          title,
          content,
          autonomousDistrict: autonomousDistrictCode,
          administrativeDistrict: administrativeDistrictCode,
          viewCount: post.viewCount,
          empathyCount: post.empathyCount,
        });
      } else {
        // Create new post
        await createBoard({
          userId: user.userId,
          nickname: user.nickname,
          title,
          content,
          autonomousDistrict: autonomousDistrictCode,
          administrativeDistrict: administrativeDistrictCode,
          emotionalScore: 0
        });
      }
      onSubmit();
    } catch (e) {
      setError(isEditMode ? "게시글 수정에 실패했습니다." : "게시글 작성에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  // Show loading state when fetching post data for edit
  if (initialLoading) {
    return (
      <div className="max-w-2xl mx-auto p-4">
        <div className="flex items-center justify-center py-8">
          <div className="text-gray-500">게시글 정보를 불러오는 중...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">
        {isEditMode ? "게시글 수정" : "게시글 작성"}
      </h1>
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
          {loading
            ? (isEditMode ? "수정 중..." : "작성 중...")
            : (isEditMode ? "수정하기" : "작성하기")
          }
        </Button>
      </form>
    </div>
  );
}
