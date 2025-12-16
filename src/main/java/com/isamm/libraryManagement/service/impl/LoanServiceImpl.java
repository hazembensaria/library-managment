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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private static final int MAX_LOANS_PER_USER = 3;

    private final LoanRepository loanRepository;
    private final ExemplaireRepository exemplaireRepository;
    private final UserRepository userRepository;

    @Override
    public Loan reserve(Long exemplaireId) {
        Exemplaire exemplaire = exemplaireRepository.findById(exemplaireId)
                .orElseThrow(() -> new IllegalArgumentException("Exemplaire introuvable"));

        // Récupérer l'utilisateur courant (email dans le SecurityContext)
        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Aucun utilisateur authentifié");
        }
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        // Règle 1 : disponibilité de l'exemplaire
        if (Boolean.FALSE.equals(exemplaire.getDisponible())) {
            throw new IllegalStateException("Cet exemplaire n'est pas disponible");
        }

        // Règle 1 bis : aucun prêt actif sur cet exemplaire
        if (loanRepository.existsByExemplaireAndStatusIn(
                exemplaire,
                Arrays.asList(LoanStatus.RESERVE, LoanStatus.EMPRUNTE))) {
            throw new IllegalStateException("Cet exemplaire est déjà réservé ou emprunté");
        }

        // Règle 2 : limite de prêts par utilisateur
        long activeLoansForUser = loanRepository.countByUserAndStatusIn(
                user,
                Arrays.asList(LoanStatus.RESERVE, LoanStatus.EMPRUNTE));
        if (activeLoansForUser >= MAX_LOANS_PER_USER) {
            throw new IllegalStateException("Limite de prêts atteinte pour cet utilisateur");
        }

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setExemplaire(exemplaire);
        loan.setStatus(LoanStatus.RESERVE);
        Date now = new Date();
        loan.setCreatedAt(now);

        // On peut fixer une date d'échéance simple, par exemple +14 jours
        long fourteenDaysMillis = 14L * 24 * 60 * 60 * 1000;
        loan.setDueAt(new Date(now.getTime() + fourteenDaysMillis));

        // Marquer l'exemplaire comme non disponible
        exemplaire.setDisponible(false);
        exemplaireRepository.save(exemplaire);

        return loanRepository.save(loan);
    }

    @Override
    public Loan borrow(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Prêt introuvable"));

        if (loan.getStatus() != LoanStatus.RESERVE) {
            throw new IllegalStateException("Seules les réservations peuvent être validées en emprunt");
        }

        loan.setStatus(LoanStatus.EMPRUNTE);
        loan.setBorrowedAt(new Date());
        return loanRepository.save(loan);
    }

    @Override
    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Prêt introuvable"));

        if (loan.getStatus() == LoanStatus.RETOURNE) {
            return loan;
        }

        loan.setStatus(LoanStatus.RETOURNE);
        loan.setReturnedAt(new Date());

        // Libérer l'exemplaire
        Exemplaire exemplaire = loan.getExemplaire();
        exemplaire.setDisponible(true);
        exemplaireRepository.save(exemplaire);

        return loanRepository.save(loan);
    }

    @Override
    public List<Loan> getAll() {
        return loanRepository.findAll();
    }

    @Override
    public List<Loan> getByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        return loanRepository.findByUser(user);
    }

    @Override
    public List<Loan> getByStatus(LoanStatus status) {
        return loanRepository.findByStatus(status);
    }
}


