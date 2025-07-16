import type { Metadata } from "next";
import "./globals.css";

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
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
