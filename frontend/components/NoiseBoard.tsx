'use client';

import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { MessageSquare, MapPin, Heart, Eye, PlusCircle } from "lucide-react";
import NoiseBoardSearchBar from "./NoiseBoardSearchBar";
import { getBoards, searchBoards, BoardPost, BoardSearchResponse } from "@/lib/boardApi";

// 자치구 매핑 (코드 → 한글명)
const autonomousDistrictMapping: { [key: string]: string } = {
  "11010": "종로구",
  "11020": "중구", 
  "11030": "용산구",
  "11040": "성동구",
  "11050": "광진구",
  "11060": "동대문구",
  "11070": "중랑구",
  "11080": "성북구",
  "11090": "강북구",
  "11100": "도봉구",
  "11110": "노원구",
  "11120": "은평구",
  "11130": "서대문구",
  "11140": "마포구",
  "11150": "양천구",
  "11160": "강서구",
  "11170": "구로구",
  "11180": "금천구",
  "11190": "영등포구",
  "11200": "동작구",
  "11210": "관악구",
  "11220": "서초구",
  "11230": "강남구",
  "11240": "송파구",
  "11250": "강동구"
};

// 행정동 매핑 (코드 → 한글명)
const administrativeDistrictMapping: { [key: string]: string } = {
  "11010530": "사직동",
  "11010540": "삼청동", 
  "11010550": "부암동",
  "11010560": "평창동",
  "11010570": "무악동",
  "11010580": "교남동",
  "11010600": "가회동",
  "11010610": "종로1·2·3·4가동",
  "11010630": "종로5·6가동",
  "11010640": "이화동",
  "11010670": "창신1동",
  "11010680": "창신2동",
  "11010690": "창신3동",
  "11010700": "숭인1동",
  "11010710": "숭인2동",
  "11010720": "청운효자동",
  "11010730": "혜화동",
  "11020520": "소공동",
  "11020540": "회현동",
  "11020550": "명동",
  "11020570": "필동",
  "11020580": "장충동",
  "11020590": "광희동",
  "11020600": "을지로동",
  "11020650": "신당5동",
  "11020670": "황학동",
  "11020680": "중림동",
  "11020690": "신당동",
  "11020700": "다산동",
  "11020710": "약수동",
  "11020720": "청구동",
  "11020730": "동화동",
  "11030510": "후암동",
  "11030520": "용산2가동",
  "11030530": "남영동",
  "11030570": "원효로2동",
  "11030580": "효창동",
  "11030590": "용문동",
  "11030630": "이촌1동",
  "11030640": "이촌2동",
  "11030650": "이태원1동",
  "11030660": "이태원2동",
  "11030690": "서빙고동",
  "11030700": "보광동",
  "11030710": "청파동",
  "11030720": "원효로1동",
  "11030730": "한강로동",
  "11030740": "한남동",
  "11040520": "왕십리2동",
  "11040540": "마장동",
  "11040550": "사근동",
  "11040560": "행당1동",
  "11040570": "행당2동",
  "11040580": "응봉동",
  "11040590": "금호1가동",
  "11040620": "금호4가동",
  "11040650": "성수1가1동",
  "11040660": "성수1가2동",
  "11040670": "성수2가1동",
  "11040680": "성수2가3동",
  "11040690": "송정동",
  "11040700": "용답동",
  "11040710": "왕십리도선동",
  "11040720": "금호2·3가동",
  "11040730": "옥수동",
  "11050530": "화양동",
  "11050540": "군자동",
  "11050550": "중곡1동",
  "11050560": "중곡2동",
  "11050570": "중곡3동",
  "11050580": "중곡4동",
  "11050590": "능동",
  "11050600": "구의1동",
  "11050610": "구의2동",
  "11050620": "구의3동",
  "11050630": "광장동",
  "11050640": "자양1동",
  "11050650": "자양2동",
  "11050660": "자양3동",
  "11050670": "자양4동",
  "11060710": "회기동",
  "11060720": "휘경1동",
  "11060730": "휘경2동",
  "11060800": "청량리동",
  "11060810": "용신동",
  "11060820": "제기동",
  "11060830": "전농1동",
  "11060840": "전농2동",
  "11060860": "답십리2동",
  "11060870": "장안1동",
  "11060880": "장안2동",
  "11060890": "이문1동",
  "11060900": "이문2동",
  "11060910": "답십리1동",
  "11070520": "면목2동",
  "11070540": "면목4동",
  "11070550": "면목5동",
  "11070570": "면목7동",
  "1123010100": "역삼동",
  "1123010200": "신사동",
  "1123010300": "논현동",
  "1123010400": "압구정동",
  "1123010500": "청담동",
  "1123010600": "삼성동",
  "1123010700": "대치동",
  "1123010800": "개포동",
  "1123010900": "세곡동",
  "1123011000": "자곡동",
  "1121010100": "봉천동",
  "1121010200": "신림동",
  "1121010300": "남현동"
};

