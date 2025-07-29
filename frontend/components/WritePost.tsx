"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { createBoard, updateBoard, getBoardById, BoardPost } from "@/lib/boardApi";
import { getCurrentUser } from "@/lib/auth";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import {
  getAutonomousDistrictName,
  getAdministrativeDistrictName,
  getAutonomousDistrictCode,
  getAdministrativeDistrictCode,
  getAutonomousDistrictNames,
  getAdministrativeDistrictNames,
  initializeDistrictMappings,
  getDongsByGu
} from "@/lib/api/district";



export default function WritePost({
                                    onBack,
                                    onSubmit,
                                    boardId,
                                  }: {
  onBack: () => void;
  onSubmit: () => void;
  boardId?: number;
}) {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [autonomousDistrict, setAutonomousDistrict] = useState("");
  const [administrativeDistrict, setAdministrativeDistrict] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [post, setPost] = useState<BoardPost | null>(null);
  const [initialLoading, setInitialLoading] = useState(false);
  const [autonomousDistrictOptions, setAutonomousDistrictOptions] = useState<string[]>([]);
  const [administrativeDistrictOptions, setAdministrativeDistrictOptions] = useState<string[]>([]);

  // Determine if we're in edit mode
  const editBoardId = boardId || (searchParams?.get('boardId') ? Number(searchParams.get('boardId')) : null);
  const isEditMode = !!editBoardId;

  // Initialize district mappings on component mount
  useEffect(() => {
    const initializeDistricts = async () => {
      await initializeDistrictMappings();
      // Set autonomous district options
      const autonomousNames = getAutonomousDistrictNames();
      setAutonomousDistrictOptions(["전체", ...autonomousNames]);
    };
    initializeDistricts();
  }, []);

  // Update administrative district options when autonomous district changes
  useEffect(() => {
    const fetchAdministrativeDistricts = async () => {
      if (autonomousDistrict && autonomousDistrict !== "전체") {
        const autonomousCode = getAutonomousDistrictCode(autonomousDistrict);
        try {
          // Call the API endpoint /api/v1/district/gu/{guCode}/dongs
          const dongs = await getDongsByGu(autonomousCode);
          const dongNames = dongs.map(dong => dong.districtNameKo);
          setAdministrativeDistrictOptions(["전체", ...dongNames]);
        } catch (error) {
          console.error('Failed to fetch dongs for gu:', autonomousCode, error);
          // Fallback to empty options on error
          setAdministrativeDistrictOptions(["전체"]);
        }
      } else {
        setAdministrativeDistrictOptions(["전체"]);
      }
    };

    fetchAdministrativeDistricts();
  }, [autonomousDistrict]);

  // Fetch existing post data when in edit mode
  useEffect(() => {
    if (isEditMode && editBoardId) {
      setInitialLoading(true);
      setError(null);
      getBoardById(editBoardId)
        .then((data) => {
          setPost(data);
          setTitle(data.title);
          setContent(data.content);
          // Convert codes to names for display
          setAutonomousDistrict(getAutonomousDistrictName(data.autonomousDistrict));
          setAdministrativeDistrict(getAdministrativeDistrictName(data.administrativeDistrict));
        })
        .catch(() => setError("게시글을 불러오지 못했습니다."))
        .finally(() => setInitialLoading(false));
    }
  }, [isEditMode, editBoardId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    const user = await getCurrentUser();
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

    // 지역구 코드 매핑
    const autonomousDistrictCode = getAutonomousDistrictCode(autonomousDistrict);
    const administrativeDistrictCode = getAdministrativeDistrictCode(administrativeDistrict);

    if (!autonomousDistrictCode || !administrativeDistrictCode) {
      setError("지역 코드를 찾을 수 없습니다.");
      return;
    }

    setLoading(true);
    try {
      if (isEditMode && editBoardId && post) {
        // Update existing post
        await updateBoard(editBoardId, user.userId, {
          title,
          content,
          autonomousDistrict: autonomousDistrictCode,
          administrativeDistrict: administrativeDistrictCode,
          viewCount: post.viewCount,
          empathyCount: post.empathyCount,
        });
      } else {
        // Create new post
        await createBoard({
          userId: user.userId,
          nickname: user.nickname,
          title,
          content,
          autonomousDistrict: autonomousDistrictCode,
          administrativeDistrict: administrativeDistrictCode,
          emotionalScore: 0
        });
      }
      onSubmit();
    } catch (e) {
      setError(isEditMode ? "게시글 수정에 실패했습니다." : "게시글 작성에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  // Show loading state when fetching post data for edit
  if (initialLoading) {
    return (
      <div className="max-w-2xl mx-auto p-4">
        <div className="flex items-center justify-center py-8">
          <div className="text-gray-500">게시글 정보를 불러오는 중...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">
        {isEditMode ? "게시글 수정" : "게시글 작성"}
      </h1>
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
          <Select
            value={autonomousDistrict || "전체"}
            onValueChange={value => {
              setAutonomousDistrict(value);
              setAdministrativeDistrict("전체");
            }}
          >
            <SelectTrigger className="w-full">
              <SelectValue placeholder="자치구 선택" />
            </SelectTrigger>
            <SelectContent>
              {autonomousDistrictOptions.map((district) => (
                <SelectItem key={district} value={district}>
                  {district}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <Select
            value={administrativeDistrict || "전체"}
            onValueChange={setAdministrativeDistrict}
            disabled={autonomousDistrict === "전체" || administrativeDistrictOptions.length <= 1}
          >
            <SelectTrigger className="w-full">
              <SelectValue placeholder="행정동 선택" />
            </SelectTrigger>
            <SelectContent>
              {administrativeDistrictOptions.map((dong) => (
                <SelectItem key={dong} value={dong}>
                  {dong}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        {error && <div className="text-red-500 text-sm">{error}</div>}
        <Button type="submit" disabled={loading} className="w-full">
          {loading
            ? (isEditMode ? "수정 중..." : "작성 중...")
            : (isEditMode ? "수정하기" : "작성하기")
          }
        </Button>
      </form>
    </div>
  );
}
