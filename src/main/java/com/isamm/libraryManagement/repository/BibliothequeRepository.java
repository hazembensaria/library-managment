package com.isamm.libraryManagement.repository;

import com.isamm.libraryManagement.entity.Bibliotheque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BibliothequeRepository extends JpaRepository<Bibliotheque, Long> {
}