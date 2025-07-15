import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchWithAuth } from '../utils/fetchWithAuth';

interface User {
  nickname: string;
  id: string;
  name: string;
  email: string;
}

function getCookie(name: string): string | undefined {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop()?.split(';').shift();
}

function deleteCookie(name: string) {
  document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/';
}

function Home() {
  const navigate = useNavigate();
  const [user, setUser] = useState<User | null>(null);

  // 유저 정보 불러오기
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await fetchWithAuth('/api/user', {
          method: 'GET',
          credentials: 'include'
        });
        if (res.ok) {
          const userData = await res.json();
          setUser(userData);
          localStorage.setItem("username", userData.nickname);
          localStorage.setItem("user_id", userData.id);
        } else {
          navigate('/login');
          alert("인증만료");
        }
      } catch (err) {
        navigate('/login');
        alert("err : " + err);
      }
    };

    fetchUser();
  }, [navigate]);

  // 로그아웃 (쿠키/스토리지 모두 삭제)
  const handleLogout = useCallback(async () => {
    try {
 

      const response = await fetchWithAuth('/api/auth/logout', {
        method: 'POST',
        credentials: 'include'
      });

      if (response.ok) {
        navigate('/login');
        deleteCookie("accessToken");
        deleteCookie("refreshToken");
        localStorage.clear();
        sessionStorage.clear();

      } else {
        alert('로그아웃 실패');
      }
    } catch (error) {
      alert('로그아웃 중 에러가 발생했습니다.');
    }
  }, [navigate]);

  return (
    <div>
      <h1>Home 입니다. </h1>
      {user ? `${user.nickname}님, 로그인에 성공했습니다.` : "유저 정보를 불러오는 중..."}
      <br></br>
      <button id="logout-btn" onClick={handleLogout}>로그아웃</button>
    </div>
  );
}

export default Home;