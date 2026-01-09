package com.isamm.libraryManagement.repository;

import com.isamm.libraryManagement.entity.Bibliotheque;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BibliothequeRepository extends JpaRepository<Bibliotheque, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    @Query("""
                SELECT b FROM Bibliotheque b
                LEFT JOIN FETCH b.exemplaires e
                LEFT JOIN FETCH e.ressource
                LEFT JOIN FETCH b.sousBibliotheques
                WHERE b.id = :id
            """)
    Optional<Bibliotheque> findWithDependances(@Param("id") Long id);

}
