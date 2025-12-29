package com.isamm.libraryManagement.repository;

import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.entity.LoanStatus;
import com.isamm.libraryManagement.entity.Role;
import com.isamm.libraryManagement.entity.User;
import com.isamm.libraryManagement.entity.Exemplaire;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private BibliothequeRepository bibliothequeRepository;

    @Autowired
    private RessourceRepository ressourceRepository;

    @Test
    void findByUser_ShouldReturnLoansForSpecificUser() {
        // Arrange
        User user = new User();
        user.setEmail("test@test.com");
        user.setRole(Role.USER);
        userRepository.save(user);

        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setCodeBarre("CB12345");
        exemplaire.setEtat("Bon");
        exemplaire.setDisponible(true);
        exemplaireRepository.save(exemplaire);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setExemplaire(exemplaire); // Set the relationship
        loan.setStatus(LoanStatus.EMPRUNTE);
        loanRepository.save(loan);

        // Act
        List<Loan> loans = loanRepository.findByUser(user);


        // Assert
        assertNotNull(loans);
        assertEquals(1, loans.size());
        assertEquals(user.getId(), loans.get(0).getUser().getId());
    }

    @Test
    void findByStatus_ShouldReturnLoansWithSpecificStatus() {
        // Arrange
        // Need a user for loans
        User user = new User();
        user.setEmail("status_test@test.com");
        user.setRole(Role.USER);
        userRepository.save(user);

        Exemplaire ex1 = new Exemplaire();
        ex1.setCodeBarre("CB-S-1");
        ex1.setEtat("New");
        exemplaireRepository.save(ex1);

        Loan loan1 = new Loan();
        loan1.setUser(user);
        loan1.setExemplaire(ex1);
        loan1.setStatus(LoanStatus.EMPRUNTE);
        loanRepository.save(loan1);

        Exemplaire ex2 = new Exemplaire();
        ex2.setCodeBarre("CB-S-2");
        ex2.setEtat("New");
        exemplaireRepository.save(ex2);

        Loan loan2 = new Loan();
        loan2.setUser(user);
        loan2.setExemplaire(ex2);
        loan2.setStatus(LoanStatus.RESERVE);
        loanRepository.save(loan2);

        // Act
        List<Loan> borrowedLoans = loanRepository.findByStatus(LoanStatus.EMPRUNTE);

        // Assert
        assertEquals(1, borrowedLoans.size());
        assertEquals(LoanStatus.EMPRUNTE, borrowedLoans.get(0).getStatus());
    }
}
