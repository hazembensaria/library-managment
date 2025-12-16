package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.entity.User;
import com.isamm.libraryManagement.repository.UserRepository;
import com.isamm.libraryManagement.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class UIController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    public UIController(DashboardService dashboardService, UserRepository userRepository) {
        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
    }

    @RequestMapping("/register")
    public String registerPage() {
        return "register";
    }

    @RequestMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {

        // KPIs (affichés seulement pour ADMIN dans ton HTML)
        model.addAttribute("kpis", dashboardService.getDashboardKpis());

        if (authentication == null) {
            return "dashboard";
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("USER"));

        boolean isBiblio = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("BIBLIOTHECAIRE"));

        // ================= USER =================
        if (isUser) {
            String email = authentication.getName(); // doit être email
            Optional<User> optUser = userRepository.findByEmail(email);

            if (optUser.isPresent()) {
                User currentUser = optUser.get();

                List<Loan> mesPrets = dashboardService.getMesPretsEnCours(currentUser);
                Map<Long, Long> joursRestantsMap = dashboardService.buildJoursRestantsMap(mesPrets);

                model.addAttribute("mesPrets", mesPrets);
                model.addAttribute("joursRestantsMap", joursRestantsMap);

                long enCours = mesPrets.size();
                long enRetard = dashboardService.countMesPretsEnRetard(currentUser);
                long restitues = dashboardService.countMesPretsRestitues(currentUser);

                model.addAttribute("statsUser", new Object() {
                    public long getPretsEnCours() { return enCours; }
                    public long getPretsEnRetard() { return enRetard; }
                    public long getPretsRestitues() { return restitues; }
                });
            }
        }

        // ================= BIBLIOTHECAIRE =================
        if (isBiblio) {
            List<Loan> pretsEnCours = dashboardService.getPretsEnCours();

            Map<Long, Long> joursRestantsMapB = dashboardService.buildJoursRestantsMap(pretsEnCours);
            Map<Long, String> prioriteMap = dashboardService.buildPrioriteMap(pretsEnCours);

            model.addAttribute("pretsEnCours", pretsEnCours);
            model.addAttribute("joursRestantsMap", joursRestantsMapB);
            model.addAttribute("prioriteMap", prioriteMap);

            long retards = dashboardService.countRetardsAll();
            long aRendre = dashboardService.countARendreAll();
            long actifs = dashboardService.countPretsEnCoursAll();
            long rotation = dashboardService.tauxRotationSimple();

            model.addAttribute("statsLibrarian", new Object() {
                public long getRetards() { return retards; }
                public long getARendre() { return aRendre; }
                public long getEnCours() { return actifs; }
                public long getTauxRotation() { return rotation; }
            });
        }

        // ================= ADMIN =================
        if (isAdmin) {
            model.addAttribute("chartCategorie", dashboardService.getLoansByCategorie());
            model.addAttribute("chartBibliotheque", dashboardService.getLoansByBibliotheque());
        }

        return "dashboard";
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("kpis", dashboardService.getDashboardKpis());
        return "home";
    }
}
