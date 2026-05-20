FROM eclipse-temurin:21-jdk-alpine AS builder

ARG SERVICE

WORKDIR /app

COPY gradlew gradlew
COPY gradle/ gradle/
COPY build.gradle.kts settings.gradle.kts gradle.properties* ./

COPY commons/ commons/
COPY account/ account/
COPY auth/ auth/
COPY catalog/ catalog/
COPY gateway/ gateway/
COPY invoice/ invoice/

RUN chmod +x gradlew

RUN ./gradlew :${SERVICE}:buildFatJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

ARG SERVICE

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/${SERVICE}/build/libs/*-all.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]