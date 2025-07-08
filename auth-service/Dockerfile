# Dockerfile optimizado
FROM openjdk:17-jdk-slim

WORKDIR /app
ARG JAR_FILE=build/libs/auth-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","app.jar"]
