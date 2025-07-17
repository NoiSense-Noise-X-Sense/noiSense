"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { Chrome, MessageCircle } from "lucide-react"

export default function LoginPage({ onLogin }: { onLogin: () => void }) {
  const handleSocialLogin = (provider: string) => {
    console.log(`${provider} 로그인 시도`)
    // 실제 구현에서는 OAuth 로그인 로직
    onLogin() // 로그인 성공 시 메인 페이지로 이동
  }

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

          <Separator className="my-6" />

          <p className="text-center text-sm text-gray-500">
            계정이 없으신가요?{" "}
            <a href="#" className="text-blue-600 hover:underline">
              회원가입
            </a>
          </p>
        </CardContent>
      </Card>
    </div>
  )
}
