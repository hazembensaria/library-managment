# Base image
FROM eclipse-temurin:17-jre

# Working directory
WORKDIR /app

# Copy Spring Boot JAR
COPY target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run Spring Boot with CI profile
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