// 코드 → 한글명 변환 함수
const getAutonomousDistrictName = (code: string): string => {
  return autonomousDistrictMapping[code] || code;
};

const getAdministrativeDistrictName = (code: string): string => {
  return administrativeDistrictMapping[code] || code;
};

export default function NoiseBoard({
  onPostClick,
  onWriteClick,
}: {
  onPostClick: (postId: number) => void;
  onWriteClick: () => void;
}) {
  const [currentFilters, setCurrentFilters] = useState({
    district: "전체",
    dong: "전체",
    searchTerm: "",
  });
  const [currentPage, setCurrentPage] = useState(1);
  const [posts, setPosts] = useState<BoardPost[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const postsPerPage = 5;

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        let response: BoardSearchResponse;
        if (currentFilters.searchTerm) {
          response = await searchBoards(currentFilters.searchTerm, currentPage - 1, postsPerPage);
        } else {
          response = await getBoards(currentPage - 1, postsPerPage);
        }
        let filtered = response.content;
        console.log("게시글 데이터:", filtered);
        if (currentFilters.district !== "전체") {
          filtered = filtered.filter(post => post.autonomousDistrict === currentFilters.district);
        }
        if (currentFilters.dong !== "전체") {
          filtered = filtered.filter(post => post.administrativeDistrict === currentFilters.dong);
        }
        setPosts(filtered);
        setTotalPages(response.totalPages);
      } catch (e) {
        setError("게시글 목록을 불러오지 못했습니다.");
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [currentFilters, currentPage]);

  const handleSearch = (filters: {
    district: string;
    dong: string;
    searchTerm: string;
  }) => {
    setCurrentFilters(filters);
    setCurrentPage(1);
  };

  const pageNumbers = Array.from({ length: totalPages }, (_, i) => i + 1);
  const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

  return (
    <div className="min-h-screen bg-gray-50 p-4 md:p-8">
      <div className="max-w-4xl mx-auto space-y-8">
        {/* Header */}
        <div className="text-center">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            소음 제보 게시판
          </h1>
          <p className="text-gray-600">
            주변 소음 문제를 공유하고 해결책을 찾아보세요.
          </p>
        </div>

        {/* Search and Filter Section */}
        <NoiseBoardSearchBar onSearch={handleSearch} />

        {/* Write Post Button */}
        <div className="flex justify-end">
          <Button onClick={onWriteClick} className="flex items-center gap-2">
            <PlusCircle className="h-5 w-5" />
            글쓰기
          </Button>
        </div>

        {/* Post List */}
        <Card className="shadow-lg">
          <CardHeader>
            <CardTitle className="text-xl font-semibold">전체 게시글</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {loading ? (
                <div className="text-center py-10 text-gray-500">불러오는 중...</div>
              ) : error ? (
                <div className="text-center py-10 text-red-500">{error}</div>
              ) : posts.length > 0 ? (
                posts.map((post) => (
                  <div
                    key={post.boardId}
                    className="border rounded-lg p-4 hover:bg-gray-50 transition-colors cursor-pointer"
                    onClick={() => onPostClick(post.boardId)}
                  >
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-2">
                          <h3 className="font-semibold text-lg">
                            {post.title}
                          </h3>
                          <Badge
                            variant="secondary"
                            className="flex items-center gap-1"
                          >
                            <MapPin className="h-3 w-3" />
                            {getAutonomousDistrictName(post.autonomousDistrict)} {getAdministrativeDistrictName(post.administrativeDistrict)}
                          </Badge>
                        </div>
                        <p className="text-gray-600 mb-3 line-clamp-2">
                          {post.content}
                        </p>
                        <div className="flex items-center gap-4 text-sm text-gray-500">
                          <span className="flex items-center gap-1">
                            <Heart className="h-4 w-4" />
                            {post.empathyCount}
                          </span>
                          <span className="flex items-center gap-1">
                            <MessageSquare className="h-4 w-4" />
                            {post.commentCount}
                          </span>
                          <span className="flex items-center gap-1">
                            <Eye className="h-4 w-4" />
                            {post.viewCount}
                          </span>
                          <span>{post.createdDate}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <div className="text-center py-10 text-gray-500">
                  게시글이 없습니다.
                </div>
              )}
            </div>
          </CardContent>
        </Card>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex justify-center space-x-2 mt-8">
            {pageNumbers.map((number) => (
              <Button
                key={number}
                onClick={() => paginate(number)}
                variant={currentPage === number ? "default" : "outline"}
                size="sm"
              >
                {number}
              </Button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
