package com.isamm.libraryManagement.service.impl;

import com.isamm.libraryManagement.entity.*;
import com.isamm.libraryManagement.repository.ExemplaireRepository;
import com.isamm.libraryManagement.repository.LoanRepository;
import com.isamm.libraryManagement.repository.UserRepository;
import com.isamm.libraryManagement.service.EmailService;
import com.isamm.libraryManagement.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private ExemplaireRepository exemplaireRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private EmailService emailService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private LoanServiceImpl loanService;

    @BeforeEach
    void setUp() {
        // Clear security context before each test to avoid pollution
        SecurityContextHolder.clearContext();
    }

    @Test
    void reserve_SuccessfulReservation() {
        // Arrange
        Long exemplaireId = 1L;
        String email = "test@example.com";

        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setId(exemplaireId);
        exemplaire.setDisponible(true);

        User user = new User();
        user.setEmail(email);
        user.setRole(Role.USER);

        // Security Context Mocking
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);

        // Repository Mocking
        when(exemplaireRepository.findById(exemplaireId)).thenReturn(Optional.of(exemplaire));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(loanRepository.existsByExemplaireAndStatusIn(any(), anyList())).thenReturn(false);
        when(loanRepository.countByUserAndStatusIn(any(), anyList())).thenReturn(0L);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Loan result = loanService.reserve(exemplaireId);

        // Assert
        assertNotNull(result);
        assertEquals(LoanStatus.RESERVE, result.getStatus());
        assertFalse(exemplaire.getDisponible()); // Should mark as unavailable
        verify(loanRepository).save(any(Loan.class));
        verify(notificationService).envoyerNotification(any(User.class), anyString(), eq(NotificationType.RAPPEL));
    }

    @Test
    void reserve_CopyNotAvailable_ThrowsException() {
        // Arrange
        Long exemplaireId = 1L;
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setDisponible(false);

        when(exemplaireRepository.findById(exemplaireId)).thenReturn(Optional.of(exemplaire));

        
        String email = "test@example.com";
        User user = new User();
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));


        // Act & Assert
        assertThrows(IllegalStateException.class, () -> loanService.reserve(exemplaireId));
    }

    @Test
    void borrow_SuccessfulBorrow() {
        // Arrange
        Long loanId = 1L;
        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setStatus(LoanStatus.RESERVE);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Loan result = loanService.borrow(loanId);

        // Assert
        assertEquals(LoanStatus.EMPRUNTE, result.getStatus());
        assertNotNull(result.getBorrowedAt());
    }
    @Test
    void reserve_UserReachedMaxLoans_ThrowsException() {
        Long exemplaireId = 1L;
        String email = "user@example.com";

        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setId(exemplaireId);
        exemplaire.setDisponible(true);

        User user = new User();
        user.setEmail(email);
        user.setRole(Role.USER);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);

        when(exemplaireRepository.findById(exemplaireId)).thenReturn(Optional.of(exemplaire));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(loanRepository.existsByExemplaireAndStatusIn(any(), anyList())).thenReturn(false);
        when(loanRepository.countByUserAndStatusIn(any(), anyList())).thenReturn(3L);

        assertThrows(IllegalStateException.class, () -> loanService.reserve(exemplaireId));
    }
    @Test
    void reserve_CopyAlreadyReservedOrBorrowed_ThrowsException() {
        Long exemplaireId = 1L;
        String email = "user@example.com";

        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setId(exemplaireId);
        exemplaire.setDisponible(true);

        User user = new User();
        user.setEmail(email);
        user.setRole(Role.USER);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);

        when(exemplaireRepository.findById(exemplaireId)).thenReturn(Optional.of(exemplaire));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(loanRepository.existsByExemplaireAndStatusIn(any(), anyList())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> loanService.reserve(exemplaireId));
    }
    @Test
    void reserveForUser_Successful() {
        Long exemplaireId = 1L;
        Integer userId = 2;

        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setId(exemplaireId);
        exemplaire.setDisponible(true);

        User user = new User();
        user.setRole(Role.USER);

        when(exemplaireRepository.findById(exemplaireId)).thenReturn(Optional.of(exemplaire));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(loanRepository.existsByExemplaireAndStatusIn(any(), anyList())).thenReturn(false);
        when(loanRepository.countByUserAndStatusIn(any(), anyList())).thenReturn(0L);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(exemplaireRepository.save(any(Exemplaire.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanService.reserveForUser(exemplaireId, userId);

        assertNotNull(result);
        assertEquals(LoanStatus.RESERVE, result.getStatus());
        assertFalse(exemplaire.getDisponible());
    }
    @Test
    void borrow_NotReserved_ThrowsException() {
        Long loanId = 1L;
        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setStatus(LoanStatus.RETOURNE);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class, () -> loanService.borrow(loanId));
    }
    @Test
    void returnLoan_SuccessfulReturn() {
        // Arrange
        Long loanId = 1L;
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setDisponible(false);
        
        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setStatus(LoanStatus.EMPRUNTE);
        loan.setExemplaire(exemplaire);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Loan result = loanService.returnLoan(loanId);

        // Assert
        assertEquals(LoanStatus.RETOURNE, result.getStatus());
        assertNotNull(result.getReturnedAt());
        assertTrue(exemplaire.getDisponible()); // Should become available
    }
    @Test
    void returnLoan_AlreadyReturned() {
        Long loanId = 1L;
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setDisponible(true);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setStatus(LoanStatus.RETOURNE);
        loan.setExemplaire(exemplaire);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        Loan result = loanService.returnLoan(loanId);

        assertEquals(LoanStatus.RETOURNE, result.getStatus());
        verify(loanRepository, never()).save(any(Loan.class)); // Already returned, should not save again
    }
    @Test
    void getAll_ShouldCallRepository() {
        loanService.getAll();
        verify(loanRepository).findAll();
    }

    @Test
    void getByUser_ShouldReturnLoans() {
        Integer userId = 1;
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        loanService.getByUser(userId);
        verify(loanRepository).findByUser(user);
    }

    @Test
    void getByStatus_ShouldReturnLoans() {
        LoanStatus status = LoanStatus.RESERVE;
        loanService.getByStatus(status);
        verify(loanRepository).findByStatus(status);
    }

}
