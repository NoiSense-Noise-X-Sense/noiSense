import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

/**
 * 인증되지 않은 사용자는 /login으로 리다이렉트
 * 인증 판별은 localStorage의 'token' 여부로 결정
 */
export function useAuthGuard() {
  const router = useRouter();

  useEffect(() => {
    if (typeof window !== 'undefined' && !localStorage.getItem('token')) {
      router.replace('/login');
    }
  }, [router]);
}
