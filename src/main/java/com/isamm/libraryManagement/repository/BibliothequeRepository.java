package com.isamm.libraryManagement.repository;

import com.isamm.libraryManagement.entity.Bibliotheque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BibliothequeRepository extends JpaRepository<Bibliotheque, Long> {
    boolean existsByCode(String code);

    // utile en modification (on exclut l'id courant)
    boolean existsByCodeAndIdNot(String code, Long id);

}