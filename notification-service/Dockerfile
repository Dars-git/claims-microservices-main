# Multi-stage build. Build context must be the repository root:
#   docker build -f notification-service/Dockerfile -t notification-service .
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY common common
COPY notification-service notification-service
COPY claims-service/pom.xml claims-service/pom.xml
RUN mvn -q -pl notification-service -am -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/notification-service/target/claim-notification-service-2.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
