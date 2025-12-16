package com.isamm.libraryManagement.repository;

import com.isamm.libraryManagement.entity.Exemplaire;
import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.entity.LoanStatus;
import com.isamm.libraryManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    long countByUserAndStatusIn(User user, Collection<LoanStatus> statuses);

    boolean existsByExemplaireAndStatusIn(Exemplaire exemplaire, Collection<LoanStatus> statuses);

    List<Loan> findByUser(User user);

    List<Loan> findByStatus(LoanStatus status);
}


