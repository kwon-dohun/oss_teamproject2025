# 1) 빌드 단계
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY . .

# 실행 권한 부여 및 빌드
RUN chmod +x ./gradlew && ./gradlew clean build -x test

# 2) 실행 단계
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]