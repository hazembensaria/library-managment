package com.isamm.libraryManagement.service.impl;

import com.isamm.libraryManagement.entity.Bibliotheque;
import com.isamm.libraryManagement.repository.BibliothequeRepository;
import com.isamm.libraryManagement.service.BibliothequeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BibliothequeServiceImpl implements BibliothequeService {

    private final BibliothequeRepository repo;

    @Override
    public List<Bibliotheque> getAll() {
        return repo.findAll();
    }

    @Override
    public Bibliotheque getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public Bibliotheque save(Bibliotheque b) {
        return repo.save(b);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
