"use client"

import { Volume2, Home, User, MessageSquare, Map, BarChart3, LogIn, LayoutDashboard } from "lucide-react"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import LoginPage from "../components/login-page"
import SeoulNoiseDashboard from "../components/seoul-noise-dashboard"
import MyPage from "../components/my-page"
import NoiseBoard from "../components/noise-board"
import PostDetail from "../components/post-detail"
import WritePost from "../components/write-post"
import DistrictDashboard from "../components/district-dashboard"
import SeoulMapV3 from "../components/seoul-map"
import FilterSidebar from "../components/filter-sidebar"
import AnalysisReport from "../components/analysis-report"

type PageType =
  | "main"
  | "login"
  | "mypage"
  | "board"
  | "post-detail"
  | "write-post"
  | "district-dashboard"
  | "noise-map"
  | "analysis-report"

export default function NoiSenseDashboard() {
  const [currentPage, setCurrentPage] = useState<PageType>("main")
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [selectedPostId, setSelectedPostId] = useState<number | null>(null)
  const [selectedDistrict, setSelectedDistrict] = useState<string>("강남구") // Default selected district

  const handleLogin = () => {
    setIsLoggedIn(true)
    setCurrentPage("main")
  }

  const handleLogout = () => {
    setIsLoggedIn(false)
    setCurrentPage("main")
  }

  const handlePostClick = (postId: number) => {
    setSelectedPostId(postId)
    setCurrentPage("post-detail")
  }

  const handleWriteClick = () => {
    if (isLoggedIn) {
      setCurrentPage("write-post")
    } else {
      setCurrentPage("login")
    }
  }

  const handleDistrictClick = (district: string) => {
    setSelectedDistrict(district)
    setCurrentPage("district-dashboard")
  }

  const handleBackToBoard = () => {
    setCurrentPage("board")
  }

  const handleWriteSubmit = () => {
    setCurrentPage("board")
  }

  // Show login page only when explicitly requested
  if (currentPage === "login") {
    return <LoginPage onLogin={handleLogin} />
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
                variant={currentPage === "district-dashboard" ? "default" : "ghost"}
                size="sm"
                onClick={() => setCurrentPage("district-dashboard")}
                className={`flex items-center gap-2 ${
                  currentPage === "district-dashboard" ? "bg-white shadow-sm text-purple-600" : "hover:bg-gray-200"
                }`}
              >
                <LayoutDashboard className="h-4 w-4" />
                지역 대시보드
              </Button>
              <Button
                variant={currentPage === "noise-map" ? "default" : "ghost"}
                size="sm"
                onClick={() => setCurrentPage("noise-map")}
                className={`flex items-center gap-2 ${
                  currentPage === "noise-map" ? "bg-white shadow-sm text-purple-600" : "hover:bg-gray-200"
                }`}
              >
                <Map className="h-4 w-4" />
                소음지도
              </Button>
              <Button
                variant={currentPage === "analysis-report" ? "default" : "ghost"}
                size="sm"
                onClick={() => setCurrentPage("analysis-report")}
                className={`flex items-center gap-2 ${
                  currentPage === "analysis-report" ? "bg-white shadow-sm text-purple-600" : "hover:bg-gray-200"
                }`}
              >
                <BarChart3 className="h-4 w-4" />
                분석리포트
              </Button>
              <Button
                variant={
                  currentPage === "board" || currentPage === "post-detail" || currentPage === "write-post"
                    ? "default"
                    : "ghost"
                }
                size="sm"
                onClick={() => setCurrentPage("board")}
                className={`flex items-center gap-2 ${
                  currentPage === "board" || currentPage === "post-detail" || currentPage === "write-post"
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
      {currentPage === "noise-map" && (
        <div className="flex">
          <FilterSidebar />
          <div className="flex-1 p-6">
            <SeoulMapV3 />
          </div>
        </div>
      )}
      {currentPage === "analysis-report" && <AnalysisReport />}
      {currentPage === "mypage" && isLoggedIn && <MyPage />}
      {currentPage === "board" && <NoiseBoard onPostClick={handlePostClick} onWriteClick={handleWriteClick} />}
      {currentPage === "post-detail" && <PostDetail onBack={handleBackToBoard} postId={0}
                                                    onEdit={function (postId: number): void {
                                                      throw new Error("Function not implemented.")
                                                    }} onDelete={function (postId: number): void {
        throw new Error("Function not implemented.")
      }} />}
      {currentPage === "write-post" && <WritePost onBack={handleBackToBoard} onSubmit={handleWriteSubmit} />}
      {currentPage === "district-dashboard" && <DistrictDashboard selectedDistrict={selectedDistrict} />}
    </div>
  )
}
