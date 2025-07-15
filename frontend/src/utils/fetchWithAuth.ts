// src/utils/fetchWithAuth.ts
export async function fetchWithAuth(input: RequestInfo, init: RequestInit = {}) {
  let accessToken = localStorage.getItem("accessToken");
  let refreshToken = localStorage.getItem("refreshToken");

  // 헤더 객체 준비
  let headers: Record<string, string> = {
    ...(init.headers ? (init.headers as Record<string, string>) : {}),
    'Content-Type': 'application/json',
  };

  // accessToken이 있으면 Authorization 추가
  if (accessToken) {
    headers["Authorization"] = "Bearer " + accessToken;
  }

  // credentials: include 보장
  const options: RequestInit = {
    ...init,
    headers,
    credentials: "include",
  };

  // 실제 요청
  let res = await fetch(input, options);

  // ---- JWT 토큰 디코드 및 만료 로그 ----
  function decode(token?: string) {
    try {
      if (!token) return null;
      const base64Url = token.split('.')[1];
      if (!base64Url) throw new Error("JWT 형식 아님");

      let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      while (base64.length % 4 !== 0) base64 += "=";

      return JSON.parse(atob(base64));
    } catch (e) {
      console.error("디코딩 실패:", (e as Error).message, "| token =", token);
      return null;
    }
  }

  if (accessToken) {
    const payload = decode(accessToken);
    if (payload) {
      console.log(
        "exp:", payload.exp,
        "now:", Math.floor(Date.now() / 1000),
        "남은초:", payload.exp - Math.floor(Date.now() / 1000)
      );
    }
  } else {
    console.log("accessToken 없음 (로그인 안됨 or 만료)");
  }

  console.log("accessToken payload:", decode(accessToken || undefined));
  console.log("refreshToken payload:", decode(refreshToken || undefined));
  console.log("accessToken:", accessToken);
  console.log("refreshToken:", refreshToken);

  // ---- accessToken 만료 (401) 시 refreshToken으로 재발급 시도 ----
  if (res.status === 401 && refreshToken) {
    const refreshRes = await fetch("/api/auth/refresh", {
      method: "POST",
      credentials: "include",
      headers: {
        "Authorization": "Bearer " + refreshToken,
        "Content-Type": "application/json",
      },
    });

    if (refreshRes.ok) {
      const data = await refreshRes.json();
      const newAccessToken = data.accessToken;
      const newRefreshToken = data.refreshToken;

      if (newAccessToken) {
        localStorage.setItem("accessToken", newAccessToken);
        if (newRefreshToken) {
          localStorage.setItem("refreshToken", newRefreshToken);
        }
        console.log("새로 발급 받은 엑세스 토큰 = ", newAccessToken);
        console.log("스토리지에 저장한 토큰 ", decode(newAccessToken));

        // Authorization 헤더 갱신
        options.headers = {
          ...headers,
          "Authorization": "Bearer " + newAccessToken,
        };

        // 재요청
        return fetch(input, options);
      }
    } else {
      location.href = "index.html";
      throw new Error("로그인 만료");
    }
  }

  return res;
}