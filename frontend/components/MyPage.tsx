"use client"

import { useState, useEffect, useCallback } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Separator } from "@/components/ui/separator"
import { User, FileText, Trash2, Edit, Heart, LogOut, ChevronLeft, ChevronRight } from "lucide-react"
import { getUserInfo, updateUserInfo, logout, deleteUser, getUserActivityStats, UserData, UserActivityStats } from "@/lib/api/user"
import { getMyBoards, deleteBoard, BoardDto } from "@/lib/api/board"
import { getAutonomousDistricts, getDongsByGu, DistrictDto } from "@/lib/api/district"

// 1. 상태 관리 구조 개선: 연관 상태를 객체로 그룹화
type ProfileState = {
  nickname: string
  email: string
  isEditing: boolean
}

type DistrictState = {
  selectedGuCode: string
  selectedDongCode: string
  guList: DistrictDto[]
  dongList: DistrictDto[]
  guLoading: boolean
  dongLoading: boolean
}

type PostsState = {
  data: BoardDto[]
  currentPage: number
  totalPages: number
  totalElements: number
  loading: boolean
  error: string | null
}

type ActivityStatsState = {
  data: UserActivityStats | null
  loading: boolean
  error: string | null
}


export default function MyPage({ onPostClick, onEditClick }: { onPostClick?: (postId: number) => void; onEditClick?: (boardId: number) => void }) {
  const router = useRouter()

  // 페이지 전역 상태
  const [pageLoading, setPageLoading] = useState(true)
  const [pageError, setPageError] = useState<string | null>(null)

  // 상태 객체화
  const [profile, setProfile] = useState<ProfileState>({ nickname: "", email: "", isEditing: false })
  const [districts, setDistricts] = useState<DistrictState>({
    selectedGuCode: "",
    selectedDongCode: "",
    guList: [],
    dongList: [],
    guLoading: true,
    dongLoading: false,
  })
  const [posts, setPosts] = useState<PostsState>({
    data: [],
    currentPage: 0,
    totalPages: 0,
    totalElements: 0,
    loading: true,
    error: null,
  })
  const [activityStats, setActivityStats] = useState<ActivityStatsState>({ data: null, loading: true, error: null })

  const PAGE_SIZE = 5

  // 2. 로직 콜백으로 래핑: 함수 재생성 방지 및 의존성 명확화
  const fetchMyPosts = useCallback(async (page: number) => {
    setPosts(prev => ({ ...prev, loading: true }))
    try {
      const postsData = await getMyBoards(page, PAGE_SIZE)
      setPosts(prev => ({
        ...prev,
        data: postsData.content,
        currentPage: postsData.pageable.pageNumber,
        totalPages: postsData.totalPages,
        totalElements: postsData.totalElements,
        loading: false,
        error: null,
      }))
    } catch (err) {
      console.error('Error fetching user posts:', err)
      setPosts(prev => ({ ...prev, loading: false, error: '게시글을 불러오는 데 실패했습니다.' }))
    }
  }, [])

  const fetchActivityStats = useCallback(async () => {
    setActivityStats(prev => ({ ...prev, loading: true }))
    try {
      const stats = await getUserActivityStats()
      setActivityStats({ data: stats, loading: false, error: null })
    } catch (err) {
      console.error('Error fetching activity stats:', err)
      setActivityStats({ data: null, loading: false, error: '활동 통계를 불러오는 데 실패했습니다.' })
    }
  }, [])

  const loadDongsForGu = useCallback(async (guCode: string) => {
    if (!guCode) return
    setDistricts(prev => ({ ...prev, dongLoading: true, dongList: [] }))
    try {
      const dongs = await getDongsByGu(guCode)
      setDistricts(prev => ({ ...prev, dongList: dongs, dongLoading: false }))
      // '구' 변경 시 첫번째 '동'을 기본으로 선택
      if (dongs.length > 0) {
        setDistricts(prev => ({ ...prev, selectedDongCode: dongs[0].districtCode }))
      }
    } catch (err) {
      console.error('Error loading dongs for district:', err)
      setDistricts(prev => ({ ...prev, dongLoading: false }))
    }
  }, [])

  // 3. 데이터 로딩 로직 통합: 초기 렌더링 시 필요한 모든 데이터를 한곳에서 호출
  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        setPageLoading(true)
        // 병렬 API 호출로 로딩 시간 최적화
        const [userData, guData] = await Promise.all([getUserInfo(), getAutonomousDistricts()])

        setProfile(prev => ({ ...prev, nickname: userData.nickname || "", email: userData.email || "" }))
        setDistricts(prev => ({
          ...prev,
          guList: guData,
          selectedGuCode: userData.autonomousDistrict || "",
          selectedDongCode: userData.administrativeDistrict || "",
          guLoading: false,
        }))

        // 사용자의 '구' 정보가 있으면 해당 '동' 목록 로드
        if (userData.autonomousDistrict) {
          setDistricts(prev => ({...prev, dongLoading: true}));
          const dongs = await getDongsByGu(userData.autonomousDistrict);
          setDistricts(prev => ({...prev, dongList: dongs, dongLoading: false}));
        }

        // 다른 데이터들도 병렬로 호출
        Promise.all([fetchMyPosts(0), fetchActivityStats()])

      } catch (err) {
        console.error('Error fetching initial data:', err)
        setPageError('페이지를 불러오는 데 실패했습니다. 잠시 후 다시 시도해주세요.')
      } finally {
        setPageLoading(false)
      }
    }
    fetchInitialData()
  }, [fetchMyPosts, fetchActivityStats])


  const handleGuChange = (guCode: string) => {
    setDistricts(prev => ({ ...prev, selectedGuCode: guCode, selectedDongCode: "" }))
    loadDongsForGu(guCode)
  }

  const handleSaveProfile = async () => {
    setPageLoading(true)
    try {
      const userData: Partial<UserData> = {
        nickname: profile.nickname,
        email: profile.email,
        autonomousDistrict: districts.selectedGuCode,
        administrativeDistrict: districts.selectedDongCode,
      }
      await updateUserInfo(userData)
      setProfile(prev => ({ ...prev, isEditing: false }))
      // 저장 성공 후 활동 통계 정보도 다시 불러와서 '내 지역' 정보 갱신
      await fetchActivityStats()
    } catch (err) {
      console.error('Error updating user info:', err)
      setPageError('프로필 업데이트에 실패했습니다.')
    } finally {
      setPageLoading(false)
    }
  }

  const handleDeletePost = async (postId: number) => {
    // 4. UX 개선: confirm 대신 UI 라이브러리의 AlertDialog 사용을 권장
    // const confirmed = await showAlertDialog({ title: "삭제 확인", description: "정말로 이 게시글을 삭제하시겠습니까?" });
    if (window.confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
      const success = await deleteBoard(postId)
      if (success) {
        alert("게시글이 성공적으로 삭제되었습니다.")
        // 게시글 목록을 다시 불러와서 UI 갱신
        fetchMyPosts(posts.currentPage)
      } else {
        alert("게시글 삭제에 실패했습니다.")
      }
    }
  }

  const handleLogout = async () => {
    if (window.confirm("로그아웃 하시겠습니까?")) {
      await logout()
      // 페이지 전체 리로드 대신 router로 이동하는 것이 더 부드러운 사용자 경험을 제공
      router.push("/")
      // router.refresh() // 필요 시 서버 데이터를 다시 가져오기 위해 사용
    }
  }

  const handleWithdraw = async () => {
    if (window.confirm("정말로 회원탈퇴를 하시겠습니까?")) {
      await deleteUser()
      alert("회원탈퇴가 완료되었습니다.")
      router.push("/")
    }
  }

  const handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < posts.totalPages) {
      fetchMyPosts(newPage)
    }
  }

  const moveBoardPage = (boardId: number) => {
    if (onEditClick) {
      onEditClick(boardId);
    } else {
      router.push(`/boards/edit/${boardId}`);
    }
  }

  // 로딩 및 에러 UI
  if (pageLoading && !posts.data.length) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="animate-spin rounded-full h-16 w-16 border-b-4 border-blue-500"></div>
      </div>
    )
  }

  if (pageError) {
    return (
      <div className="flex flex-col justify-center items-center h-screen text-center">
        <p className="text-red-600 mb-4">{pageError}</p>
        <Button onClick={() => window.location.reload()} variant="outline">다시 시도</Button>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 p-4 md:p-8">
      <div className="max-w-4xl mx-auto space-y-8">
        <div className="text-center">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">마이페이지</h1>
          <p className="text-gray-600">프로필 관리 및 내 활동 확인</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Profile Section */}
          <Card className="shadow-lg">
            <CardHeader>
              <CardTitle className="flex items-center gap-2"><User /> 프로필 관리</CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="space-y-4">
                <div>
                  <Label htmlFor="nickname">닉네임</Label>
                  <Input id="nickname" value={profile.nickname} onChange={(e) => setProfile(p => ({ ...p, nickname: e.target.value }))} disabled={!profile.isEditing} />
                </div>
                <div>
                  <Label htmlFor="email">이메일</Label>
                  <Input id="email" type="email" value={profile.email} onChange={(e) => setProfile(p => ({ ...p, email: e.target.value }))} disabled={!profile.isEditing} />
                </div>
                <div>
                  <Label>내 지역</Label>
                  <div className="flex gap-2">
                    <Select value={districts.selectedGuCode} onValueChange={handleGuChange} disabled={!profile.isEditing || districts.guLoading}>
                      <SelectTrigger className="w-32"><SelectValue placeholder={districts.guLoading ? "로딩중..." : "구 선택"} /></SelectTrigger>
                      <SelectContent>{districts.guList.map(gu => <SelectItem key={gu.districtCode} value={gu.districtCode}>{gu.districtNameKo}</SelectItem>)}</SelectContent>
                    </Select>
                    <Select value={districts.selectedDongCode} onValueChange={(code) => setDistricts(p => ({ ...p, selectedDongCode: code }))} disabled={!profile.isEditing || districts.dongLoading || districts.dongList.length === 0}>
                      <SelectTrigger className="w-32"><SelectValue placeholder={districts.dongLoading ? "로딩중..." : "동 선택"} /></SelectTrigger>
                      <SelectContent>{districts.dongList.map(dong => <SelectItem key={dong.districtCode} value={dong.districtCode}>{dong.districtNameKo}</SelectItem>)}</SelectContent>
                    </Select>
                  </div>
                </div>
              </div>

              <Button onClick={profile.isEditing ? handleSaveProfile : () => setProfile(p => ({ ...p, isEditing: true }))} disabled={pageLoading} variant={profile.isEditing ? "default" : "secondary"} className="w-full">
                {profile.isEditing ? "저장하기" : "수정하기"}
              </Button>

              <Separator />

              <div className="space-y-4">
                <Button onClick={handleLogout} variant="outline" className="w-full"><LogOut className="h-4 w-4 mr-2" />로그아웃</Button>
                <Button onClick={handleWithdraw} variant="destructive" className="w-full"><Trash2 className="h-4 w-4 mr-2" />회원탈퇴</Button>
              </div>
            </CardContent>
          </Card>

          {/* Activity Stats Section */}
          <Card className="shadow-lg">
            <CardHeader>
              <CardTitle className="flex items-center gap-2"><FileText />내 활동 통계</CardTitle>
            </CardHeader>
            <CardContent>
              {activityStats.loading ? <p>활동 정보 로딩 중...</p> : activityStats.error ? <p className="text-red-500">{activityStats.error}</p> : (
                <div className="grid grid-cols-2 gap-4">
                  <div className="text-center p-4 bg-blue-50 rounded-lg">
                    <div className="text-2xl font-bold text-blue-600">{posts.totalElements}</div>
                    <div className="text-sm text-gray-600">작성한 글</div>
                  </div>
                  <div className="text-center p-4 bg-green-50 rounded-lg">
                    <div className="text-2xl font-bold text-green-600">{activityStats.data?.totalLikes || 0}</div>
                    <div className="text-sm text-gray-600">받은 좋아요</div>
                  </div>
                  <div className="text-center p-4 bg-purple-50 rounded-lg">
                    <div className="text-2xl font-bold text-purple-600">{activityStats.data?.totalComments || 0}</div>
                    <div className="text-sm text-gray-600">받은 댓글</div>
                  </div>
                  <div className="text-center p-4 bg-orange-50 rounded-lg">
                    <div className="text-xl font-bold text-orange-600">
                      {activityStats.data?.autonomousDistrictKo || '정보 없음'}<br />
                      {activityStats.data?.administrativeDistrictKo}
                    </div>
                    <div className="text-sm text-gray-600">내 지역</div>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* My Posts Section */}
        <Card className="shadow-lg">
          <CardHeader>
            <CardTitle className="flex items-center gap-2"><FileText />나의 게시글 목록</CardTitle>
          </CardHeader>
          <CardContent>
            {posts.loading && !posts.data.length ? <p>게시글 로딩 중...</p> : posts.error ? <p className="text-red-500">{posts.error}</p> : posts.data.length === 0 ? <p>작성한 게시글이 없습니다.</p> : (
              <div className="space-y-4">
                {posts.data.map(post => (
                  // 5. key 최적화: 고유 ID만 사용
                  <div key={post.boardId} className="border rounded-lg p-4 hover:bg-gray-50 transition-colors">
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <h3
                          className="font-semibold text-lg cursor-pointer hover:text-blue-600 transition-colors"
                          onClick={() => onPostClick?.(post.boardId)}
                        >
                          {post.title}
                        </h3>
                        <p className="text-gray-600 my-2 line-clamp-2">{post.content}</p>
                        <div className="flex items-center gap-4 text-sm text-gray-500">
                          <span className="flex items-center gap-1"><Heart className="h-4 w-4" />{post.empathyCount || 0}</span>
                          <span>{new Date(post.createdDate).toLocaleString('ko-KR')}</span>
                        </div>
                      </div>
                      <div className="flex gap-2 ml-4">
                        <Button size="sm" variant="outline" onClick={() => moveBoardPage(post.boardId)}><Edit className="h-4 w-4" /></Button>
                        <Button size="sm" variant="destructive" onClick={() => handleDeletePost(post.boardId)}><Trash2 className="h-4 w-4" /></Button>
                      </div>
                    </div>
                  </div>
                ))}

                {posts.totalPages > 1 && (
                  <div className="flex items-center justify-between mt-6">
                    <div className="text-sm text-gray-500">
                      총 {posts.totalElements}개 중 {posts.currentPage * PAGE_SIZE + 1}-{Math.min((posts.currentPage + 1) * PAGE_SIZE, posts.totalElements)}개
                    </div>
                    <div className="flex items-center gap-2">
                      <Button size="sm" variant="outline" onClick={() => handlePageChange(posts.currentPage - 1)} disabled={posts.currentPage === 0}><ChevronLeft /></Button>
                      <span>{posts.currentPage + 1} / {posts.totalPages}</span>
                      <Button size="sm" variant="outline" onClick={() => handlePageChange(posts.currentPage + 1)} disabled={posts.currentPage === posts.totalPages - 1}><ChevronRight /></Button>
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
