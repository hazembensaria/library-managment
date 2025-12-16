package com.isamm.libraryManagement.service;

import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.entity.LoanStatus;

import java.util.List;

public interface LoanService {

    Loan reserve(Long exemplaireId);

    Loan borrow(Long loanId);

    Loan returnLoan(Long loanId);

    List<Loan> getAll();

    List<Loan> getByUser(Integer userId);

    List<Loan> getByStatus(LoanStatus status);
}


