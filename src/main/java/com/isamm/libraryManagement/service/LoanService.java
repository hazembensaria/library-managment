package com.isamm.libraryManagement.service;

import com.isamm.libraryManagement.entity.Loan;

import java.util.List;

public interface LoanService {
    Loan reserve(Long exemplaireId, Integer userId) throws Exception;
    Loan borrow(Long loanId) throws Exception;
    Loan returnLoan(Long loanId) throws Exception;
    List<Loan> listAll();
    List<Loan> listByUser(Integer userId);
}
