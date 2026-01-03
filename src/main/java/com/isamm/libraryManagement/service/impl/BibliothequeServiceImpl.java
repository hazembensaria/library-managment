package com.isamm.libraryManagement.service.impl;

import com.isamm.libraryManagement.entity.Bibliotheque;
import com.isamm.libraryManagement.repository.BibliothequeRepository;
import com.isamm.libraryManagement.service.BibliothequeService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
    public boolean existsByCode(String code) {
        return repo.existsByCode(code);
    }

    @Override
    public boolean existsByCodeAndIdNot(String code, Long id) {
        return repo.existsByCodeAndIdNot(code, id);
    }

    @Override
    public void delete(Long id) {
        try {
            repo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            // id inexistant → rien à faire ou lever exception custom
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Library not found");
        } catch (DataIntegrityViolationException e) {
            // enfants ou livres liés
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot be deleted: children or books exist");
        }
    }

    public Bibliotheque getWithDependances(Long id) {
        Bibliotheque b = repo.findById(id)
                .orElseThrow(() -> new IllegalStateException("Library not found"));

        // Force le chargement des listes
        b.getExemplaires().size();
        b.getSousBibliotheques().size();

        return b;
    }

    // public Bibliotheque getWithDependances(Long id) {
    // return repo.findWithDependancesById(id)
    // .orElseThrow(() -> new IllegalStateException("Library not found"));
    // }

    // @Transactional(readOnly = true)
    // public Bibliotheque getWithDependances(Long id) {
    // return repo.findWithDependancesById(id)
    // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Library
    // not found"));
    // }
}
