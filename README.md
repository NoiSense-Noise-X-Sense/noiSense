# 🎧 NoiSense (Noise × Sense)

> **소음 민원 + 시민 감정 분석 기반 체감 소음 시각화 플랫폼**

생활불편신고, 공공 API 데이터를 기반으로  
지역별·시간대별 소음 민원 발생 밀도와 감정 키워드를 분석하여  
'체감 소음 점수'를 지도 위에 시각화하는 시민 체감형 데이터 서비스입니다.

---

## 📌 프로젝트 개요

- **프로젝트명:** NoiSense (Noise + Sense)
- **목적:**  
  시민에게는 **거주지 선택**에 도움을 주고,  
  정책결정자에게는 **정책 수립의 근거**가 될 수 있는 체감형 소음 데이터 제공

- **핵심 특징:**
  - 소음 민원 + 감정 분석 기반 **체감 소음 점수화**
  - **Elasticsearch**를 활용한 시간·공간 기반 실시간 분석
  - 지도 시각화를 통한 사용자 친화적 정보 제공

---

## 👨‍👩‍👧‍👦 팀원 소개

| 이름 | 역할 |
|------|------|
| 장소희 | 팀장 /  |
| 김종국 | 팀원 /  |
| 김형년 | 팀원 /  |
| 백가희 | 팀원 /  |
| 이선민 | 팀원 /  |
| 조영주 | 팀원 /  |

---

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| Frontend | React, TypeScript, Map API (예: Kakao/Leaflet) |
| Backend | Spring Boot / Node.js (선택), Elasticsearch, Python (감정 분석) |
| Infra | Docker, GitHub, Nginx |
| Data | 서울시 열린데이터광장 API, 공공 민원데이터, 감정 사전 기반 키워드 추출 |

---

## 📊 주요 기능

- 🔍 **소음 민원 밀도 분석**: 지역·시간대별 소음 민원 건수 집계
- 🧠 **감정 키워드 분석**: 민원 텍스트 기반 키워드 추출 및 가중치 부여
- 📈 **체감 소음 점수 산출**: 민원 수치 + 감정 키워드를 통한 점수 계산
- 🗺 **지도 시각화**: geo_point 기반 소음/감정 점수의 지역 시각화

---

## 📂 프로젝트 구조 (예시)

```
NoiSense/
├── frontend/         # 프론트엔드 (React)
├── backend/          # 백엔드 (Spring Boot or Node)
├── data/             # 데이터 수집, 정제 스크립트
├── elasticsearch/    # ES 설정 및 색인 매핑
├── docker/           # Dockerfile 및 docker-compose
└── README.md
```

---

## 🚀 실행 방법

```bash
# 1. 클론
git clone https://github.com/your-org/NoiSense.git
cd NoiSense

# 2. 백엔드 실행
cd backend
./gradlew bootRun

# 3. 프론트엔드 실행
cd frontend
npm install
npm run dev
```

---

## ✅ 진행 현황

- [ ] 요구사항 정리
- [ ] 도메인 분석
- [ ] 업무분담
- [ ] ERD(데이터 모델) 설계
- [ ] API 명세 작성
- [ ] 기술스택 선정
- [ ] 마일스톤/일정계획
- [ ] 테스트케이스 작성
      
- [ ] 데이터 수집 API 조사
- [ ] 감정 키워드 분석 로직 설계
- [ ] Elasticsearch 색인 및 쿼리
- [ ] 프론트엔드 지도 시각화 개발

---

## 📌 참고자료

- 서울시 생활불편신고 데이터: https://data.seoul.go.kr/
- Elasticsearch 공식문서: https://www.elastic.co/guide/
- 감정 키워드 사전 (SentiWordNet, KNU 감성 사전 등)
