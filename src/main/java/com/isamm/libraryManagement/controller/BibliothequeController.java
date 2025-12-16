package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.entity.Bibliotheque;
import com.isamm.libraryManagement.service.BibliothequeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bibliotheques")
public class BibliothequeController {

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
        if (result.hasErrors()) {
            result.getFieldErrors().forEach(e -> System.out.println(
                    e.getField() + " -> " + e.getDefaultMessage() + " (rejected=" + e.getRejectedValue() + ")"));
            model.addAttribute("bibliotheques", service.getAll());
            return "bibliotheque-form";
        }

        if (b.getParentId() != null) {
            Bibliotheque parent = service.getById(b.getParentId());
            b.setParent(parent);
        } else {
            b.setParent(null); // bibliothèque centrale
        }

        service.save(b);
        return "redirect:/bibliotheques";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Bibliotheque bib = service.getById(id);
        model.addAttribute("bibliotheque", bib);
        model.addAttribute("bibliotheques", service.getAll());
        return "bibliotheque-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("success", "Bibliothèque supprimée avec succès !");
        } catch (ResponseStatusException e) {
            ra.addFlashAttribute("error", e.getReason());
        }
        return "redirect:/bibliotheques";
    }

}
