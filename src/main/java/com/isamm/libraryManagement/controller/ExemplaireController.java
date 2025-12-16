package com.isamm.libraryManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import com.isamm.libraryManagement.entity.Exemplaire;
import com.isamm.libraryManagement.service.BibliothequeService;
import com.isamm.libraryManagement.service.ExemplaireService;
import com.isamm.libraryManagement.service.RessourceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/exemplaires")
public class ExemplaireController {

    private final ExemplaireService exemplaireService;
    private final RessourceService ressourceService;
    private final BibliothequeService bibliothequeService;

    @GetMapping("/ressource/{id}")
    public String listByRessource(@PathVariable Long id, Model model) {
        model.addAttribute("ressource", ressourceService.getById(id));
        model.addAttribute("exemplaires", exemplaireService.getByRessource(id));
        return "exemplaires-by-ressource";
    }

    @GetMapping("/bibliotheque/{id}")
    public String listByBibliotheque(@PathVariable Long id, Model model) {
        model.addAttribute("bibliotheque", bibliothequeService.getById(id));
        model.addAttribute("exemplaires", exemplaireService.getByBibliotheque(id));
        return "exemplaires-by-bibliotheque";
    }

    @GetMapping("/add/{ressourceId}")
    public String addForm(@PathVariable Long ressourceId, Model model) {
        Exemplaire ex = new Exemplaire();
        ex.setDisponible(true);

        model.addAttribute("exemplaire", ex);
        model.addAttribute("ressource", ressourceService.getById(ressourceId));
        model.addAttribute("bibliotheques", bibliothequeService.getAll());
        return "exemplaire-form";
    }

    // @PostMapping("/save/{ressourceId}")
    // public String save(@PathVariable Long ressourceId,
    // @Valid @ModelAttribute("exemplaire") Exemplaire exemplaire,
    // BindingResult result,
    // Model model) {

    // if (result.hasErrors()) {
    // // renvoyer les infos nécessaires pour réafficher la page
    // model.addAttribute("ressource", ressourceService.getById(ressourceId));
    // model.addAttribute("bibliotheques", bibliothequeService.getAll());
    // return "exemplaire-form";
    // }
    // exemplaire.setRessource(ressourceService.getById(ressourceId));
    // exemplaire.setBibliotheque(bibliothequeService.getById(bibliothequeId));

    // exemplaireService.save(exemplaire);

    // return "redirect:/exemplaires/ressource/" + ressourceId;
    // }
    @PostMapping("/save/{ressourceId}")
    public String save(@PathVariable Long ressourceId,
            @Valid @ModelAttribute("exemplaire") Exemplaire exemplaire,
            BindingResult result,
            @RequestParam(name = "bibliothequeId", required = false) Long bibliothequeId,
            Model model) {

        if (bibliothequeId == null) {
            result.reject("bibliothequeId.missing", "Veuillez choisir une bibliothèque.");
        }

        if (result.hasErrors()) {
            model.addAttribute("ressource", ressourceService.getById(ressourceId));
            model.addAttribute("bibliotheques", bibliothequeService.getAll());
            return "exemplaire-form";
        }

        exemplaire.setRessource(ressourceService.getById(ressourceId));
        exemplaire.setBibliotheque(bibliothequeService.getById(bibliothequeId));

        exemplaireService.save(exemplaire);
        return "redirect:/exemplaires/ressource/" + ressourceId;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Exemplaire ex = exemplaireService.getById(id);
        Long ressourceId = ex.getRessource().getId();

        exemplaireService.delete(id);
        return "redirect:/exemplaires/ressource/" + ressourceId;
    }
}
