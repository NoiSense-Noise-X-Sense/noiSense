const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function fetchWithAuth(input: RequestInfo, init: RequestInit = {}) {
  // 클라이언트 환경에서만 동작하도록 체크 (Next.js SSR 방지)
  if (typeof window === "undefined") {
    throw new Error("fetchWithAuth는 브라우저에서만 사용할 수 있습니다.");
  }

  let accessToken = localStorage.getItem("accessToken");
  let refreshToken = localStorage.getItem("refreshToken");

  // 헤더 설정
  let headers: Record<string, string> = {
    ...(init.headers ? (init.headers as Record<string, string>) : {}),
    "Content-Type": "application/json",
  };

  if (accessToken) {
    headers["Authorization"] = "Bearer " + accessToken;
  }

  const options: RequestInit = {
    ...init,
    headers,
    credentials: "include",
  };

  // If input is a relative URL, prepend API_URL
  const url = input.toString().startsWith('/') ? `${API_URL}${input}` : input;
  let res = await fetch(url, options);

  // --- JWT 디코드, 디버깅 ---
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
      console.log("exp:", payload.exp, "now:", Math.floor(Date.now() / 1000), "남은초:", payload.exp - Math.floor(Date.now() / 1000));
    }
  } else {
    console.log("accessToken 없음 (로그인 안됨 or 만료)");
  }

  // 401: 만료 → refreshToken으로 재발급 시도
  if (res.status === 401 && refreshToken) {
    const refreshRes = await fetch(`${API_URL}/api/auth/refresh`, {
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
        if (newRefreshToken) localStorage.setItem("refreshToken", newRefreshToken);

        // 헤더 갱신
        options.headers = {
          ...headers,
          "Authorization": "Bearer " + newAccessToken,
        };
        // 재요청
        const url = input.toString().startsWith('/') ? `${API_URL}${input}` : input;
        return fetch(url, options);
      }
    } else {
      // Next.js에서는 location.href 보단 useRouter()를 추천하지만,
      // 이 유틸에서는 그대로 두거나, 페이지에서 라우터 처리 권장
      window.location.href = "/login";
      throw new Error("로그인 만료");
    }
  }

  return res;
}
