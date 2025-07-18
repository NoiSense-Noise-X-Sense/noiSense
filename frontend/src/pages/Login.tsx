// Login.tsx (핵심 부분만)

function Login() {

  return (
    <div>
      <h1>로그인 페이지</h1>
        <button onClick={() => window.location.href = '/oauth2/authorization/google'}>Google 로그인
        </button>
        <br></br>
        <button onClick={() => window.location.href = '/oauth2/authorization/kakao'}>Kakao 로그인
        </button>
    </div>
  );
}

export default Login;