import dotenv from 'dotenv'
import { fileURLToPath } from 'url'
import { dirname, resolve } from 'path'

const envPath =
  process.env.NODE_ENV === 'production'
    ? '.env.production'
    : '.env.development'

dotenv.config({ path: resolve(dirname(fileURLToPath(import.meta.url)), envPath) })
console.log('✅ [next.config] API URL:', process.env.NEXT_PUBLIC_API_URL)


/** @type {import('next').NextConfig} */
const nextConfig = {
  eslint: {
    ignoreDuringBuilds: true,
  },
  typescript: {
    ignoreBuildErrors: true,
  },
  images: {
    unoptimized: true,
  },

  
  // 환경변수 수동 주입
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL,
    NEXT_PUBLIC_PROFILE: process.env.NEXT_PUBLIC_PROFILE,
  },
}

export default nextConfig