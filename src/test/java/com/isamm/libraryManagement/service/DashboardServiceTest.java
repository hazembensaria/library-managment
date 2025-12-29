package com.isamm.libraryManagement.service;

import com.isamm.libraryManagement.dto.DashboardKpis;
import com.isamm.libraryManagement.repository.BibliothequeRepository;
import com.isamm.libraryManagement.repository.ExemplaireRepository;
import com.isamm.libraryManagement.repository.LoanRepository;
import com.isamm.libraryManagement.repository.RessourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private BibliothequeRepository bibliothequeRepository;

    @Mock
    private RessourceRepository ressourceRepository;

    @Mock
    private ExemplaireRepository exemplaireRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashboardKpis_ShouldReturnCorrectCounts() {
        // Arrange
        when(bibliothequeRepository.count()).thenReturn(5L);
        when(ressourceRepository.count()).thenReturn(50L);
        when(exemplaireRepository.count()).thenReturn(100L);
        // Note: countByDisponibleTrue + countByDisponibleFalse should ideally sum to count, 
        // but for unit testing we just verify the return values map correctly.
        when(exemplaireRepository.countByDisponibleTrue()).thenReturn(80L);
        when(exemplaireRepository.countByDisponibleFalse()).thenReturn(20L);

        // Act
        DashboardKpis kpis = dashboardService.getDashboardKpis();

        // Assert
        assertNotNull(kpis);
        assertEquals(5L, kpis.getTotalBibliotheques());
        assertEquals(50L, kpis.getTotalRessources());
        assertEquals(100L, kpis.getTotalExemplaires());
        assertEquals(80L, kpis.getExemplairesDisponibles());
        assertEquals(20L, kpis.getExemplairesIndisponibles());
    }
}
