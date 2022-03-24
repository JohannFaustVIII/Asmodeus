FROM gradle:jdk8 AS builder
WORKDIR /usr/app/
COPY build.gradle settings.gradle ./

COPY src src
RUN gradle clean build

FROM openjdk:8-jre

WORKDIR /usr/app/
ENTRYPOINT ["java", "-jar", "app.jar"]
COPY --from=builder /usr/app/build/libs/Asmodeus-*.jar app.jar
