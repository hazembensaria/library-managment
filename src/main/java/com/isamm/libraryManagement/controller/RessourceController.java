package com.isamm.libraryManagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import com.isamm.libraryManagement.entity.Ressource;
import com.isamm.libraryManagement.entity.TypeRessource;
import com.isamm.libraryManagement.service.RessourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils; // POUR StringUtils
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.nio.file.Files; // POUR Files.exists, createDirectories, etc.
import java.nio.file.Path; // POUR Path
import java.nio.file.Paths; // POUR Paths.get
import java.util.UUID;

@Controller
@RequestMapping("/ressources")
@RequiredArgsConstructor
public class RessourceController {

    private final RessourceService service;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("ressources", service.getAll());
        return "ressources";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("ressource", new Ressource());
        return "ressource-form";
    }

    private String storeFile(MultipartFile file, String directory) throws IOException {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex != -1) {
            extension = originalFilename.substring(dotIndex);
        }

        String fileName = UUID.randomUUID().toString() + extension;

        Path uploadDir = Paths.get(directory).toAbsolutePath().normalize();
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(fileName).normalize();

        file.transferTo(filePath.toFile());
        return fileName;
    }

    // =================== LIENS SECURISES POUR LES FICHIERS ===================

    @GetMapping("/cover/{id}")
    public ResponseEntity<Resource> getCover(@PathVariable Long id) throws IOException {
        Ressource r = service.getById(id);
        if (r == null || r.getCoverPath() == null) {
            return ResponseEntity.notFound().build();
        }

        return serveFile(r.getCoverPath());
    }

    /**
     * Méthode utilitaire privée qui sert un fichier à partir du chemin stocké en
     * BDD
     */
    private ResponseEntity<Resource> serveFile(String storedPath) throws IOException {
        if (storedPath == null || storedPath.isBlank()) {
            return ResponseEntity.notFound().build();
        }

        // Normaliser le chemin
        storedPath = storedPath.replace("\\", "/");

        // On s'attend à ce que ça commence par "/uploads/..."
        if (!storedPath.startsWith("/uploads/")) {
            return ResponseEntity.notFound().build();
        }

        // Dossier racine des uploads (même base que dans
        // storeFile("uploads/covers"...))
        Path uploadRoot = Paths.get("uploads").toAbsolutePath().normalize();

        // Enlever le "/uploads/" initial => "covers/xxx.jpg" ou "previews/xxx.pdf"
        String relativePath = storedPath.substring("/uploads/".length());

        Path filePath = uploadRoot.resolve(relativePath).normalize();

        // Protection simple : le fichier doit rester DANS "uploads"
        if (!filePath.startsWith(uploadRoot) || !Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                // "inline" => affichage dans le navigateur (image/pdf)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + filePath.getFileName().toString() + "\"")
                .body(resource);
    }

    @GetMapping("/preview/{id}")
    public ResponseEntity<Resource> getPreview(@PathVariable Long id) throws IOException {
        Ressource r = service.getById(id);
        if (r == null || r.getPreviewPath() == null) {
            return ResponseEntity.notFound().build();
        }

        return serveFile(r.getPreviewPath());
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("ressource") Ressource formRessource,
            BindingResult result,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            @RequestParam(value = "previewFile", required = false) MultipartFile previewFile,
            Model model) throws IOException {
        // Vérification de la validation
        if (result.hasErrors()) {
            model.addAttribute("ressource", formRessource);
            return "ressource-form";
        }
        Ressource r;

        // 1) Création ou modification ?
        if (formRessource.getId() != null) {
            // MODIFICATION : on récupère l'entité existante en BDD
            r = service.getById(formRessource.getId());
            if (r == null) {
                return "/ressources"; // id inexistant
            }
        } else {
            // CREATION
            r = new Ressource();
        }

        // 2) Mettre à jour les champs simples
        r.setTitre(formRessource.getTitre());
        r.setAuteur(formRessource.getAuteur());
        r.setCategorie(formRessource.getCategorie());
        r.setDescription(formRessource.getDescription());
        r.setTypeRessource(formRessource.getTypeRessource());

        // 3) Gestion de la couverture : remplacer seulement si nouveau fichier
        if (coverFile != null && !coverFile.isEmpty()) {
            String coverFileName = storeFile(coverFile, "uploads/covers");
            r.setCoverPath("/uploads/covers/" + coverFileName);
        }
        // Sinon : on NE TOUCHE PAS à r.getCoverPath() → on garde l'ancienne

        // 4) Gestion de la preview selon le type
        if (r.getTypeRessource() == TypeRessource.LIVRE ||
                r.getTypeRessource() == TypeRessource.DOCUMENT) {

            if (previewFile != null && !previewFile.isEmpty()) {
                String contentType = previewFile.getContentType();
                if (contentType != null &&
                        (contentType.equals("application/pdf") || contentType.startsWith("image/"))) {

                    String previewFileName = storeFile(previewFile, "uploads/previews");
                    r.setPreviewPath("/uploads/previews/" + previewFileName);
                }
            }
            // Sinon : on garde la preview existante
        } else {
            // Si le type est passé à CD/DVD/REVUE, on supprime la preview
            r.setPreviewPath(null);
        }

        service.save(r);
        return "redirect:/ressources";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/ressources";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("ressource", service.getById(id));
        return "ressource-form";
    }
}
