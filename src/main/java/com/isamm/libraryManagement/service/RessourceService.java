package com.isamm.libraryManagement.service;


import com.isamm.libraryManagement.entity.Ressource;
import java.util.List;

public interface RessourceService {

    List<Ressource> getAll();

    Ressource save(Ressource r);

    Ressource getById(Long id);

    void delete(Long id);
}
