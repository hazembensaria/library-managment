# 1️ Image de base 
FROM eclipse-temurin:17-jre

# Workdir
WORKDIR /app

# Copy the wait script and make it executable
COPY wait-for-db.sh /app/wait-for-db.sh
RUN chmod +x /app/wait-for-db.sh

# Copy application JAR
COPY target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Start by waiting for the DB, then run the app
ENTRYPOINT ["/app/wait-for-db.sh", "db", "3306", "java", "-jar", "/app/app.jar"]
