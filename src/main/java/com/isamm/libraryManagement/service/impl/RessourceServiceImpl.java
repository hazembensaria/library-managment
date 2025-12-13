package com.isamm.libraryManagement.service.impl;

import com.isamm.libraryManagement.entity.Ressource;
import com.isamm.libraryManagement.repository.RessourceRepository;
import com.isamm.libraryManagement.service.RessourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RessourceServiceImpl implements RessourceService {

    private final RessourceRepository repo;

    @Override
    public List<Ressource> getAll() {
        return repo.findAll(); // récupération de toutes les ressources via le repository
    }

    @Override
    public Ressource save(Ressource r) {
        return repo.save(r); // sauvegarde dans la BDD
    }

    @Override
    public Ressource getById(Long id) {
        return repo.findById(id).orElse(null); // recherche une ressource par son ID
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id); // suppression d'une ressource
    }
}
