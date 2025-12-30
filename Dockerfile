# 1️ Image de base 
FROM eclipse-temurin:17-jre

# 2️ Répertoire de travail dans le conteneur
WORKDIR /app

# 3️ Copier le JAR dans l’image
COPY target/*.jar app.jar

# 4️ le port Spring Boot
EXPOSE 8080

# 5️ Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
