package com.isamm.libraryManagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Dossier "uploads" à la racine du projet (là où il y a ton pom.xml)
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
        String uploadPath = uploadDir.toUri().toString(); // ex: file:/C:/Users/.../uploads/

        // Toute URL commençant par /uploads/** sera cherchée dans ce dossier physique
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}