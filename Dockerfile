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

# Build-time environment variable for data directory (Render's Persistent Disk path)
ENV APP_UPLOAD_DIR=/data/uploads
RUN mkdir -p /data/uploads

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
