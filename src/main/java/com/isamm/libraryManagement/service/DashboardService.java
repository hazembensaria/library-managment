package com.isamm.libraryManagement.service;

import com.isamm.libraryManagement.dto.DashboardKpis;
import com.isamm.libraryManagement.repository.BibliothequeRepository;
import com.isamm.libraryManagement.repository.ExemplaireRepository;
import com.isamm.libraryManagement.repository.RessourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final BibliothequeRepository bibliothequeRepository;
  private final RessourceRepository ressourceRepository;
  private final ExemplaireRepository exemplaireRepository;

  public DashboardKpis getDashboardKpis() {
    long totalBib = bibliothequeRepository.count();
    long totalRes = ressourceRepository.count();
    long totalEx = exemplaireRepository.count();
    long dispo = exemplaireRepository.countByDisponibleTrue();
    long indispo = exemplaireRepository.countByDisponibleFalse();

    return new DashboardKpis(totalBib, totalRes, totalEx, dispo, indispo);
  }
}
