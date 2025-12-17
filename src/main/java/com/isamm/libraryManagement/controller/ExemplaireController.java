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
import com.isamm.libraryManagement.entity.Ressource;
import com.isamm.libraryManagement.service.BibliothequeService;
import com.isamm.libraryManagement.service.ExemplaireService;
import com.isamm.libraryManagement.service.RessourceService;
import org.springframework.dao.DataIntegrityViolationException;
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
        try {
            Ressource ressource = ressourceService.getById(id);
            if (ressource == null) {
                return "redirect:/ressources";
            }
            model.addAttribute("ressource", ressource);
            model.addAttribute("exemplaires", exemplaireService.getByRessource(id));
            return "exemplaires-by-ressource";
        } catch (Exception e) {
            e.printStackTrace(); // <-- voir l'erreur exacte
            return "error"; // page error.html custom
        }
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
    // @RequestParam(name = "bibliothequeId", required = false) Long bibliothequeId,
    // Model model) {

    // if (bibliothequeId == null) {
    // result.reject("bibliothequeId.missing", "Veuillez choisir une
    // bibliothèque.");
    // }

    // if (result.hasErrors()) {
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

        // Toujours remettre ces attributs si on doit ré-afficher le form
        Ressource ressource = ressourceService.getById(ressourceId);
        if (ressource == null)
            return "redirect:/ressources";

        model.addAttribute("ressource", ressource);
        model.addAttribute("bibliotheques", bibliothequeService.getAll());

        // Normaliser codeBarre
        if (exemplaire.getCodeBarre() != null) {
            exemplaire.setCodeBarre(exemplaire.getCodeBarre().trim());
        }

        // Bibliothèque obligatoire (erreur "globale" si tu n'as pas un champ codeBarre)
        if (bibliothequeId == null) {
            result.reject("bibliothequeId.missing", "Veuillez choisir une bibliothèque.");
        }

        // Unicité codeBarre (avant save)
        if (!result.hasFieldErrors("codeBarre")
                && exemplaire.getCodeBarre() != null
                && !exemplaire.getCodeBarre().isBlank()) {

            boolean duplicate = (exemplaire.getId() == null)
                    ? exemplaireService.existsByCodeBarre(exemplaire.getCodeBarre())
                    : exemplaireService.existsByCodeBarreAndIdNot(exemplaire.getCodeBarre(), exemplaire.getId());

            if (duplicate) {
                result.rejectValue("codeBarre", "codeBarre.duplicate",
                        "Ce code-barres est déjà utilisé. Veuillez en choisir un autre.");
            }
        }

        if (result.hasErrors()) {
            return "exemplaire-form";
        }

        exemplaire.setRessource(ressource);
        exemplaire.setBibliotheque(bibliothequeService.getById(bibliothequeId));

        // Sécurité: si la BD refuse quand même (contrainte unique)
        try {
            exemplaireService.save(exemplaire);
        } catch (DataIntegrityViolationException ex) {
            result.rejectValue("codeBarre", "codeBarre.duplicate",
                    "Ce code-barres est déjà utilisé. Veuillez en choisir un autre.");
            return "exemplaire-form";
        }

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
