package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.entity.Ressource;
import com.isamm.libraryManagement.entity.RessourceSpecifications;
import com.isamm.libraryManagement.entity.TypeRessource;
import com.isamm.libraryManagement.repository.RessourceRepository;
import com.isamm.libraryManagement.service.RessourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/ressources")
@RequiredArgsConstructor
public class RessourceController {

    private final RessourceService service;
    private final RessourceRepository ressourceRepository;

    @GetMapping
    public String list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "publishDate") String sort,
            @RequestParam(defaultValue = "desc") String dir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 4), 48);

        // Parse enum type safely
        TypeRessource typeEnum = null;
        if (type != null && !type.isBlank()) {
            try {
                typeEnum = TypeRessource.valueOf(type.toUpperCase());
            } catch (Exception ignored) {
            }
        }

        // Sort sécurisé
        String[] allowedSortFields = { "titre", "auteur", "categorie", "typeRessource", "publishDate" };
        if (!java.util.Arrays.asList(allowedSortFields).contains(sort)) {
            sort = "publishDate";
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        // Specification simplifiée (plus de filtre availability)
        var spec = RessourceSpecifications.advanced(q, categorie, typeEnum, null, dateFrom, dateTo);
        Page<Ressource> result = ressourceRepository.findAll(spec, pageable);

        // Model attributes
        model.addAttribute("page", result);
        model.addAttribute("ressources", result.getContent());
        model.addAttribute("q", q);
        model.addAttribute("categorie", categorie);
        model.addAttribute("type", typeEnum);
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("size", size);
        model.addAttribute("types", TypeRessource.values());

        return "ressources";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("ressource", new Ressource());
        return "ressource-form";
    }

    // =================== UPLOAD ===================
    private String storeFile(MultipartFile file, String directory) throws IOException {
        String originalFilename = StringUtils
                .cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex != -1)
            extension = originalFilename.substring(dotIndex);

        String fileName = UUID.randomUUID() + extension;

        Path uploadDir = Paths.get(directory).toAbsolutePath().normalize();
        if (!Files.exists(uploadDir))
            Files.createDirectories(uploadDir);

        Path filePath = uploadDir.resolve(fileName).normalize();
        file.transferTo(filePath.toFile());
        return fileName;
    }

    // =================== FILE SERVE ===================
    @GetMapping("/cover/{id}")
    public ResponseEntity<Resource> getCover(@PathVariable Long id) throws IOException {
        Ressource r = service.getById(id);
        if (r == null || r.getCoverPath() == null)
            return ResponseEntity.notFound().build();
        return serveFile(r.getCoverPath());
    }

    @GetMapping("/preview/{id}")
    public ResponseEntity<Resource> getPreview(@PathVariable Long id) throws IOException {
        Ressource r = service.getById(id);
        if (r == null || r.getPreviewPath() == null)
            return ResponseEntity.notFound().build();
        return serveFile(r.getPreviewPath());
    }

    private ResponseEntity<Resource> serveFile(String storedPath) throws IOException {
        if (storedPath == null || storedPath.isBlank())
            return ResponseEntity.notFound().build();

        storedPath = storedPath.replace("\\", "/");
        if (!storedPath.startsWith("/uploads/"))
            return ResponseEntity.notFound().build();

        Path uploadRoot = Paths.get("uploads").toAbsolutePath().normalize();
        String relativePath = storedPath.substring("/uploads/".length());
        Path filePath = uploadRoot.resolve(relativePath).normalize();

        if (!filePath.startsWith(uploadRoot) || !Files.exists(filePath))
            return ResponseEntity.notFound().build();

        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        if (contentType == null)
            contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName() + "\"")
                .body(resource);
    }

    // =================== SAVE / EDIT / DELETE ===================
    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("ressource") Ressource formRessource,
            BindingResult result,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            @RequestParam(value = "previewFile", required = false) MultipartFile previewFile,
            Model model) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("ressource", formRessource);
            return "ressource-form";
        }

        Ressource r;
        if (formRessource.getId() != null) {
            r = service.getById(formRessource.getId());
            if (r == null)
                return "redirect:/ressources";
        } else {
            r = new Ressource();
        }

        r.setTitre(formRessource.getTitre());
        r.setAuteur(formRessource.getAuteur());
        r.setCategorie(formRessource.getCategorie());
        r.setDescription(formRessource.getDescription());
        r.setTypeRessource(formRessource.getTypeRessource());
        r.setPublishDate(formRessource.getPublishDate());

        if (coverFile != null && !coverFile.isEmpty()) {
            String coverFileName = storeFile(coverFile, "uploads/covers");
            r.setCoverPath("/uploads/covers/" + coverFileName);
        }

        if (r.getTypeRessource() == TypeRessource.LIVRE || r.getTypeRessource() == TypeRessource.DOCUMENT) {
            if (previewFile != null && !previewFile.isEmpty()) {
                String contentType = previewFile.getContentType();
                if (contentType != null
                        && (contentType.equals("application/pdf") || contentType.startsWith("image/"))) {
                    String previewFileName = storeFile(previewFile, "uploads/previews");
                    r.setPreviewPath("/uploads/previews/" + previewFileName);
                }
            }
        } else {
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