// components/LoginPage.tsx
"use client";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Chrome, MessageCircle } from "lucide-react";
import { useEffect } from "react";

export default function LoginPage({ onLogin }: { onLogin: () => void }) {
  // OAuth 콜백에서 토큰이 localStorage에 저장된 경우 로그인 처리
  useEffect(() => {
    // 콜백에서 accessToken을 세팅해두었으면
    if (localStorage.getItem("accessToken")) {
      onLogin();
    }
  }, [onLogin]);

  const OAUTH_URLS: Record<string, string> = {
    Kakao: "http://localhost:8080/oauth2/authorization/kakao",
    Google: "http://localhost:8080/oauth2/authorization/google",
  };


  const handleSocialLogin = (provider: "Kakao" | "Google") => {
    window.location.href = OAUTH_URLS[provider];
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-100 p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-3xl font-bold">NoiSense 로그인</CardTitle>
          <CardDescription className="text-gray-600">소음 정보 대시보드에 오신 것을 환영합니다.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="space-y-4">
            <Button
              variant="outline"
              className="w-full py-2 text-lg flex items-center justify-center gap-3 border-yellow-500 text-yellow-700 hover:bg-yellow-50 bg-transparent"
              onClick={() => handleSocialLogin("Kakao")}
            >
              <MessageCircle className="h-6 w-6" />
              카카오로 로그인
            </Button>
            <Button
              variant="outline"
              className="w-full py-2 text-lg flex items-center justify-center gap-3 border-blue-500 text-blue-700 hover:bg-blue-50 bg-transparent"
              onClick={() => handleSocialLogin("Google")}
            >
              <Chrome className="h-6 w-6" />
              Google로 로그인
            </Button>
          </div>

        </CardContent>
      </Card>
    </div>
  );
}
