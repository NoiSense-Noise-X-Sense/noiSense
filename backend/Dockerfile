# 가볍고 안정적인 OpenJDK 17 이미지 사용
FROM openjdk:17-slim

# 빌드된 JAR 파일을 컨테이너에 복사
COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너가 시작되면 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
