const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function fetchWithAuth(input: RequestInfo, init: RequestInit = {}) {
  if (typeof window === "undefined") {
    throw new Error("fetchWithAuth는 브라우저에서만 사용할 수 있습니다.");
  }

  const options: RequestInit = {
    ...init,
    credentials: "include", // 중요: 쿠키 포함해서 요청
    headers: {
      ...(init.headers ? (init.headers as Record<string, string>) : {}),
      "Content-Type": "application/json",
    },
  };

  const url = input.toString().startsWith('/') ? `${API_URL}${input}` : input;
  const res = await fetch(url, options);

  if (res.status === 401) {
    console.warn("인증 실패: 로그인 안 됨 or 세션 만료");
    // window.location.href = "/login";
  }

  return res;
}
