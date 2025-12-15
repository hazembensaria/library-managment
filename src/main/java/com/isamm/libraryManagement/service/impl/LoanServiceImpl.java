package com.isamm.libraryManagement.service.impl;

import com.isamm.libraryManagement.entity.Exemplaire;
import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.entity.LoanStatus;
import com.isamm.libraryManagement.entity.User;
import com.isamm.libraryManagement.repository.ExemplaireRepository;
import com.isamm.libraryManagement.repository.LoanRepository;
import com.isamm.libraryManagement.repository.UserRepository;
import com.isamm.libraryManagement.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final ExemplaireRepository exemplaireRepository;
    private final UserRepository userRepository;

    private final int MAX_ACTIVE_LOANS = 3; // rule: max loans per user

    @Override
    public Loan reserve(Long exemplaireId, Integer userId) throws Exception {
        Exemplaire ex = exemplaireRepository.findById(exemplaireId).orElseThrow(() -> new Exception("Exemplaire not found"));
        if (ex.getDisponible() == null || !ex.getDisponible()) {
            throw new Exception("Exemplaire not available");
        }
        // contrôle sur l'état : on n'autorise la réservation que si l'exemplaire n'est pas abîmé
        if (ex.getEtat() == null || ex.getEtat().equalsIgnoreCase("abime") || ex.getEtat().equalsIgnoreCase("damaged")) {
            throw new Exception("Exemplaire state not valid for reservation");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
        // count active loans
        long active = loanRepository.findByUserId(userId).stream()
                .filter(l -> l.getStatus() == LoanStatus.RESERVED || l.getStatus() == LoanStatus.BORROWED)
                .count();
        if (active >= MAX_ACTIVE_LOANS) {
            throw new Exception("User has reached loan limit");
        }
        // create reservation
        Loan loan = new Loan();
        loan.setExemplaire(ex);
        loan.setUser(user);
        loan.setStatus(LoanStatus.BORROWED);
        loan.setStartDate(new Date());
        loan.setEndDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 14));
        // mark exemplaire unavailable
        ex.setDisponible(false);
        exemplaireRepository.save(ex);
        return loanRepository.save(loan);
    }

    @Override
    public Loan borrow(Long loanId) throws Exception {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new Exception("Loan not found"));
        if (loan.getStatus() != LoanStatus.RESERVED) {
            throw new Exception("Loan must be RESERVED to borrow");
        }
        loan.setStatus(LoanStatus.BORROWED);
        loan.setStartDate(new Date());
        // example period 14 days
        loan.setEndDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 14));
        return loanRepository.save(loan);
    }

    @Override
    public Loan returnLoan(Long loanId) throws Exception {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new Exception("Loan not found"));
        if (loan.getStatus() != LoanStatus.BORROWED) {
            throw new Exception("Loan must be BORROWED to return");
        }
        loan.setStatus(LoanStatus.RETURNED);
        loan.setEndDate(new Date());
        // mark exemplaire available
        Exemplaire ex = loan.getExemplaire();
        ex.setDisponible(true);
        exemplaireRepository.save(ex);
        return loanRepository.save(loan);
    }

    @Override
    public List<Loan> listAll() {
        return loanRepository.findAll();
    }

    @Override
    public List<Loan> listByUser(Integer userId) {
        return loanRepository.findByUserId(userId);
    }
}
