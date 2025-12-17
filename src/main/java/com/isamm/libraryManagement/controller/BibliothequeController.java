package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.entity.Bibliotheque;
import com.isamm.libraryManagement.repository.BibliothequeRepository;
import com.isamm.libraryManagement.service.BibliothequeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bibliotheques")
public class BibliothequeController {

    private final BibliothequeRepository bibliothequeRepository;
    private final BibliothequeService service;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("bibliotheques", service.getAll());
        return "bibliotheques"; // liste.html
    }

    @GetMapping("/add")
    public String showForm(Model model) {
        model.addAttribute("bibliotheque", new Bibliotheque());
        model.addAttribute("bibliotheques", service.getAll()); // pour le select parent
        return "bibliotheque-form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("bibliotheque") Bibliotheque b,
            BindingResult result,
            Model model) {

        // Normaliser un minimum pour éviter " ABC " ≠ "ABC"
        if (b.getCode() != null) {
            b.setCode(b.getCode().trim());
        }

        // Gestion parent (comme tu l'as fait)
        if (b.getParentId() != null) {
            Bibliotheque parent = service.getById(b.getParentId());
            b.setParent(parent);
        } else {
            b.setParent(null);
        }

        // Vérifier l'unicité seulement si le champ "code" n'a pas déjà une erreur de
        // validation
        if (!result.hasFieldErrors("code")
                && b.getCode() != null
                && !b.getCode().isBlank()) {

            boolean duplicate = (b.getId() == null)
                    ? service.existsByCode(b.getCode()) // ajout
                    : service.existsByCodeAndIdNot(b.getCode(), b.getId()); // modification

            if (duplicate) {
                result.rejectValue(
                        "code",
                        "code.duplicate",
                        "Ce code est déjà utilisé. Veuillez choisir un autre code.");
            }
        }

        // Si erreurs => retourner au form + recharger la liste
        if (result.hasErrors()) {
            model.addAttribute("bibliotheques", service.getAll());
            return "bibliotheque-form";
        }

        // Sauvegarde + sécurité si conflit BD (cas concurrence)
        try {
            service.save(b);
        } catch (DataIntegrityViolationException ex) {
            result.rejectValue(
                    "code",
                    "code.duplicate",
                    "Ce code est déjà utilisé. Veuillez choisir un autre code.");
            model.addAttribute("bibliotheques", service.getAll());
            return "bibliotheque-form";
        }

        return "redirect:/bibliotheques";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Bibliotheque bib = service.getById(id);
        model.addAttribute("bibliotheque", bib);
        model.addAttribute("bibliotheques", service.getAll());
        return "bibliotheque-form";
    }

    // @GetMapping("/{id}/dependances")
    // public String afficherDependances(
    // @PathVariable Long id,
    // Model model) {

    // Bibliotheque b = service.getWithDependances(id);

    // model.addAttribute("bibliotheque", b);
    // model.addAttribute("nbExemplaires", b.getExemplaires().size());
    // model.addAttribute("nbSousBibliotheques", b.getSousBibliotheques().size());

    // return "bibliotheques-dependances";
    // }
    // @GetMapping("/{id}/dependances")
    // public String afficherDependances(@PathVariable Long id, Model model) {
    // Bibliotheque b = service.getWithDependances(id);

    // var exemplaires = (b.getExemplaires() != null) ? b.getExemplaires() :
    // java.util.Collections.emptyList();
    // var sousBibliotheques = (b.getSousBibliotheques() != null) ?
    // b.getSousBibliotheques()
    // : java.util.Collections.emptyList();

    // model.addAttribute("bibliotheque", b);

    // model.addAttribute("exemplaires", exemplaires);
    // model.addAttribute("sousBibliotheques", sousBibliotheques);

    // model.addAttribute("nbExemplaires", exemplaires.size());
    // model.addAttribute("nbSousBibliotheques", sousBibliotheques.size());

    // return "bibliotheques-dependances";
    // }
    @GetMapping("/{id}/dependances")
    public String afficherDependances(@PathVariable Long id, Model model) {
        Bibliotheque b = service.getWithDependances(id);

        var exemplaires = (b.getExemplaires() != null) ? b.getExemplaires()
                : java.util.Collections.emptyList();

        var sousBibliotheques = (b.getSousBibliotheques() != null) ? b.getSousBibliotheques()
                : java.util.Collections.emptyList();

        model.addAttribute("bibliotheque", b);
        model.addAttribute("exemplaires", exemplaires);
        model.addAttribute("sousBibliotheques", sousBibliotheques);
        model.addAttribute("nbExemplaires", exemplaires.size());
        model.addAttribute("nbSousBibliotheques", sousBibliotheques.size());

        return "bibliotheques-dependances";
    }

    @GetMapping("/delete/{id}")
    public String delete(

            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute(
                    "success", "Library successfully deleted");
        } catch (ResponseStatusException e) {

            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                redirectAttributes.addFlashAttribute("error", e.getReason());
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                redirectAttributes.addFlashAttribute("error", "Library does not exist");
            }
        }

        return "redirect:/bibliotheques";
    }
}
