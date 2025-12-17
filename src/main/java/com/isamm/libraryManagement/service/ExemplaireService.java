package com.isamm.libraryManagement.service;

import java.util.List;

import com.isamm.libraryManagement.entity.Exemplaire;

public interface ExemplaireService {
    List<Exemplaire> getAll();

    Exemplaire save(Exemplaire e);

    Exemplaire getById(Long id);

    void delete(Long id);

    List<Exemplaire> getByRessource(Long ressourceId);

    List<Exemplaire> getByBibliotheque(Long biblioId);

    boolean existsByCodeBarre(String code);

    boolean existsByCodeBarreAndIdNot(String code, Long id);

}
