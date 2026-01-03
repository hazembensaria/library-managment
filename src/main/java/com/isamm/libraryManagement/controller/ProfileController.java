package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.entity.Role;
import com.isamm.libraryManagement.entity.User;
import com.isamm.libraryManagement.repository.UserRepository;
import com.isamm.libraryManagement.service.impl.ProfileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileServiceImpl profileService;

    private final UserRepository userRepository;




    // =========================
    // SHOW PROFILE PAGE
    // =========================
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {

        User user = profileService.getCurrentUser(authentication);
        model.addAttribute("profileUpdate", user);
        model.addAttribute("user", user);
        model.addAttribute("loans", profileService.getUserLoans(user));

        return "profile"; // templates/profile.html
    }

    // =========================
    // UPDATE PROFILE
    // =========================
    @PostMapping("/profile/update")
    public String updateProfile(
            Authentication authentication,
            @RequestParam String firstname,
            @RequestParam String lastname
    ) {
        User user = profileService.getCurrentUser(authentication);
        profileService.updateProfile(user, firstname, lastname);

        return "redirect:/profile";
    }

        @GetMapping("/userManagement")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String listUsers(Model model) {
        // Get all users
        List<User> allUsers = userRepository.findAll();

        model.addAttribute("allUsers", allUsers);
        model.addAttribute("roles", Role.values()); // Enum Role
        return "user-management"; // Thymeleaf template
    }

    @PostMapping("/admin/users/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String updateUserRole(@PathVariable Integer id, @RequestParam Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        user.setRole(role);
        userRepository.save(user);

        return "redirect:/user-management";
    }


}