"use client";

import { useState } from "react";
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

// Dummy data for a single post and comments
const dummyPost = {
  id: 1,
  title: "강남역 근처 공사 소음이 너무 심해요",
  content:
    "매일 아침 7시부터 공사 소음으로 잠을 못 자겠습니다. 주말에도 계속되어서 너무 힘드네요. 특히 새벽 시간대에도 작업이 이루어져서 생활에 큰 불편을 겪고 있습니다. 구청에 민원을 넣어봤지만 개선되지 않고 있습니다. 다른 분들도 비슷한 경험 있으신가요? 어떻게 해결해야 할지 막막합니다.",
  region: "강남구",
  dong: "역삼동",
  likes: 15,
  comments: 8,
  views: 120,
  createdAt: "2024.07.05",
  author: "김소음",
  isAuthor: true, // Simulate if the current user is the author
};

const dummyComments = [
  {
    id: 1,
    author: "이웃주민",
    content:
      "저도 같은 문제로 고통받고 있습니다. 구청에 집단 민원을 넣어보는 건 어떨까요?",
    createdAt: "2024.07.05 10:30",
  },
  {
    id: 2,
    author: "소음전문가",
    content:
      "공사 현장에 소음 방지벽 설치를 요청하거나, 야간 작업 시간 제한을 요구할 수 있습니다.",
    createdAt: "2024.07.05 11:00",
  },
  {
    id: 3,
    author: "지나가던이",
    content: "힘내세요! 꼭 해결되길 바랍니다.",
    createdAt: "2024.07.05 14:15",
  },
];

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
  // In a real application, you would fetch post details based on postId
  const post = dummyPost; // Using dummy data for now

  const [commentContent, setCommentContent] = useState("");
  const [isLiked, setIsLiked] = useState(false); // State for like button

  const handleCommentSubmit = () => {
    if (commentContent.trim()) {
      console.log("New comment:", commentContent);
      // In a real app, send comment to backend and update comments list
      setCommentContent("");
      alert("댓글이 작성되었습니다!");
    }
  };

  const handleLikeToggle = () => {
    setIsLiked(!isLiked);
    // In a real app, send like/unlike action to backend
  };

  if (!post) {
    return (
      <div className="min-h-screen bg-gray-50 p-4 md:p-8 flex items-center justify-center">
        <p className="text-gray-500">게시글을 찾을 수 없습니다.</p>
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
              {post.isAuthor && ( // Show edit/delete only if current user is the author
                <div className="flex gap-2">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => onEdit(post.id)}
                  >
                    <Edit className="h-4 w-4 mr-1" />
                    수정
                  </Button>
                  <Button
                    size="sm"
                    variant="destructive"
                    onClick={() => onDelete(post.id)}
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
                {post.region} {post.dong}
              </Badge>
              <span>{post.author}</span>
              <span>•</span>
              <span>{post.createdAt}</span>
            </div>
          </CardHeader>
          <CardContent className="space-y-6">
            <p className="text-gray-800 leading-relaxed">{post.content}</p>

            <div className="flex items-center gap-6 text-gray-500">
              <Button
                variant="ghost"
                size="sm"
                onClick={handleLikeToggle}
                className="flex items-center gap-1"
              >
                <Heart
                  className={cn(
                    "h-5 w-5",
                    isLiked ? "fill-red-500 text-red-500" : ""
                  )}
                />
                {post.likes + (isLiked ? 1 : 0)}
              </Button>
              <span className="flex items-center gap-1">
                <MessageSquare className="h-5 w-5" />
                {post.comments}
              </span>
              <span className="flex items-center gap-1">
                <Eye className="h-5 w-5" />
                {post.views}
              </span>
            </div>

            <div className="border-t pt-6 space-y-4">
              <h3 className="text-xl font-semibold">
                댓글 ({dummyComments.length})
              </h3>
              <div className="space-y-4">
                {dummyComments.map((comment) => (
                  <div key={comment.id} className="flex items-start gap-3">
                    <Avatar className="h-8 w-8">
                      <AvatarImage src="/placeholder-user.jpg" />
                      <AvatarFallback>
                        {comment.author.charAt(0)}
                      </AvatarFallback>
                    </Avatar>
                    <div className="flex-1 bg-gray-100 p-3 rounded-lg">
                      <div className="flex items-center justify-between mb-1">
                        <span className="font-semibold text-sm">
                          {comment.author}
                        </span>
                        <span className="text-xs text-gray-500">
                          {comment.createdAt}
                        </span>
                      </div>
                      <p className="text-sm text-gray-700">{comment.content}</p>
                    </div>
                  </div>
                ))}
              </div>

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
                  >
                    <Send className="h-4 w-4" />
                    댓글 작성
                  </Button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
