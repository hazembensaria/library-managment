package com.isamm.libraryManagement.repository;

import com.isamm.libraryManagement.entity.Exemplaire;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExemplaireRepository extends JpaRepository<Exemplaire, Long> {
    List<Exemplaire> findByRessourceId(Long ressourceId);

    List<Exemplaire> findByBibliothequeId(Long biblioId);
}
