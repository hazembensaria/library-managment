package com.isamm.libraryManagement.repository;

import com.isamm.libraryManagement.entity.Bibliotheque;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BibliothequeRepositoryTest {

    @Autowired
    private BibliothequeRepository bibliothequeRepository;

    @Test
    void existsByCode_shouldReturnTrue_whenCodeExists() {
        // Arrange
        Bibliotheque b = new Bibliotheque();
        b.setCode("BIB001");
        b.setNom("Bibliothèque Centrale");

        bibliothequeRepository.save(b);

        // Act
        boolean exists = bibliothequeRepository.existsByCode("BIB001");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByCode_shouldReturnFalse_whenCodeDoesNotExist() {
        // Act
        boolean exists = bibliothequeRepository.existsByCode("UNKNOWN");

        // Assert
        assertFalse(exists);
    }

    @Test
    void existsByCodeAndIdNot_shouldReturnTrue_whenSameCodeDifferentId() {
        // Arrange
        Bibliotheque b1 = new Bibliotheque();
        b1.setCode("BIB002");
        b1.setNom("Bibliothèque A");
        bibliothequeRepository.save(b1);

        Bibliotheque b2 = new Bibliotheque();
        b2.setCode("BIB003");
        b2.setNom("Bibliothèque B");
        bibliothequeRepository.save(b2);

        // Act
        boolean exists = bibliothequeRepository.existsByCodeAndIdNot(
                "BIB002",
                b2.getId()
        );

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByCodeAndIdNot_shouldReturnFalse_whenSameId() {
        // Arrange
        Bibliotheque b = new Bibliotheque();
        b.setCode("BIB004");
        b.setNom("Bibliothèque Unique");
        bibliothequeRepository.save(b);

        // Act
        boolean exists = bibliothequeRepository.existsByCodeAndIdNot(
                "BIB004",
                b.getId()
        );

        // Assert
        assertFalse(exists);
    }


}
