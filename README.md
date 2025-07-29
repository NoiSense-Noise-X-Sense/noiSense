# NoiSense

> ### 개요
> React + Spring Boot 기반 웹 애플리케이션
>
> ### Backend
>  - Spring Boot + Prettier 환경
> ### Frontend
> - React + TypeScript + Next.js + ESLint + Prettier 환경
---

## 프로젝트 구조
```
project-root/
├── Data/                # DB (local-자동실행 docker-compose, 개발-AWS RDS에 입력하는 초기데이터,자동실행x)
├── Elasticsearch/       # Elasticsearch Dockerfile (monitoring 쪽에서 실행함)
├── infra/               # infra - AWS EC2 서버에서 실행 (Nginx, Redis) (서버에서 배치는 실행하지 않음_80만건..)
├── monitoring/          # monitoring - AWS EC2 서버에서 실행(elasticsearch, kibana, logstash)
├── backend/             # Spring Boot 프로젝트
│   ├──.gitignore
│   ├── build.gradle
│   └── ...
├── frontend/            # React + npm or pnpm
│   ├──.gitignore
│   ├── package.json
│   └── ...
└── README.md
```

-------------------------------------------------------
-------------------------------------------------------

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
![bootRun.png](bootRun.png)
noisense> Tasks> application> bootRun

```
✅ 프론트엔드(React)
```bash
cd frontend
npm install        # 또는 pnpm install
npm run dev        
```
> front 3000, back 8080



### 환경변수 설정

- frontend/.env.development
- frontend/.env.production (Git Action 배포시 자동생성)


- backend/src/main/resources/application.yml
```yaml
server:
  port: 8080
```




### 테스트 (선택)

백엔드 테스트
```bash
cd backend
./gradlew test
```



-------------------------------------------------------
-------------------------------------------------------

# 🧩 NoiSense - Spring Batch 실행 가이드 (운영 배포용)

본 문서는 NoiSense 프로젝트에서 시간 기반 배치 작업(Spring Batch)을 운영 환경에 배포하고 실행하는 방법을 안내합니다.

> 🎯 Job 이름: `hourlyNoiseJob`  
> 📌 목적: 센서 API 수집(오전 9시 30분, 오후 3시 30분) 및 하루 1회 대시보드 통계 생성

---

## 📁 디렉토리 구조
```
noisense/
├── backend/              # Spring Boot Backend
│      └── Dockerfile     # 배치용 Dockerfile
├── frontend/             # Next.js Frontend
└── infra/
       └── k8s/
            └── batch-cronjob.yaml  # Kubernetes 배치 CronJob 정의
```

---

## 🚀 운영 실행 절차

### 1️⃣ 백엔드 JAR 빌드

```bash
cd backend
./gradlew clean bootJar
```

---

### 2️⃣ Docker 이미지 빌드 및 Push

```bash
# 이미지 빌드
docker build --platform=linux/amd64 \
  -t registry.dosion.com/noisense/backend-batch:1.0.0 .

# 이미지 Push
docker push registry.dosion.com/noisense/backend-batch:1.0.0
```

---

### 3️⃣ Kubernetes CronJob 배포

```bash
cd ../infra/k8s
kubectl apply -f batch-cronjob.yaml
```

---

### 4️⃣ CronJob 상태 확인 및 활성화

```bash
kubectl get cronjobs
kubectl get jobs
kubectl logs job/<JOB_NAME>
```

> CronJob이 정지 상태(SUSPEND: True)일 경우:

```bash
kubectl patch cronjob noisense-batch-job -p '{"spec": {"suspend": false}}'
```

---

## 🔁 배치 Job 구조

```
[hourlyNoiseJob]
  └── Step 1: apiStep (센서 API 수집)
         ↓
      Decider: dashboardTriggerDecider (하루 1회 실행 여부 판단)
      ├─ EXECUTE_DASHBOARD → Step 2: statStep (대시보드 통계 생성)
      └─ SKIP_DASHBOARD    → 종료
```
- `ApiDataFetchTasklet`: 센서 API 호출 및 저장
- `DashboardStatBuildTasklet`: 하루 1회 통계 생성

---

## ✅ 참고사항

- `batch-cronjob.yaml`의 `schedule` 필드로 실행 주기 설정 가능
- 모든 배포는 `이미지 빌드 → 레지스트리 Push → K8s 배포` 순으로 진행

