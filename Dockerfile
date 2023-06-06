# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jre-alpine

COPY ./target/*.jar /application/app.jar

WORKDIR /application

CMD ["java", "-jar", "app.jar"]