// src/components/PrivateRoute.tsx
import { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';

interface Props {
  children: ReactNode;
}

function isAuthenticated(): boolean {
  // 여기에 로그인 여부 판별 로직 (예: localStorage 토큰 등)
  return !!localStorage.getItem('token');
}

// export default function PrivateRoute({ children }: Props) {
//   if (!isAuthenticated()) {
//     return <Navigate to="/login" replace />;
//   }
//   return <>{children}</>;
// }