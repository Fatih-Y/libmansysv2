# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim AS builder
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the final Docker image
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp

# Copy the JAR file from the build stage
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
