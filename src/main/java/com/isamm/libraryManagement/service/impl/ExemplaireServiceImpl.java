package com.isamm.libraryManagement.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.isamm.libraryManagement.repository.ExemplaireRepository;
import com.isamm.libraryManagement.service.ExemplaireService;
import com.isamm.libraryManagement.entity.Exemplaire;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExemplaireServiceImpl implements ExemplaireService {

    private final ExemplaireRepository repo;

    @Override
    public List<Exemplaire> getAll() {
        return repo.findAll();
    }

    @Override
    public Exemplaire save(Exemplaire e) {
        return repo.save(e);
    }

    @Override
    public Exemplaire getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Exemplaire> getByRessource(Long ressourceId) {
        return repo.findByRessourceId(ressourceId);
    }

    @Override
    public List<Exemplaire> getByBibliotheque(Long biblioId) {
        return repo.findByBibliothequeId(biblioId);
    }
}
