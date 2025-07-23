"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { createBoard } from "@/lib/boardApi";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";

// accessToken에서 user 정보 추출 함수
function getUserFromToken() {
  if (typeof window === "undefined") return null;
  const token = localStorage.getItem("accessToken");
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return {
      userId: payload.id,
      nickname: payload.nickname,
    };
  } catch {
    return null;
  }
}

export default function WritePost() {
  const router = useRouter();
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [autonomousDistrict, setAutonomousDistrict] = useState("");
  const [administrativeDistrict, setAdministrativeDistrict] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    const user = getUserFromToken();
    if (!user) {
      setError("로그인 정보가 없습니다. 다시 로그인 해주세요.");
      return;
    }
    if (!title.trim() || !content.trim()) {
      setError("제목과 내용을 모두 입력해주세요.");
      return;
    }
    if (!autonomousDistrict || !administrativeDistrict) {
      setError("지역을 선택해주세요.");
      return;
    }
    setLoading(true);
    try {
      await createBoard({
        userId: user.userId,
        nickname: user.nickname,
        title,
        content,
        autonomousDistrict,
        administrativeDistrict,
        emotionalScore: 0
      });
      router.push("/board");
    } catch (e) {
      setError("게시글 작성에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">게시글 작성</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          placeholder="제목을 입력하세요"
          value={title}
          onChange={e => setTitle(e.target.value)}
        />
        <Textarea
          placeholder="내용을 입력하세요"
          value={content}
          onChange={e => setContent(e.target.value)}
          rows={8}
        />
        <div className="flex gap-2">
          <Input
            placeholder="자치구"
            value={autonomousDistrict}
            onChange={e => setAutonomousDistrict(e.target.value)}
          />
          <Input
            placeholder="행정동"
            value={administrativeDistrict}
            onChange={e => setAdministrativeDistrict(e.target.value)}
          />
        </div>
        {error && <div className="text-red-500 text-sm">{error}</div>}
        <Button type="submit" disabled={loading} className="w-full">
          {loading ? "작성 중..." : "작성하기"}
        </Button>
      </form>
    </div>
  );
}
