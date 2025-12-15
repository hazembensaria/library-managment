package com.isamm.libraryManagement.repository;

import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Integer userId);
}
