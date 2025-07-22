// app/layout.tsx

import type { Metadata } from "next";
import localFont from 'next/font/local';
import "./globals.css";

// DSEG7 폰트를 불러옵니다.
const dsegFont = localFont({
  src: './fonts/DSEG7Classic-Bold.woff2', // 폰트 파일의 상대 경로
  display: 'swap',
  variable: '--font-dseg', // CSS 변수로 사용하기 위한 이름
});

export const metadata: Metadata = {
  title: "NoiSense - 서울 공공데이터를 활용한 소음정보 제공 서비스",
  description: "Created with v0",
  generator: "v0.dev",
};

export default function RootLayout({
                                     children,
                                   }: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    // 👇 [수정] <html>과 <body> 태그를 수정합니다.
    <html lang="ko">
    <body className={dsegFont.variable}>
    {children}
    </body>
    </html>
  );
}
