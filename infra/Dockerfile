# 1. Build Stage
FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY . .
RUN #gradle build --no-daemon #테스트코드 포함 빌드
RUN gradle build -x test --no-daemon # 테스트코드 건너뛰기
# 2. Run Stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/hansabal-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]