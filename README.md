# NoiSense

> ### 개요
> React + Spring Boot 기반 웹 애플리케이션
>
> ### Backend
>  - Spring Boot + Prettier 환경
> ### Frontend
> - React + TypeScript + Vite + ESLint + Prettier 환경
---

## 프로젝트 구조
```
project-root/
├── backend/            # Spring Boot 프로젝트
│   ├──.gitignore
│   ├── build.gradle
│   └── src/main/resources/static/ ← 프론트(Vite 빌드 결과물) 복사됨
│   └── ...
├── frontend/                    # React + npm or pnpm
│   ├──.gitignore
│   ├── package.json
│   ├── tsconfig.json         # 기본 config (extends만 함)
│   ├── tsconfig.app.json     # React/Vite 앱용
│   ├── tsconfig.node.json    # Vite 설정 및 Node 스크립트용
│   ├── vite.config.ts
│   ├── tsconfig.json
│   ├── index.html
│   ├── src/
│   └── dist/ ← 빌드 결과물 (vite)
│   └── ...
└── README.md
```


---

## 🚀 빠른 시작

### 1. 환경 요구사항
- Java 17 이상
- Node.js 18 이상
- npm 또는 pnpm
- Gradle 7 이상

### 2. 프로젝트 클론
```bash
git clone https://github.com/NoiSense-Noise-X-Sense/noiSense.git noisense
cd noisense
```


#### ⚙️ 개발 서버 실행 방법

✅ 백엔드(Spring Boot)
```bash
cd backend
./gradlew bootRun
```
✅ 프론트엔드(React)
```bash
cd frontend
npm install        # 또는 pnpm install
npm start
```
> 프론트 개발 중엔 API 요청은 프록시로 /api → localhost:8080으로 연결됨


####  배포 빌드 방법 (React → Spring Boot 포함)
```bash
cd backend
./gradlew bootJar
자동으로 frontend 프로젝트를 빌드하고,
결과물은 backend/src/main/resources/static/에 복사됨
생성된 JAR 파일 하나로 프론트 + API 모두 제공됨
```



🌐 배포 후 접근
```plaintext
http://localhost:8080/
프론트 페이지: /

API 경로: /api/...
```



📌 환경변수 설정

🔧 frontend/.env
```env
REACT_APP_API_URL=/api
```

🔧 backend/src/main/resources/application.yml
```yaml
server:
  port: 8080
```




🧪 테스트 (선택)

백엔드 테스트
```bash
cd backend
./gradlew test
```




🧑‍💻 팀 규칙
```
역할	담당자
프론트 구조 및 디자인	@
백엔드 API 및 DB	@
배포 빌드 자동화	@mid
문서 정리 및 README 관리	@
```




📚 기타 정보
```
코드 스타일은 Prettier / ESLint 기준

커밋 메시지는 [FE], [BE], [Docs] 등 접두어 사용

Git은 main 브랜치 보호 + PR 기반 머지 방식 사용
```


- Lint&Format
```
🔧 IntelliJ에서 해야 할 설정 (모두 루트 기준)
Editor → Code Style → Enable EditorConfig ✅

Editor → Code Style → Java/JS/HTML → 모두 "From .editorconfig" ✅

Languages & Frameworks → Prettier → root 경로로 설정 ✅
→ project-root/node_modules/prettier 경로로 설정되도록 npm install -D prettier 한 번만 해줘
```


- 옵션: vite.config.ts에서 타입 적용
```ts
/// <reference types="vitest" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom'
  }
})
```
> 위와 같이 타입 인식이 필요한 경우, tsconfig.node.json이 잘 작동함


