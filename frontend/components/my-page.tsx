"use client"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Separator } from "@/components/ui/separator"
import { Badge } from "@/components/ui/badge"
import { User, MapPin, FileText, Trash2, Edit, Heart } from "lucide-react"

// Dummy data
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
]

const myPosts = [
  {
    id: 1,
    title: "강남역 근처 공사 소음이 너무 심해요",
    content: "매일 아침 7시부터 공사 소음으로 잠을 못 자겠습니다...",
    region: "강남구",
    likes: 15,
    comments: 8,
    createdAt: "2024.07.05",
  },
  {
    id: 2,
    title: "아파트 층간소음 문제",
    content: "윗집에서 계속 뛰어다니는 소리가...",
    region: "서초구",
    likes: 7,
    comments: 3,
    createdAt: "2024.07.03",
  },
  {
    id: 3,
    title: "도로 교통소음 개선 요청",
    content: "밤늦게까지 오토바이 소음이 심합니다",
    region: "강남구",
    likes: 23,
    comments: 12,
    createdAt: "2024.07.01",
  },
]

export default function MyPage() {
  const [nickname, setNickname] = useState("소음지킴이")
  const [email, setEmail] = useState("user@example.com") // Added email state
  const [selectedRegion, setSelectedRegion] = useState("강남구")
  const [isEditing, setIsEditing] = useState(false)

  const handleSaveProfile = () => {
    setIsEditing(false)
    // Save profile logic here
    console.log("Profile saved:", { nickname, email, selectedRegion })
  }

  const handleDeletePost = (postId: number) => {
    // Delete post logic here
    console.log("Delete post:", postId)
  }

  const handleWithdraw = () => {
    if (confirm("정말로 회원탈퇴를 하시겠습니까?")) {
      // Withdraw logic here
      console.log("Account withdrawal")
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 p-4 md:p-8">
      <div className="max-w-4xl mx-auto space-y-8">
        {/* Header */}
        <div className="text-center">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">마이페이지</h1>
          <p className="text-gray-600">프로필 관리 및 내 활동 확인</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Profile Section */}
          <Card className="shadow-lg">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <User className="h-5 w-5" />
                프로필 관리
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="space-y-4">
                <div>
                  <Label htmlFor="nickname">닉네임</Label>
                  <div className="flex gap-2">
                    <Input
                      id="nickname"
                      value={nickname}
                      onChange={(e) => setNickname(e.target.value)}
                      disabled={!isEditing}
                    />
                  </div>
                </div>

                <div>
                  <Label htmlFor="email">이메일</Label>
                  <Input id="email" value={email} disabled className="bg-gray-50" />
                </div>

                <div>
                  <Label htmlFor="region">내 지역</Label>
                  <Select value={selectedRegion} onValueChange={setSelectedRegion} disabled={!isEditing}>
                    <SelectTrigger>
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
              </div>

              <Button
                onClick={isEditing ? handleSaveProfile : () => setIsEditing(true)}
                variant={isEditing ? "default" : "secondary"}
                className="w-full"
              >
                {isEditing ? "저장하기" : "수정하기"}
              </Button>

              <Separator />

              <div className="space-y-4">
                <Button onClick={handleWithdraw} variant="destructive" className="w-full">
                  <Trash2 className="h-4 w-4 mr-2" />
                  회원탈퇴
                </Button>
              </div>
            </CardContent>
          </Card>

          {/* Activity Stats */}
          <Card className="shadow-lg">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <FileText className="h-5 w-5" />내 활동 통계
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-2 gap-4">
                <div className="text-center p-4 bg-blue-50 rounded-lg">
                  <div className="text-2xl font-bold text-blue-600">{myPosts.length}</div>
                  <div className="text-sm text-gray-600">작성한 글</div>
                </div>
                <div className="text-center p-4 bg-green-50 rounded-lg">
                  <div className="text-2xl font-bold text-green-600">
                    {myPosts.reduce((sum, post) => sum + post.likes, 0)}
                  </div>
                  <div className="text-sm text-gray-600">받은 좋아요</div>
                </div>
                <div className="text-center p-4 bg-purple-50 rounded-lg">
                  <div className="text-2xl font-bold text-purple-600">
                    {myPosts.reduce((sum, post) => sum + post.comments, 0)}
                  </div>
                  <div className="text-sm text-gray-600">받은 댓글</div>
                </div>
                <div className="text-center p-4 bg-orange-50 rounded-lg">
                  <div className="text-2xl font-bold text-orange-600">{selectedRegion}</div>
                  <div className="text-sm text-gray-600">내 지역</div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* My Posts */}
        <Card className="shadow-lg">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <FileText className="h-5 w-5" />
              나의 게시글 목록
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {myPosts.map((post) => (
                <div key={post.id} className="border rounded-lg p-4 hover:bg-gray-50 transition-colors">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <h3 className="font-semibold text-lg">{post.title}</h3>
                        <Badge variant="outline">
                          <MapPin className="h-3 w-3 mr-1" />
                          {post.region}
                        </Badge>
                      </div>
                      <p className="text-gray-600 mb-3 line-clamp-2">{post.content}</p>
                      <div className="flex items-center gap-4 text-sm text-gray-500">
                        <span className="flex items-center gap-1">
                          <Heart className="h-4 w-4" />
                          {post.likes}
                        </span>
                        <span>댓글 {post.comments}</span>
                        <span>{post.createdAt}</span>
                      </div>
                    </div>
                    <div className="flex gap-2 ml-4">
                      <Button size="sm" variant="outline">
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => handleDeletePost(post.id)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
