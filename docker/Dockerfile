# Multi-stage build for Spring Boot application
FROM eclipse-temurin:17-jdk-jammy as builder

# Build args (optional - can be used during build if needed)
ARG JWT_SECRET
ARG AWS_ACCESS_KEY
ARG AWS_SECRET_KEY
ARG SLACK_BOT_TOKEN

WORKDIR /app

# Gradle wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사
COPY src src

    # 빌드 실행
    # Windows CRLF 문제 해결: sed로 CRLF를 LF로 변환 후 실행
    RUN chmod +x ./gradlew && \
        sed -i 's/\r$//' ./gradlew && \
        ./gradlew clean build -x test --no-daemon

# 실행 이미지
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 헬스체크용 curl 설치
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

