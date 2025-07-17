"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { MessageSquare, MapPin, Heart, Eye, PlusCircle } from "lucide-react";
import NoiseBoardSearchBar from "./NoiseBoardSearchBar"; // Import the new search bar component

// Dummy data for posts
const dummyPosts = [
  {
    id: 1,
    title: "강남역 근처 공사 소음이 너무 심해요",
    content:
      "매일 아침 7시부터 공사 소음으로 잠을 못 자겠습니다. 주말에도 계속되어서 너무 힘드네요.",
    region: "강남구",
    dong: "역삼동",
    likes: 15,
    comments: 8,
    views: 120,
    createdAt: "2024.07.05",
  },
  {
    id: 2,
    title: "아파트 층간소음 문제 해결 방안 공유",
    content:
      "윗집에서 계속 뛰어다니는 소리가 들려요. 혹시 좋은 해결 방안 있으실까요?",
    region: "서초구",
    dong: "반포동",
    likes: 7,
    comments: 3,
    views: 80,
    createdAt: "2024.07.03",
  },
  {
    id: 3,
    title: "도로 교통소음 개선 요청합니다",
    content:
      "밤늦게까지 오토바이 소음이 심합니다. 특히 주말에는 더 심해서 창문을 열 수가 없어요.",
    region: "강남구",
    dong: "논현동",
    likes: 23,
    comments: 12,
    views: 200,
    createdAt: "2024.07.01",
  },
  {
    id: 4,
    title: "새벽 시간 쓰레기 수거 소음",
    content:
      "새벽 4시에 쓰레기 수거 차량 소음 때문에 매번 잠에서 깹니다. 시간 조절이 필요해요.",
    region: "송파구",
    dong: "잠실동",
    likes: 10,
    comments: 5,
    views: 95,
    createdAt: "2024.06.28",
  },
  {
    id: 5,
    title: "공원 내 확성기 소음 민원",
    content:
      "주말마다 공원에서 확성기 소리가 너무 커서 휴식을 방해합니다. 조치가 필요합니다.",
    region: "마포구",
    dong: "연남동",
    likes: 5,
    comments: 2,
    views: 60,
    createdAt: "2024.06.25",
  },
  {
    id: 6,
    title: "옆집 리모델링 소음",
    content:
      "한 달째 옆집 리모델링 소음으로 고통받고 있습니다. 주말에도 공사를 강행하네요.",
    region: "영등포구",
    dong: "당산동",
    likes: 12,
    comments: 6,
    views: 150,
    createdAt: "2024.06.20",
  },
  {
    id: 7,
    title: "학교 운동장 소음",
    content:
      "저녁 늦게까지 학교 운동장에서 발생하는 소음 때문에 아이들이 잠을 못 잡니다.",
    region: "노원구",
    dong: "상계동",
    likes: 8,
    comments: 4,
    views: 100,
    createdAt: "2024.06.18",
  },
  {
    id: 8,
    title: "상가 건물 에어컨 실외기 소음",
    content: "밤새도록 상가 건물 에어컨 실외기 소리가 너무 커서 잠을 설칩니다.",
    region: "중구",
    dong: "명동",
    likes: 18,
    comments: 9,
    views: 180,
    createdAt: "2024.06.15",
  },
  {
    id: 9,
    title: "새벽 배달 오토바이 소음",
    content: "새벽 시간 배달 오토바이들이 너무 시끄럽게 다녀서 잠을 깨네요.",
    region: "동대문구",
    dong: "회기동",
    likes: 11,
    comments: 5,
    views: 130,
    createdAt: "2024.06.12",
  },
  {
    id: 10,
    title: "공사장 야간 작업 소음",
    content:
      "야간에도 공사장에서 작업 소음이 발생하여 주민들이 불편을 겪고 있습니다.",
    region: "강서구",
    dong: "마곡동",
    likes: 20,
    comments: 10,
    views: 210,
    createdAt: "2024.06.10",
  },
  {
    id: 11,
    title: "반려동물 짖는 소리 민원",
    content: "옆집 강아지가 밤낮으로 짖어서 생활에 방해가 됩니다.",
    region: "관악구",
    dong: "신림동",
    likes: 6,
    comments: 3,
    views: 75,
    createdAt: "2024.06.08",
  },
  {
    id: 12,
    title: "버스 정류장 안내 방송 소음",
    content: "버스 정류장 안내 방송 소리가 너무 커서 집 안까지 들립니다.",
    region: "성북구",
    dong: "길음동",
    likes: 9,
    comments: 4,
    views: 90,
    createdAt: "2024.06.05",
  },
];

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
  const postsPerPage = 5; // Display 5 posts per page

  const handleSearch = (filters: {
    district: string;
    dong: string;
    searchTerm: string;
  }) => {
    setCurrentFilters(filters);
    setCurrentPage(1); // Reset to first page on new search
  };

  const filteredPosts = dummyPosts
    .filter((post) => {
      const matchesDistrict =
        currentFilters.district === "전체" ||
        post.region === currentFilters.district;
      const matchesDong =
        currentFilters.dong === "전체" || post.dong === currentFilters.dong;
      const matchesSearchTerm =
        currentFilters.searchTerm === "" ||
        post.title
          .toLowerCase()
          .includes(currentFilters.searchTerm.toLowerCase()) ||
        post.content
          .toLowerCase()
          .includes(currentFilters.searchTerm.toLowerCase());

      return matchesDistrict && matchesDong && matchesSearchTerm;
    })
    .sort(
      (a, b) =>
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    ); // Always sort by latest

  // Pagination logic
  const indexOfLastPost = currentPage * postsPerPage;
  const indexOfFirstPost = indexOfLastPost - postsPerPage;
  const currentPosts = filteredPosts.slice(indexOfFirstPost, indexOfLastPost);

  const totalPages = Math.ceil(filteredPosts.length / postsPerPage);
  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

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
              {currentPosts.length > 0 ? (
                currentPosts.map((post) => (
                  <div
                    key={post.id}
                    className="border rounded-lg p-4 hover:bg-gray-50 transition-colors cursor-pointer"
                    onClick={() => onPostClick(post.id)}
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
                            {post.region} {post.dong}
                          </Badge>
                        </div>
                        <p className="text-gray-600 mb-3 line-clamp-2">
                          {post.content}
                        </p>
                        <div className="flex items-center gap-4 text-sm text-gray-500">
                          <span className="flex items-center gap-1">
                            <Heart className="h-4 w-4" />
                            {post.likes}
                          </span>
                          <span className="flex items-center gap-1">
                            <MessageSquare className="h-4 w-4" />
                            {post.comments}
                          </span>
                          <span className="flex items-center gap-1">
                            <Eye className="h-4 w-4" />
                            {post.views}
                          </span>
                          <span>{post.createdAt}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <div className="text-center py-10 text-gray-500">
                  <p>게시글이 없습니다.</p>
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
