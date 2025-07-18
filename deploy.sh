#!/bin/bash

# Clean Code: 변수, 함수, 메시지 모두 명확하게

# 종료 신호(Ctrl+C 등) 감지 시 백엔드/프론트 프로세스 모두 종료
function cleanup() {
    echo ""
    echo "종료 신호 감지! 백엔드(Spring Boot), 프론트(Next.js) 서버를 종료합니다..."
    kill -9 $BACK_PID 2>/dev/null
    kill -9 $FRONT_PID 2>/dev/null
    exit 0
}
trap cleanup SIGINT SIGTERM

# 백엔드(Spring Boot) 실행
cd backend
./gradlew bootRun &
BACK_PID=$!
cd ..

# 프론트(Next.js) 실행
cd frontend
npm run dev &
FRONT_PID=$!
cd ..

# 종료 안내
echo "----------------------------------------"
echo "Next.js, Spring Boot 개발 서버가 실행중입니다."
echo "중단하려면 [Ctrl+C]"
echo "----------------------------------------"

# 두 프로세스 모두 종료될 때까지 대기
wait $BACK_PID
wait $FRONT_PID
