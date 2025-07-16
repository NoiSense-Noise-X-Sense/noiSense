"use client"

import { useState } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Badge } from "@/components/ui/badge"
import { Search, MapPin, Plus } from "lucide-react"

// Dummy data
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
]

const boardPosts = [
  {
    id: 1,
    title: "강남역 근처 공사 소음이 너무 심해요",
    content: "매일 아침 7시부터 공사 소음으로 잠을 못 자겠습니다. 언제까지 계속될까요?",
    nickname: "소음지킴이",
    region: "강남구",
    likes: 15,
    comments: 8,
    createdAt: "2024.07.05",
    isHot: true,
  },
  {
    id: 2,
    title: "아파트 층간소음 문제 해결 방법",
    content: "윗집에서 계속 뛰어다니는 소리가 나는데 어떻게 해결해야 할까요?",
    nickname: "평화주의자",
    region: "서초구",
    likes: 7,
    comments: 3,
    createdAt: "2024.07.04",
    isHot: false,
  },
  {
    id: 3,
    title: "도로 교통소음 개선 요청드립니다",
    content: "밤늦게까지 오토바이 소음이 심합니다. 단속 강화가 필요해요.",
    nickname: "조용한밤",
    region: "마포구",
    likes: 23,
    comments: 12,
    createdAt: "2024.07.03",
    isHot: true,
  },
  {
    id: 4,
    title: "학교 앞 카페 음악 소음",
    content: "새벽까지 음악 소리가 너무 커서 공부에 집중이 안 됩니다.",
    nickname: "수험생",
    region: "관악구",
    likes: 9,
    comments: 5,
    createdAt: "2024.07.02",
    isHot: false,
  },
  {
    id: 5,
    title: "지하철 소음 측정 결과 공유",
    content: "2호선 강남역에서 측정한 소음 수치를 공유합니다.",
    nickname: "데이터분석가",
    region: "강남구",
    likes: 31,
    comments: 18,
    createdAt: "2024.07.01",
    isHot: true,
  },
  {
    id: 6,
    title: "편의점 냉장고 소음 문제",
    content: "24시간 돌아가는 냉장고 소음이 너무 시끄러워요.",
    nickname: "불면증환자",
    region: "송파구",
    likes: 4,
    comments: 2,
    createdAt: "2024.06.30",
    isHot: false,
  },
]

export default function NoiseBoardV2({ 
  onPostClick, 
  onWriteClick 
}: { 
  onPostClick: (postId: number) => void
  onWriteClick: () => void 
}) {
  const [selectedRegion, setSelectedRegion] = useState("전체")
  const [searchQuery, setSearchQuery] = useState("")
  const [searchType, setSearchType] = useState("title")

  const filteredPosts = boardPosts.filter((post) => {
    const regionMatch = selectedRegion === "전체" || post.region === selectedRegion
    const searchMatch =
      searchQuery === "" ||
      (searchType === "title" && post.title.toLowerCase().includes(searchQuery.toLowerCase())) ||
      (searchType === "nickname" && post.nickname.toLowerCase().includes(searchQuery.toLowerCase())) ||
      (searchType === "content" && post.content.toLowerCase().includes(searchQuery.toLowerCase()))

    return regionMatch && searchMatch
  })

  return (
    <div className="min-h-screen bg-gray-50 p-4 md:p-8">
      <div className="max-w-6xl mx-auto space-y-8">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">소음 게시판2</h1>
            <p className="text-gray-600">소음 관련 정보를 공유하고 소통해보세요</p>
          </div>
          <Button onClick={onWriteClick} className="bg-blue-600 hover:bg-blue-700">
            <Plus className="h-4 w-4 mr-2" />
            글쓰기
          </Button>
        </div>

        {/* Filters and Search */}
        <Card className="shadow-lg">
          <CardContent className="p-6">
            <div className="flex flex-col md:flex-row gap-4">
              {/* Region Filter - 크기 줄임 */}
              <div className="w-full md:w-48">
                <Select value={selectedRegion} onValueChange={setSelectedRegion}>
                  <SelectTrigger>
                    <MapPin className="h-4 w-4 mr-2" />
                    <SelectValue />
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

              {/* Search Type */}
              <div className="w-full md:w-32">
                <Select value={searchType} onValueChange={setSearchType}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="title">제목</SelectItem>
                    <SelectItem value="nickname">닉네임</SelectItem>
                    <SelectItem value="content">내용</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              {/* Search Input */}
              <div className="flex-1">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                  <Input
                    placeholder="검색어를 입력하세요"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="pl-10"
                  />
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Posts List */}
        <div className="space-y-4">
          {filteredPosts.map((post) => (
            <Card
              key={post.id}
              className="shadow-md hover:shadow-lg transition-shadow cursor-pointer"
              onClick={() => onPostClick(post.id)}
            >
              <CardContent className="p-6">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-2">
                      {post.isHot && <Badge className="bg-red-500 hover:bg-red-600">HOT</Badge>}
                      <Badge variant="outline">
                        <MapPin className="h-3 w-3 mr-1" />
                        {post.region}
                      </Badge>
                    </div>

                    <h3 className="text-xl font-semibold mb-2 hover:text-blue-600 transition-colors">{post.title}</h3>

                    <p className="text-gray-600 mb-3 line-clamp-2">{post.content}</p>

                    <div className="flex items-center justify-between text-sm text-gray-500">
                      <div className="flex items-center gap-4">
                        \
