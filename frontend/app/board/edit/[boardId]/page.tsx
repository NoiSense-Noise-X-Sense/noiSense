"use client";

import { useRouter, useParams } from "next/navigation";
import WritePost from "@/components/WritePost";

export default function EditBoardPage() {
  const router = useRouter();
  const params = useParams();
  const boardId = params.boardId as string;

  const handleBack = () => {
    router.back();
  };

  const handleSubmit = () => {
    // After successful edit, navigate back to the previous page or board list
    router.back();
  };

  return (
    <WritePost
      onBack={handleBack}
      onSubmit={handleSubmit}
    />
  );
}
