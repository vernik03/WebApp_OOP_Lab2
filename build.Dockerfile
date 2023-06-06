# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /application

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# getting rid of Windows endings
RUN apk update && apk add dos2unix
RUN dos2unix mvnw

RUN ./mvnw clean install

RUN cp ./target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]