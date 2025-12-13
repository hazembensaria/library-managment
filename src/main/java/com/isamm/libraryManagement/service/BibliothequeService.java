package com.isamm.libraryManagement.service;

import com.isamm.libraryManagement.entity.Bibliotheque;
import java.util.List;

public interface BibliothequeService {

    List<Bibliotheque> getAll();

    Bibliotheque getById(Long id);

    Bibliotheque save(Bibliotheque b);

    void delete(Long id);
}
