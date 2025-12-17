package com.isamm.libraryManagement.service.impl;

import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.entity.User;
import com.isamm.libraryManagement.repository.LoanRepository;
import com.isamm.libraryManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    // 🔹 get connected user from DB
    public User getCurrentUser(Authentication authentication) {
        String email = authentication.getName(); // JWT / Spring Security
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 🔹 loan history
    public List<Loan> getUserLoans(User user) {
        return loanRepository.findByUser(user);
    }

    // 🔹 update profile (email locked)
    public void updateProfile(User dbUser, String firstname, String lastname) {
        dbUser.setFirstname(firstname);
        dbUser.setLastname(lastname);
        userRepository.save(dbUser);
    }
}