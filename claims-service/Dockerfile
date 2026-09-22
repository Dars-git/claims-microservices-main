# Multi-stage build. Build context must be the repository root:
#   docker build -f claims-service/Dockerfile -t claims-service .
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY common common
COPY claims-service claims-service
COPY notification-service/pom.xml notification-service/pom.xml
RUN mvn -q -pl claims-service -am -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/claims-service/target/claim-intake-service-2.0.0.jar app.jar
EXPOSE 8080
USER 10001:10001
ENTRYPOINT ["java", "-jar", "app.jar"]
