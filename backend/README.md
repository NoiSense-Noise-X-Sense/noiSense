# 빌드 가이드

## 최종 JAR 내부
```
backend-x.x.x-SNAPSHOT.jar
├── BOOT-INF/
│   ├── classes/              ← build/classes/java/main/ 의 내용
│   │   └── com/example/...
│   ├── lib/                  ← 의존성 JAR들
│   └── resources/            ← src/main/resources/ 의 내용
│       ├── application.yml
│       └── static/           ← 프론트 dist 결과물이 들어감
├── META-INF/
│   └── MANIFEST.MF           ← 메인 클래스 정의
└── org/springframework/boot/loader/...
```

- 패키징 제외할 경로 또는 파일 지정 예시
```groovy
bootJar {
    exclude("**/nodejs/**")
    exclude("**/*.map") // 소스맵 파일 제거 등
}
```

- 포함되었는지 직접 확인 방법
```bash
jar tf build/libs/backend-0.0.1-SNAPSHOT.jar
```

- static swagger api
```plain
http://localhost:8080/swagger-ui/index.html
```
