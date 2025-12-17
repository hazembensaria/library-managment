package com.isamm.libraryManagement.repository;

import com.isamm.libraryManagement.entity.Bibliotheque;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BibliothequeRepository extends JpaRepository<Bibliotheque, Long> {
    boolean existsByCode(String code);

    @EntityGraph(attributePaths = {
            "exemplaires",
            "exemplaires.ressource",
            "sousBibliotheques"
    })

    // utile en modification (on exclut l'id courant)
    boolean existsByCodeAndIdNot(String code, Long id);

    @EntityGraph(attributePaths = { "exemplaires", "sousBibliotheques" })
    Optional<Bibliotheque> findWithDependancesById(Long id);

}