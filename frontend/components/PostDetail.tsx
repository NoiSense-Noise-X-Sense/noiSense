"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Textarea } from "@/components/ui/textarea";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  ArrowLeft,
  Heart,
  MessageSquare,
  Eye,
  Edit,
  Trash2,
  Send,
  MapPin,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { getBoardById, BoardPost, getComments, createComment, deleteComment, toggleEmpathy, Comment as CommentType } from "@/lib/boardApi";

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

import { getCurrentUser, CurrentUser } from "@/lib/auth";

export default function PostDetail({
                                     postId,
                                     onBack,
                                     onEdit,
                                     onDelete,
                                   }: {
  postId: number;
  onBack: () => void;
  onEdit: (postId: number) => void;
  onDelete: (postId: number) => void;
}) {
  const router = useRouter();
  const [post, setPost] = useState<BoardPost | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    console.log('PostDetail 마운트', postId);
    return () => {
      console.log('PostDetail 언마운트', postId);
    };
  }, []);

  useEffect(() => {
    console.log('getBoardById 호출', postId);
    const fetchPost = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await getBoardById(postId);
        setPost(data);
        setIsLiked(!!data.isEmpathized);
        setEmpathyCount(data.empathyCount ?? 0);
      } catch (e) {
        setError("게시글을 불러오지 못했습니다.");
      } finally {
        setLoading(false);
      }
    };
    fetchPost();
  }, [postId]);

  const [commentContent, setCommentContent] = useState("");
  const [isLiked, setIsLiked] = useState(false); // State for like button
  const [empathyCount, setEmpathyCount] = useState(0);
  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [comments, setComments] = useState<CommentType[]>([]);
  const [commentLoading, setCommentLoading] = useState(false);
  const [commentError, setCommentError] = useState<string | null>(null);
  const [commentSubmitLoading, setCommentSubmitLoading] = useState(false);
  const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);
  const [userLoading, setUserLoading] = useState(true);

  // 현재 로그인한 유저 정보 불러오기
  useEffect(() => {
    const fetchCurrentUser = async () => {
      setUserLoading(true);
      try {
        const user = await getCurrentUser();
        setCurrentUser(user);
      } catch (error) {
        console.error('Failed to fetch current user:', error);
        setCurrentUser(null);
      } finally {
        setUserLoading(false);
      }
    };

    fetchCurrentUser();
  }, []);

  // 댓글 목록 불러오기
  useEffect(() => {
    if (!post) return;
    console.log('getComments 호출', post.boardId);
    setCommentLoading(true);
    setCommentError(null);
    getComments(post.boardId)
      .then((comments) => {
        console.log("댓글 데이터 전체:", comments);
        setComments(comments);
      })
      .catch(() => setCommentError("댓글 목록을 불러오지 못했습니다."))
      .finally(() => setCommentLoading(false));
  }, [post]);

  const isAuthor = !!(currentUser && post && currentUser.userId === post.userId);

  const handleCommentSubmit = async () => {
    if (!commentContent.trim()) return;
    if (!currentUser || !post) {
      setCommentError("로그인 정보가 없거나 게시글 정보가 없습니다.");
      return;
    }
    setCommentSubmitLoading(true);
    setCommentError(null);
    try {
      await createComment({
        boardId: post.boardId,
        content: commentContent,
      });
      setCommentContent("");
      // 댓글 목록 새로고침
      const newComments = await getComments(post.boardId);
      setComments(newComments);
    } catch (e: any) {
      setCommentError(e?.message || "댓글 작성에 실패했습니다.");
    } finally {
      setCommentSubmitLoading(false);
    }
  };

  const handleLikeToggle = async () => {
    if (!currentUser || !post) return;
    try {
      await toggleEmpathy(post.boardId);
      // optimistic UI: 즉시 상태 토글 및 empathyCount 증감
      if (isLiked) {
        setIsLiked(false);
        setEmpathyCount((prev) => prev - 1);
      } else {
        setIsLiked(true);
        setEmpathyCount((prev) => prev + 1);
      }
    } catch (e) {
      // 에러 처리 필요시 추가
    }
  };

  // 게시글 삭제 처리
  const handleDelete = async () => {
    setDeleteError(null);
    if (!currentUser) {
      setDeleteError("로그인 정보가 없습니다. 다시 로그인 해주세요.");
      return;
    }
    if (!post) {
      setDeleteError("게시글 정보가 없습니다.");
      return;
    }
    try {
      // 실제 삭제 API 호출
      await import("@/lib/boardApi").then(({ deleteBoard }) =>
        deleteBoard(post.boardId, currentUser.userId)
      );
      onDelete(post.boardId);
      router.push("/boards"); // 삭제 성공 후 목록 페이지로 이동
    } catch (e: any) {
      setDeleteError(e?.message || "게시글 삭제에 실패했습니다.");
    }
  };



  // 댓글 삭제 처리
  const handleCommentDelete = async (commentId: number) => {
    if (!currentUser) {
      setCommentError("로그인 정보가 없습니다. 다시 로그인 해주세요.");
      return;
    }
    try {
      await deleteComment(commentId);
      setComments(comments.filter((c) => c.id !== commentId));
    } catch (e: any) {
      setCommentError(e?.message || "댓글 삭제에 실패했습니다.");
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 p-4 md:p-8 flex items-center justify-center">
        <p className="text-gray-500">불러오는 중...</p>
      </div>
    );
  }
  if (error || !post) {
    return (
      <div className="min-h-screen bg-gray-50 p-4 md:p-8 flex items-center justify-center">
        <p className="text-red-500">{error || "게시글을 찾을 수 없습니다."}</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-4 md:p-8">
      <div className="max-w-4xl mx-auto space-y-8">
        {/* Back Button */}
        <Button
          variant="ghost"
          onClick={onBack}
          className="flex items-center gap-2 text-gray-600 hover:text-gray-900"
        >
          <ArrowLeft className="h-5 w-5" />
          목록으로 돌아가기
        </Button>

        {/* Post Detail Card */}
        <Card className="shadow-lg">
          <CardHeader>
            <div className="flex items-center justify-between mb-2">
              <CardTitle className="text-2xl font-bold">{post.title}</CardTitle>
              {isAuthor && post && (
                <div className="flex gap-2">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => onEdit(post.boardId)}
                  >
                    <Edit className="h-4 w-4 mr-1" />
                    수정
                  </Button>
                  <Button
                    size="sm"
                    variant="destructive"
                    onClick={handleDelete}
                  >
                    <Trash2 className="h-4 w-4 mr-1" />
                    삭제
                  </Button>
                </div>
              )}
            </div>
            <div className="flex items-center gap-2 text-sm text-gray-500">
              <Badge variant="secondary">
                <MapPin className="h-3 w-3 mr-1" />
                {getAutonomousDistrictName(post.autonomousDistrict)} {getAdministrativeDistrictName(post.administrativeDistrict)}
              </Badge>
              <span>{post.nickname}</span>
              <span>•</span>
              <span>{post.createdDate}</span>
            </div>
          </CardHeader>
          <CardContent className="space-y-6">
            <p className="text-gray-800 leading-relaxed">{post.content}</p>

            <div className="flex items-center gap-6 text-gray-500">
              <Button
                variant="ghost"
                size="sm"
                onClick={handleLikeToggle}
                className={cn("flex items-center gap-1", isLiked ? "text-red-500" : "")}
              >
                <Heart
                  className={cn(
                    "h-5 w-5",
                    isLiked ? "fill-red-500 text-red-500" : ""
                  )}
                />
                {empathyCount}
              </Button>
              {/* 댓글 수는 별도 API 필요, 일단 미표시 */}
              <span className="flex items-center gap-1">
                <MessageSquare className="h-5 w-5" />
              </span>
              <span className="flex items-center gap-1">
                <Eye className="h-5 w-5" />
                {post.viewCount}
              </span>
            </div>

            <div className="border-t pt-6 space-y-4">
              <h3 className="text-xl font-semibold">
                댓글 ({comments.length})
              </h3>
              {commentLoading ? (
                <div className="text-gray-500">댓글 불러오는 중...</div>
              ) : commentError ? (
                <div className="text-red-500">{commentError}</div>
              ) : (
                <div className="space-y-4">
                  {comments.map((comment: CommentType) => (
                    <div key={comment.id} className="flex items-start gap-3">
                      <Avatar className="h-8 w-8">
                        <AvatarImage src="/placeholder-user.jpg" />
                        <AvatarFallback>
                          {comment.nickname?.charAt(0) || "?"}
                        </AvatarFallback>
                      </Avatar>
                      <div className="flex-1 bg-gray-100 p-3 rounded-lg">
                        <div className="flex items-center justify-between mb-1">
                          <span className="font-semibold text-sm">
                            {comment.nickname}
                          </span>
                          <span className="text-xs text-gray-500">
                            {comment.created_date}
                          </span>
                        </div>
                        <div className="flex justify-between items-end">
                          <p className="text-sm text-gray-700 flex-1">{comment.content}</p>
                          {/* 본인 댓글만 삭제 버튼 노출 */}
                          {currentUser && comment.user_id && Number(comment.user_id) === Number(currentUser.userId) && (
                            <Button
                              size="sm"
                              variant="destructive"
                              onClick={() => handleCommentDelete(comment.id)}
                            >
                              삭제
                            </Button>
                          )}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}

              {/* Comment Input Section */}
              <div className="pt-4">
                <h4 className="text-lg font-semibold mb-2">댓글 작성</h4>
                <div className="flex flex-col gap-3">
                  <Textarea
                    placeholder="댓글을 입력하세요..."
                    value={commentContent}
                    onChange={(e) => setCommentContent(e.target.value)}
                    rows={3}
                  />
                  <Button
                    onClick={handleCommentSubmit}
                    className="self-end flex items-center gap-2"
                    disabled={commentSubmitLoading}
                  >
                    <Send className="h-4 w-4" />
                    {commentSubmitLoading ? "작성 중..." : "댓글 작성"}
                  </Button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
        {deleteError && (
          <div className="text-red-500 text-sm text-center mt-2">{deleteError}</div>
        )}
      </div>
    </div>
  );
}
