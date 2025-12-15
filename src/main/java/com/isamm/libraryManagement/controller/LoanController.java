package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping("/loans")
    public String loansPage(Model model) {
        List<Loan> loans = loanService.listAll();
        model.addAttribute("loans", loans);
        return "loans";
    }

    @GetMapping("/reserve")
    public String reservePage() {
        return "reserve";
    }

    @PostMapping("/api/loans/reserve")
    @ResponseBody
    public Object reserve(@RequestParam Long exemplaireId, @RequestParam Integer userId) {
        try {
            Loan l = loanService.reserve(exemplaireId, userId);
            return l;
        } catch (Exception e) {
            return java.util.Map.of("error", e.getMessage());
        }
    }

    @PostMapping("/api/loans/{id}/borrow")
    @ResponseBody
    public Object borrow(@PathVariable Long id) {
        try {
            return loanService.borrow(id);
        } catch (Exception e) {
            return java.util.Map.of("error", e.getMessage());
        }
    }

    @PostMapping("/api/loans/{id}/return")
    @ResponseBody
    public Object returnLoan(@PathVariable Long id) {
        try {
            return loanService.returnLoan(id);
        } catch (Exception e) {
            return java.util.Map.of("error", e.getMessage());
        }
    }

    @GetMapping("/api/loans")
    @ResponseBody
    public List<Loan> getAllLoans() {
        return loanService.listAll();
    }
}
