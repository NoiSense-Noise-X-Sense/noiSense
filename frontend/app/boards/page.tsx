"use client";
import NoiseBoard from "@/components/NoiseBoard";
import { useRouter } from "next/navigation";

export default function BoardsPage() {
  const router = useRouter();
  return (
    <NoiseBoard
      onPostClick={(postId) => router.push(`/boards/${postId}`)}
      onWriteClick={() => router.push("/boards/write")}
    />
  );
} 