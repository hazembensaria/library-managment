package com.isamm.libraryManagement.service.impl;

import com.isamm.libraryManagement.entity.Bibliotheque;
import com.isamm.libraryManagement.repository.BibliothequeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BibliothequeServiceImplTest {

    @Mock
    private BibliothequeRepository repo;

    @InjectMocks
    private BibliothequeServiceImpl service;


    @Test
    void getAll_ShouldReturnList() {
        when(repo.findAll()).thenReturn(List.of(new Bibliotheque()));

        List<Bibliotheque> result = service.getAll();

        assertEquals(1, result.size());
        verify(repo).findAll();
    }


    @Test
    void getById_Found() {
        Bibliotheque b = new Bibliotheque();
        when(repo.findById(1L)).thenReturn(Optional.of(b));

        Bibliotheque result = service.getById(1L);

        assertNotNull(result);
    }

    @Test
    void getById_NotFound_ReturnsNull() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        Bibliotheque result = service.getById(1L);

        assertNull(result);
    }


    @Test
    void save_ShouldPersist() {
        Bibliotheque b = new Bibliotheque();
        when(repo.save(b)).thenReturn(b);

        Bibliotheque result = service.save(b);

        assertNotNull(result);
        verify(repo).save(b);
    }


    @Test
    void existsByCode_True() {
        when(repo.existsByCode("BIB01")).thenReturn(true);

        assertTrue(service.existsByCode("BIB01"));
    }

    @Test
    void existsByCodeAndIdNot_True() {
        when(repo.existsByCodeAndIdNot("BIB01", 1L)).thenReturn(true);

        assertTrue(service.existsByCodeAndIdNot("BIB01", 1L));
    }

    @Test
    void delete_Successful() {
        doNothing().when(repo).deleteById(1L);

        assertDoesNotThrow(() -> service.delete(1L));
        verify(repo).deleteById(1L);
    }

    @Test
    void delete_NotFound_Throws404() {
        doThrow(new EmptyResultDataAccessException(1))
                .when(repo).deleteById(1L);

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> service.delete(1L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Library not found", ex.getReason());
    }

    @Test
    void delete_DataIntegrityViolation_Throws409() {
        doThrow(new DataIntegrityViolationException("FK constraint"))
                .when(repo).deleteById(1L);

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> service.delete(1L));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Cannot be deleted: children or books exist", ex.getReason());
    }


    @Test
    void getWithDependances_Success() {
        Bibliotheque b = new Bibliotheque();
        b.setExemplaires(Collections.emptyList());
        b.setSousBibliotheques(Collections.emptyList());

        when(repo.findById(1L)).thenReturn(Optional.of(b));

        Bibliotheque result = service.getWithDependances(1L);

        assertNotNull(result);
        verify(repo).findById(1L);
    }

    @Test
    void getWithDependances_NotFound_ThrowsException() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        IllegalStateException ex =
                assertThrows(IllegalStateException.class,
                        () -> service.getWithDependances(1L));

        assertEquals("Library not found", ex.getMessage());
    }
}

