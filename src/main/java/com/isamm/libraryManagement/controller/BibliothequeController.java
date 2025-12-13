package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.entity.Bibliotheque;
import com.isamm.libraryManagement.service.BibliothequeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        return "bibliotheque-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("bibliotheque") Bibliotheque b) {
        service.save(b);
        return "redirect:/bibliotheques";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Bibliotheque bib = service.getById(id);
        model.addAttribute("bibliotheque", bib);
        return "bibliotheque-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/bibliotheques";
    }
}
