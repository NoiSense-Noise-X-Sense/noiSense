"use client";

import { useState, useEffect } from "react";
import {
  Volume2, Home, User, MessageSquare, Map, BarChart3, LogIn, LayoutDashboard
} from "lucide-react";
import { Button } from "@/components/ui/button";
import LoginPage from "../components/LoginPage";
import SeoulNoiseDashboard from "../components/SeoulNoiseDashboard";
import MyPage from "../components/MyPage";
import NoiseBoard from "../components/NoiseBoard";
import PostDetail from "../components/PostDetail";
import WritePost from "../components/WritePost";
import DistrictDashboard from "../components/DistrictDashboard";
import SeoulMapV3 from "../components/SeoulMap";
import FilterSidebar from "../components/FilterSidebar";
import AnalysisReport from "../components/AnalysisReport";
import { fetchWithAuth } from "@/lib/fetchWithAuth";

interface User {
  nickname: string;
  id: string;
  name: string;
  email: string;
}

// 내부 상태로 화면 전환
type PageType =
  | "main"
  | "login"
  | "mypage"
  | "board"
  | "PostDetail"
  | "WritePost"
  | "DistrictDashboard"
  | "NoiseMap"
  | "AnalysisReport";

export default function NoiSenseDashboard() {
  const [currentPage, setCurrentPage] = useState<PageType>("main");
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [selectedPostId, setSelectedPostId] = useState<number | null>(null);
  const [selectedDistrict, setSelectedDistrict] = useState<string>("강남구");

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/user', {
          method: 'GET',
          credentials: 'include'
        });
        if (!res.ok) {
          alert('인증만료');
          return;
        }
        const data = await res.json();
        //User.name=data.nickname;
        console.log("nickname :", data.nickname);
        // 여기서 상태로 유저 정보 저장하거나, 페이지 이동 등 처리 가능
      } catch (err) {
        console.error('유저 정보 요청 실패:', err);
      }
    };

    fetchUser();
  }, []);



  // --------- 이벤트 핸들러들 ---------
  const handleLogin = () => {
    setIsLoggedIn(true);
    setCurrentPage("main");
  };

  const handleLogout = async () => {
    try {
      await fetchWithAuth("/api/auth/logout", { method: "POST" });
    } catch (e) {}
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    sessionStorage.clear();
    setIsLoggedIn(false);
    setCurrentPage("login");
  };

  const handlePostClick = (postId: number) => {
    setSelectedPostId(postId);
    setCurrentPage("PostDetail");
  };

  const handleWriteClick = () => {
    if (isLoggedIn) {
      setCurrentPage("WritePost");
    } else {
      setCurrentPage("login");
    }
  };

  const handleDistrictClick = (district: string) => {
    setSelectedDistrict(district);
    setCurrentPage("DistrictDashboard");
  };

  const handleBackToBoard = () => {
    setCurrentPage("board");
  };

  const handleWriteSubmit = () => {
    setCurrentPage("board");
  };

  // ---- 조건부 렌더링 ----
  if (currentPage === "login") {
    // LoginPage에서 소셜 로그인 성공시 handleLogin 콜백 실행!
    return <LoginPage onLogin={handleLogin} />;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-xl flex items-center justify-center">
                <Volume2 className="h-6 w-6 text-white" />
              </div>
              <span className="text-2xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
                NoiSense
              </span>
            </div>
            {/* Navigation */}
            <div className="flex items-center space-x-1 bg-gray-100 p-1 rounded-lg">
              <Button
                variant={currentPage === "main" ? "default" : "ghost"}
                size="sm"
                onClick={() => setCurrentPage("main")}
                className={`flex items-center gap-2 ${
                  currentPage === "main" ? "bg-white shadow-sm text-purple-600" : "hover:bg-gray-200"
                }`}
              >
                <Home className="h-4 w-4" />
                메인
              </Button>
              <Button
                variant={currentPage === "DistrictDashboard" ? "default" : "ghost"}
                size="sm"
                onClick={() => setCurrentPage("DistrictDashboard")}
                className={`flex items-center gap-2 ${
                  currentPage === "DistrictDashboard" ? "bg-white shadow-sm text-purple-600" : "hover:bg-gray-200"
                }`}
              >
                <LayoutDashboard className="h-4 w-4" />
                지역 대시보드
              </Button>
              <Button
                variant={currentPage === "NoiseMap" ? "default" : "ghost"}
                size="sm"
                onClick={() => setCurrentPage("NoiseMap")}
                className={`flex items-center gap-2 ${
                  currentPage === "NoiseMap" ? "bg-white shadow-sm text-purple-600" : "hover:bg-gray-200"
                }`}
              >
                <Map className="h-4 w-4" />
                소음지도
              </Button>
              <Button
                variant={currentPage === "AnalysisReport" ? "default" : "ghost"}
                size="sm"
                onClick={() => setCurrentPage("AnalysisReport")}
                className={`flex items-center gap-2 ${
                  currentPage === "AnalysisReport" ? "bg-white shadow-sm text-purple-600" : "hover:bg-gray-200"
                }`}
              >
                <BarChart3 className="h-4 w-4" />
                분석리포트
              </Button>
              <Button
                variant={
                  currentPage === "board" || currentPage === "PostDetail" || currentPage === "WritePost"
                    ? "default"
                    : "ghost"
                }
                size="sm"
                onClick={() => setCurrentPage("board")}
                className={`flex items-center gap-2 ${
                  currentPage === "board" || currentPage === "PostDetail" || currentPage === "WritePost"
                    ? "bg-white shadow-sm text-purple-600"
                    : "hover:bg-gray-200"
                }`}
              >
                <MessageSquare className="h-4 w-4" />
                게시판
              </Button>
              {isLoggedIn && (
                <Button
                  variant={currentPage === "mypage" ? "default" : "ghost"}
                  size="sm"
                  onClick={() => setCurrentPage("mypage")}
                  className={`flex items-center gap-2 ${
                    currentPage === "mypage" ? "bg-white shadow-sm text-purple-600" : "hover:bg-gray-200"
                  }`}
                >
                  <User className="h-4 w-4" />
                  마이페이지
                </Button>
              )}
            </div>
            {/* Login/Logout Button */}
            {isLoggedIn ? (
              <Button onClick={handleLogout} variant="outline" size="sm">
                로그아웃
              </Button>
            ) : (
              <Button onClick={() => setCurrentPage("login")} variant="outline" size="sm">
                <LogIn className="h-4 w-4 mr-2" />
                로그인
              </Button>
            )}
          </div>
        </div>
      </header>
      {/* Main Content */}
      {currentPage === "main" && <SeoulNoiseDashboard onDistrictClick={handleDistrictClick} />}
      {currentPage === "NoiseMap" && (
        <div className="flex">
          <FilterSidebar />
          <div className="flex-1 p-6">
            <SeoulMapV3 />
          </div>
        </div>
      )}
      {currentPage === "AnalysisReport" && <AnalysisReport />}
      {currentPage === "mypage" && isLoggedIn && <MyPage />}
      {currentPage === "board" && (
        <NoiseBoard
          onPostClick={handlePostClick}
          onWriteClick={handleWriteClick}
        />
      )}
      {currentPage === "PostDetail" && selectedPostId !== null && (
        <PostDetail
          onBack={handleBackToBoard}
          postId={selectedPostId}
          onEdit={() => {/* 구현 필요 */}}
          onDelete={() => {/* 구현 필요 */}}
        />
      )}
      {currentPage === "WritePost" && (
        <WritePost onBack={handleBackToBoard} onSubmit={handleWriteSubmit} />
      )}
      {currentPage === "DistrictDashboard" && (
        <DistrictDashboard selectedDistrict={selectedDistrict} />
      )}
    </div>
  );
}
