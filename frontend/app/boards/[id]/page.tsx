"use client";
import PostDetail from "@/components/PostDetail";
import { useRouter, useParams } from "next/navigation";

export default function BoardDetailPage() {
  const router = useRouter();
  const params = useParams();
  const postId = Number(params.id);

  return (
    <PostDetail
      postId={postId}
      onBack={() => router.push("/boards")}
      onEdit={(id) => router.push(`/boards/edit/${id}`)}
      onDelete={() => router.push("/boards")}
    />
  );
} 