"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Separator } from "@/components/ui/separator"
import { Badge } from "@/components/ui/badge"
import { User, MapPin, FileText, Trash2, Edit, Heart, LogOut, ChevronLeft, ChevronRight } from "lucide-react"
import { getUserInfo, updateUserInfo, logout, deleteUser, getUserActivityStats, UserData, UserActivityStats } from "@/lib/api/user"
import { getMyBoards, deleteBoard, BoardDto, PageResponse } from "@/lib/api/board"
import { getAutonomousDistricts, getDongsByGu, DistrictDto } from "@/lib/api/district"


export default function MyPage() {
  const router = useRouter()
  const [nickname, setNickname] = useState("")
  const [email, setEmail] = useState("")
  const [selectedDistrict, setSelectedDistrict] = useState("")
  // 기본 동(구에 맞는 첫 번째 동으로)
  const [selectedDong, setSelectedDong] = useState("")
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState("")

  // District API data
  const [autonomousDistricts, setAutonomousDistricts] = useState<DistrictDto[]>([])
  const [availableDongs, setAvailableDongs] = useState<DistrictDto[]>([])
  const [districtsLoading, setDistrictsLoading] = useState(false)
  const [dongsLoading, setDongsLoading] = useState(false)
  const [selectedDistrictObj, setSelectedDistrictObj] = useState<DistrictDto | null>(null)

  // Separate state for displaying in statistics (only updates after successful API call)
  const [displayDistrictObj, setDisplayDistrictObj] = useState<DistrictDto | null>(null)
  const [displayDong, setDisplayDong] = useState("")
  const [displayDongs, setDisplayDongs] = useState<DistrictDto[]>([])
  const [myPosts, setMyPosts] = useState<BoardDto[]>([])
  const [postsLoading, setPostsLoading] = useState(true)
  const [postsError, setPostsError] = useState("")
  const [currentPage, setCurrentPage] = useState(0)
  const [pageSize, setPageSize] = useState(5)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)

  // Activity stats state
  const [activityStats, setActivityStats] = useState<UserActivityStats | null>(null)
  const [activityStatsLoading, setActivityStatsLoading] = useState(true)
  const [activityStatsError, setActivityStatsError] = useState("")

  const [isEditing, setIsEditing] = useState(false)

  // Load dongs for selected district
  const loadDongsForDistrict = async (districtCode: string, userDong?: string) => {
    try {
      setDongsLoading(true)
      const dongs = await getDongsByGu(districtCode)
      setAvailableDongs(dongs)

      // If user has a saved dong (districtCode), keep it selected, otherwise select the first dong
      if (userDong && dongs.some(d => d.districtCode === userDong)) {
        // User dong is already a districtCode, use it directly
        setSelectedDong(userDong)
      } else if (dongs.length > 0 && !selectedDong) {
        setSelectedDong(dongs[0].districtCode)
      }
    } catch (err) {
      console.error('Error loading dongs for district:', districtCode, err)
      setAvailableDongs([])
    } finally {
      setDongsLoading(false)
    }
  }

  // Fetch user activity stats
  const fetchActivityStats = async () => {
    try {
      setActivityStatsLoading(true)
      setActivityStatsError("")
      const stats = await getUserActivityStats()
      setActivityStats(stats)
    } catch (err) {
      console.error('Error fetching activity stats:', err)
      setActivityStatsError('활동 통계를 불러오는데 실패했습니다.')
    } finally {
      setActivityStatsLoading(false)
    }
  }

  // 사용자 정보 가져오기
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        setIsLoading(true)

        // Load districts and user info in parallel
        const [userData, districts] = await Promise.all([
          getUserInfo(),
          getAutonomousDistricts()
        ])

        // Set districts data
        setAutonomousDistricts(districts)

        // 사용자 정보 설정
        setNickname(userData.nickname || "")
        setEmail(userData.email || "")

        // Find the district object that matches user's stored district code
        const userDistrictObj = districts.find(d => d.districtCode === userData.autonomousDistrict)
        setSelectedDistrictObj(userDistrictObj || null)

        // Set selected district using districtCode if user has one
        setSelectedDistrict(userData.autonomousDistrict || "")
        setSelectedDong(userData.administrativeDistrict || "")

        // Initialize display state for statistics section
        setDisplayDistrictObj(userDistrictObj || null)
        setDisplayDong(userData.administrativeDistrict || "")

        // If user has a district, load the dongs for that district
        if (userDistrictObj && userDistrictObj.districtCode) {
          await loadDongsForDistrict(userDistrictObj.districtCode, userData.administrativeDistrict)
          // Also load dongs for display in statistics section
          try {
            const displayDongsData = await getDongsByGu(userDistrictObj.districtCode)
            setDisplayDongs(displayDongsData)
          } catch (err) {
            console.error('Error loading display dongs:', err)
            setDisplayDongs([])
          }
        }

        setIsLoading(false)
      } catch (err) {
        console.error('Error fetching user info:', err)

        // Provide more specific error messages based on the error type
        let errorMessage = 'Failed to load user information'
        if (err instanceof Error) {
          if (err.message.includes('Failed to fetch autonomous districts')) {
            errorMessage = '지역 정보를 불러오는데 실패했습니다. 네트워크 연결을 확인해주세요.'
          } else if (err.message.includes('로그인 만료')) {
            errorMessage = '로그인이 만료되었습니다. 다시 로그인해주세요.'
          } else if (err.message.includes('Failed to fetch')) {
            errorMessage = '서버와의 연결에 실패했습니다. 잠시 후 다시 시도해주세요.'
          }
        }

        setError(errorMessage)
        setIsLoading(false)
      }
    }

    fetchUserInfo()
  }, [])

  // 사용자 게시글 가져오기
  const fetchMyPosts = async (page = currentPage) => {
    try {
      setPostsLoading(true)

      const postsData = await getMyBoards(page, pageSize)

      // 게시글 정보 설정
      setMyPosts(postsData.content)
      setCurrentPage(postsData.pageable.pageNumber)
      setTotalPages(postsData.totalPages)
      setTotalElements(postsData.totalElements)

      setPostsLoading(false)
    } catch (err) {
      console.error('Error fetching user posts:', err)
      setPostsError('Failed to load user posts')
      setPostsLoading(false)
    }
  }

  // 페이지 변경 핸들러
  const handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < totalPages) {
      setCurrentPage(newPage)
      fetchMyPosts(newPage)
    }
  }

  useEffect(() => {
    fetchMyPosts()
    fetchActivityStats()
  }, [])

  // 구 변경 시 동도 같이 갱신 (해당 구 첫 번째 동으로)
  const handleDistrictChange = async (districtCode: string) => {
    setSelectedDistrict(districtCode)

    // Find the district object that matches the selected district code
    const districtObj = autonomousDistricts.find(d => d.districtCode === districtCode)
    setSelectedDistrictObj(districtObj || null)

    // Clear current dong selection
    setSelectedDong("")
    setAvailableDongs([])

    // Load dongs for the selected district (no user dong since district changed)
    if (districtObj && districtObj.districtCode) {
      await loadDongsForDistrict(districtObj.districtCode)
    }
  }

  const handleSaveProfile = async () => {
    try {
      setIsLoading(true)

      // Use the districtCode directly as required
      const userData: Partial<UserData> = {
        nickname,
        email,
        autonomousDistrict: selectedDistrict,
        administrativeDistrict: selectedDong
      }

      const updatedUser = await updateUserInfo(userData)
      console.log("Profile updated successfully:", updatedUser)

      // Update display state for statistics section after successful API call
      const updatedDistrictObj = autonomousDistricts.find(d => d.districtCode === updatedUser.autonomousDistrict)
      setDisplayDistrictObj(updatedDistrictObj || null)
      setDisplayDong(updatedUser.administrativeDistrict || "")

      // Update displayDongs for statistics section
      if (updatedDistrictObj && updatedDistrictObj.districtCode) {
        try {
          const updatedDisplayDongs = await getDongsByGu(updatedDistrictObj.districtCode)
          setDisplayDongs(updatedDisplayDongs)
        } catch (err) {
          console.error('Error loading updated display dongs:', err)
        }
      }

      setIsEditing(false)
      setIsLoading(false)
    } catch (err) {
      console.error('Error updating user info:', err)
      setError('Failed to update user information')
      setIsLoading(false)
    }
    fetchActivityStats();
  }



  const moveBoardPage = (boardId: number) => {
     router.push(`/board/edit/${boardId}`);
  }

  const handleDeletePost = async (postId: number) => {
    if (confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
      const success = await deleteBoard(postId)
      if (success) {
        // Remove the deleted post from the current list
        setMyPosts(prevPosts => prevPosts.filter(post => post.boardId !== postId))
        // Update total elements count
        setTotalElements(prev => prev - 1)
        alert("게시글이 성공적으로 삭제되었습니다.")
      } else {
        alert("게시글 삭제에 실패했습니다. 다시 시도해주세요.")
      }
    }
  }

  const handleLogout = async () => {
    if (confirm("로그아웃 하시겠습니까?")) {
      const success = await logout()
      if (success) {
        // Redirect to main page
        window.location.reload();
      } else {
        alert("로그아웃에 실패했습니다. 다시 시도해주세요.")
      }
    }
  }

  const handleWithdraw = async () => {
    if (confirm("정말로 회원탈퇴를 하시겠습니까?")) {
      const success = await deleteUser()
      if (success) {
        // Navigate to main page
        window.location.href = "/"
      } else {
        alert("회원탈퇴에 실패했습니다. 다시 시도해주세요.")
      }
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
              {isLoading ? (
                <div className="flex justify-center items-center h-40">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
                </div>
              ) : error ? (
                <div className="text-center p-4 bg-red-50 rounded-lg text-red-600">
                  {error}
                  <Button
                    onClick={() => window.location.reload()}
                    variant="outline"
                    className="mt-2"
                  >
                    다시 시도
                  </Button>
                </div>
              ) : (
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
                    <Input
                      id="email"
                      type="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      disabled={!isEditing}
                    />
                  </div>

                  <div>
                    <Label>내 지역</Label>
                    <div className="flex gap-2">
                      {/* 구 선택 */}
                      <Select
                        value={selectedDistrict}
                        onValueChange={handleDistrictChange}
                        disabled={!isEditing || districtsLoading}
                      >
                        <SelectTrigger className="w-32">
                          <SelectValue placeholder={districtsLoading ? "로딩중..." : "구 선택"} />
                        </SelectTrigger>
                        <SelectContent>
                          {autonomousDistricts.map((district, index) => (
                            <SelectItem key={`district-${district.districtCode}-${index}`} value={district.districtCode}>
                              {district.districtNameKo}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>

                      {/* 동 선택 */}
                      <Select
                        value={selectedDong}
                        onValueChange={setSelectedDong}
                        disabled={!isEditing || dongsLoading || availableDongs.length === 0}
                      >
                        <SelectTrigger className="w-32">
                          <SelectValue placeholder={dongsLoading ? "로딩중..." : "동 선택"} />
                        </SelectTrigger>
                        <SelectContent>
                          {availableDongs.map((dong, index) => (
                            <SelectItem key={`dong-${dong.districtCode}-${index}`} value={dong.districtCode}>
                              {dong.districtNameKo}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                  </div>
                </div>
              )}

              {!isLoading && !error && (
                <Button
                  onClick={isEditing ? handleSaveProfile : () => setIsEditing(true)}
                  variant={isEditing ? "default" : "secondary"}
                  className="w-full"
                  disabled={isLoading}
                >
                  {isLoading ? (
                    <span className="flex items-center">
                      <span className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></span>
                      처리 중...
                    </span>
                  ) : isEditing ? "저장하기" : "수정하기"}
                </Button>
              )}

              <Separator />

              <div className="space-y-4">
                <Button onClick={handleLogout} variant="outline" className="w-full mb-2">
                  <LogOut className="h-4 w-4 mr-2" />
                  로그아웃
                </Button>
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
              {activityStatsLoading ? (
                <div className="flex justify-center items-center h-40">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
                </div>
              ) : activityStatsError ? (
                <div className="text-center p-4 bg-red-50 rounded-lg text-red-600">
                  {activityStatsError}
                  <Button
                    onClick={fetchActivityStats}
                    variant="outline"
                    className="mt-2"
                  >
                    다시 시도
                  </Button>
                </div>
              ) : (
                <div className="grid grid-cols-2 gap-4">
                  <div className="text-center p-4 bg-blue-50 rounded-lg">
                    <div className="text-2xl font-bold text-blue-600">
                      {totalElements || 0}
                    </div>
                    <div className="text-sm text-gray-600">작성한 글</div>
                  </div>
                  <div className="text-center p-4 bg-green-50 rounded-lg">
                    <div className="text-2xl font-bold text-green-600">
                      {activityStats?.totalLikes || 0}
                    </div>
                    <div className="text-sm text-gray-600">받은 좋아요</div>
                  </div>
                  <div className="text-center p-4 bg-purple-50 rounded-lg">
                    <div className="text-2xl font-bold text-purple-600">
                      {activityStats?.totalComments || 0}
                    </div>
                    <div className="text-sm text-gray-600">받은 댓글</div>
                  </div>
                  <div className="text-center p-4 bg-orange-50 rounded-lg">
                    <div className="text-2xl font-bold text-orange-600">
                      {activityStats?.autonomousDistrictKo || '정보 없음'}<br></br>
                      {activityStats?.administrativeDistrictKo}
                    </div>
                    <div className="text-sm text-gray-600">내 지역</div>
                  </div>
                </div>
              )}
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
            {postsLoading ? (
              <div className="flex justify-center items-center h-40">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
              </div>
            ) : postsError ? (
              <div className="text-center p-4 bg-red-50 rounded-lg text-red-600">
                {postsError}
                <Button
                  onClick={() => window.location.reload()}
                  variant="outline"
                  className="mt-2"
                >
                  다시 시도
                </Button>
              </div>
            ) : myPosts.length === 0 ? (
              <div className="text-center p-8 text-gray-500">
                작성한 게시글이 없습니다.
              </div>
            ) : (
              <div className="space-y-4">
                {myPosts.map((post, index) => (
                  <div key={`post-${post.boardId}-${index}`} className="border rounded-lg p-4 hover:bg-gray-50 transition-colors">
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-2">
                          <h3 className="font-semibold text-lg">{post.title}</h3>
                          <Badge variant="outline">
                            <MapPin className="h-3 w-3 mr-1" />
                            {post.autonomousDistrictName}
                          </Badge>
                        </div>
                        <p className="text-gray-600 mb-3 line-clamp-2">{post.content}</p>
                        <div className="flex items-center gap-4 text-sm text-gray-500">
                          <span className="flex items-center gap-1">
                            <Heart className="h-4 w-4" />
                            {post.empathyCount ? post.empathyCount : 0}
                          </span>
                          {new Intl.DateTimeFormat('ko-KR', {
                            year: 'numeric',
                            month: '2-digit',
                            day: '2-digit',
                            hour: '2-digit',
                            minute: '2-digit',
                            hour12: false,
                          }).format(new Date(post.createdDate))}
                        </div>
                      </div>
                      <div className="flex gap-2 ml-4">
                        <Button size="sm" variant="outline" onClick={() => moveBoardPage(post.boardId)}>
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button size="sm" variant="destructive" onClick={() => handleDeletePost(post.boardId)}>
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                  </div>
                ))}

                {/* Pagination Controls */}
                {totalElements > 0 && (
                  <div className="flex items-center justify-between mt-6">
                    <div className="text-sm text-gray-500">
                      총 {totalElements}개 중 {currentPage * pageSize + 1}-{Math.min((currentPage + 1) * pageSize, totalElements)}개 표시
                    </div>
                    <div className="flex items-center gap-2">
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handlePageChange(currentPage - 1)}
                        disabled={currentPage === 0}
                      >
                        <ChevronLeft className="h-4 w-4" />
                      </Button>
                      <span className="text-sm">
                        {currentPage + 1} / {totalPages}
                      </span>
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handlePageChange(currentPage + 1)}
                        disabled={currentPage === totalPages - 1}
                      >
                        <ChevronRight className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                )}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
