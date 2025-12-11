FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew && ./gradlew clean build -x test

FROM eclipse-temurin:17-jre

WORKDIR /app

RUN apt-get update && \
    apt-get install -y pandoc && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# locale 문제 방지
ENV LC_ALL=C.UTF-8
ENV LANG=C.UTF-8

# 빌드 결과 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]