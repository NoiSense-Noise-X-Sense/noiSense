# Elasticsearch + Kibana + Logstash 세팅 가이드 (with 한힘삼 플러그인)

## ✅ 실행 순서

1. **Elasticsearch용 사용자 정의 플러그인(zip) 위치 확인**
  - `Elasticsearch/hanhinsam-0.1.zip` 파일이 존재해야 함

2. **인덱스 자동 생성 설정 파일 위치 확인**
  - `Elasticsearch/board-index.txt` 파일이 존재해야 함

3. **네트워크 생성**
```bash
docker network create prod_server
```

컨테이너 실행 (처음 한 번은 --build 필수)

```bash
cd monitoring
docker-compose -f docker-compose.monitoring.yml up --build
```


❗ 중요 사항
컨테이너 내부에 플러그인을 설치하기 위해 Dockerfile.elasticsearch를 사용합니다.

board-index.txt는 애플리케이션 시작 시 자동으로 Elasticsearch 인덱스를 생성합니다.

기존 Kibana/Elasticsearch 데이터가 꼬였을 경우:

```bash
# 컨테이너 종료
docker-compose -f docker-compose.monitoring.yml down

# 기존 볼륨 데이터 삭제
rm -rf ./volumes/esdata/*
rm -rf ./volumes/kibana-data/*
```
🧪 테스트 인덱스 확인 (선택)
```bash
# 인덱스 목록 확인
curl -X GET "http://localhost:9200/_cat/indices?v"

# 매핑 확인
curl -X GET "http://localhost:9200/board-index/_mapping?pretty"
```
✨ 담당자
작성자: 이선민

Elasticsearch 플러그인: 한힘삼 분석기 적용

