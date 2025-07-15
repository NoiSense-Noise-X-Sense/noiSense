// src/routes.tsx
import { ReactElement } from 'react';
import Login from './pages/Login';
import Home from './pages/Home';

interface AppRoute {
  path: string;
  element: ReactElement;
}

const routes: AppRoute[] = [
  {
    path: '/home',
    element:  <Home />
  },
  // 루트 접근시 자동 이동
  { path: '/', element: <Login /> },
  { path: '/login', element: <Login /> },
];

export default routes;