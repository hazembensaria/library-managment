package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.entity.LoanStatus;
import com.isamm.libraryManagement.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    public String listAll(Model model) {
        List<Loan> allLoans = loanService.getAll();
        model.addAttribute("loans", allLoans);
        return "loans";
    }

    @GetMapping("/pending")
    public String listPending(Model model) {
        model.addAttribute("loans", loanService.getByStatus(LoanStatus.RESERVE));
        return "loans";
    }

    @GetMapping("/reserve")
    public String reservePage() {
        // Page simple qui contient un petit formulaire pour saisir userId et exemplaireId
        return "reserve";
    }

    @PostMapping("/validate/{id}")
    public String validateLoan(@PathVariable Long id) {
        loanService.borrow(id);
        return "redirect:/loans";
    }

    @PostMapping("/return/{id}")
    public String returnLoan(@PathVariable Long id) {
        loanService.returnLoan(id);
        return "redirect:/loans";
    }
}


