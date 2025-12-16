package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanRestController {

    private final LoanService loanService;

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(
            @RequestParam Long exemplaireId) {
        try {
            Loan loan = loanService.reserve(exemplaireId);
            return ResponseEntity.ok(loan);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Map<String, String> body = new HashMap<>();
            body.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(body);
        }
    }

    @PostMapping("/reserveForUser")
    @PreAuthorize("hasAnyAuthority('ADMIN','BIBLIOTHECAIRE')")
    public ResponseEntity<?> reserveForUser(
            @RequestParam Long exemplaireId,
            @RequestParam Integer userId) {
        try {
            Loan loan = loanService.reserveForUser(exemplaireId, userId);
            return ResponseEntity.ok(loan);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Map<String, String> body = new HashMap<>();
            body.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(body);
        }
    }
}

