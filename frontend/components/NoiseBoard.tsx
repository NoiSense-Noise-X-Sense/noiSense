'use client';

import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { MessageSquare, MapPin, Heart, Eye, PlusCircle } from "lucide-react";
import NoiseBoardSearchBar from "./NoiseBoardSearchBar";
import { getBoards, searchBoards, BoardPost, BoardSearchResponse } from "@/lib/boardApi";
import { getAutonomousDistrictName, getAdministrativeDistrictName, initializeDistrictMappings } from "@/lib/api/district";

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
  const [districtMappingsLoaded, setDistrictMappingsLoaded] = useState(false);
  const postsPerPage = 5;

  // Initialize district mappings on component mount
  useEffect(() => {
    const initMappings = async () => {
      try {
        await initializeDistrictMappings();
        setDistrictMappingsLoaded(true);
      } catch (error) {
        console.error('Failed to initialize district mappings:', error);
        setDistrictMappingsLoaded(true); // Still allow rendering even if mappings fail
      }
    };
    initMappings();
  }, []);

  useEffect(() => {
    // Only fetch data after district mappings are loaded
    if (!districtMappingsLoaded) return;

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
  }, [currentFilters, currentPage, districtMappingsLoaded]);

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
              {!districtMappingsLoaded || loading ? (
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
                          <span>{new Date(post.createdDate).toLocaleString('ko-KR')}</span>
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
