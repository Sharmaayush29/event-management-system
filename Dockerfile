# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Environment variable for uploads (Ephemeral on Render Free Tier)
ENV APP_UPLOAD_DIR=/tmp/uploads
RUN mkdir -p /tmp/uploads && chmod 777 /tmp/uploads

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
