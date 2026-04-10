# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Environment variable for uploads (Ephemeral on Render Free Tier)
ENV APP_UPLOAD_DIR=/tmp/uploads
RUN mkdir -p /tmp/uploads && chmod 777 /tmp/uploads

EXPOSE 10000
ENTRYPOINT ["java", "-Dserver.port=${PORT:10000}", "-jar", "app.jar"]
