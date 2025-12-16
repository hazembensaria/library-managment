package com.monapp.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.isamm.libraryManagement.entity.Ressource;

@ControllerAdvice
public class UploadExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSize(MaxUploadSizeExceededException ex, Model model) {
        model.addAttribute("uploadError", "Fichier trop volumineux.");
        model.addAttribute("ressource", new Ressource()); // optionnel: voir remarque ci-dessous
        return "ressource-form";
    }

    @ExceptionHandler(MultipartException.class)
    public String handleMultipart(MultipartException ex, Model model) {
        model.addAttribute("uploadError", "Erreur lors de l'envoi du fichier.");
        model.addAttribute("ressource", new Ressource());
        return "ressource-form";
    }
}