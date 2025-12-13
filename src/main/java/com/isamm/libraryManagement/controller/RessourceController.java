package com.isamm.libraryManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import com.isamm.libraryManagement.entity.Ressource;
import com.isamm.libraryManagement.service.RessourceService;

import lombok.RequiredArgsConstructor;

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

    @PostMapping("/save")
    public String save(@ModelAttribute Ressource r) {
        service.save(r);
        return "redirect:/ressources";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("ressource", service.getById(id));
        return "ressource-form";
    }
}
