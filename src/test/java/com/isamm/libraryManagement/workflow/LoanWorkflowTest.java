package com.isamm.libraryManagement.workflow;

import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.entity.LoanStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanWorkflowTest {

    @Test
    void workflow_shouldChangeStatusCorrectly() {
        Loan loan = new Loan();

        loan.setStatus(LoanStatus.RESERVE);
        assertEquals(LoanStatus.RESERVE, loan.getStatus());

        loan.setStatus(LoanStatus.EMPRUNTE);
        assertEquals(LoanStatus.EMPRUNTE, loan.getStatus());

        loan.setStatus(LoanStatus.RETOURNE);
        assertEquals(LoanStatus.RETOURNE, loan.getStatus());
    }
}
