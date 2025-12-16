package com.isamm.libraryManagement.service;

import com.isamm.libraryManagement.dto.DashboardKpis;
import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.entity.LoanStatus;
import com.isamm.libraryManagement.entity.User;
import com.isamm.libraryManagement.repository.BibliothequeRepository;
import com.isamm.libraryManagement.repository.ExemplaireRepository;
import com.isamm.libraryManagement.repository.LoanRepository;
import com.isamm.libraryManagement.repository.RessourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;


@Service
@RequiredArgsConstructor
public class DashboardService {

  private final BibliothequeRepository bibliothequeRepository;
  private final RessourceRepository ressourceRepository;
  private final ExemplaireRepository exemplaireRepository;
  private final LoanRepository loanRepository;

  // ================= ADMIN KPIs =================
  public DashboardKpis getDashboardKpis() {
    long totalBib = bibliothequeRepository.count();
    long totalRes = ressourceRepository.count();
    long totalEx = exemplaireRepository.count();
    long dispo = exemplaireRepository.countByDisponibleTrue();
    long indispo = exemplaireRepository.countByDisponibleFalse();
    return new DashboardKpis(totalBib, totalRes, totalEx, dispo, indispo);
  }
  
//================= ADMIN Charts (DB) =================
public Map<String, Long> getLoansByCategorie() {
 List<Object[]> rows = loanRepository.countLoansByCategorie(LoanStatus.EMPRUNTE);
 Map<String, Long> result = new LinkedHashMap<>();

 for (Object[] row : rows) {
   String categorie = row[0] != null ? row[0].toString() : "Non catégorisé";
   Long count = ((Number) row[1]).longValue();
   result.put(categorie, count);
 }
 return result;
}

public Map<String, Long> getLoansByBibliotheque() {
 List<Object[]> rows = loanRepository.countLoansByBibliotheque(LoanStatus.EMPRUNTE);
 Map<String, Long> result = new LinkedHashMap<>();

 for (Object[] row : rows) {
   String nomBiblio = row[0] != null ? row[0].toString() : "Sans nom";
   Long count = ((Number) row[1]).longValue();
   result.put(nomBiblio, count);
 }
 return result;
}


  // ================= USER: mes prêts en cours =================
  public List<Loan> getMesPretsEnCours(User user) {
    List<Loan> all = loanRepository.findByUser(user);
    List<Loan> res = new ArrayList<>();
    for (Loan l : all) {
      if (l.getStatus() == LoanStatus.EMPRUNTE && l.getReturnedAt() == null) {
        res.add(l);
      }
    }
    return res;
  }

  // ================= BIBLIOTHECAIRE: prêts en cours =================
  public List<Loan> getPretsEnCours() {
    List<Loan> all = loanRepository.findByStatus(LoanStatus.EMPRUNTE);
    List<Loan> res = new ArrayList<>();
    for (Loan l : all) {
      if (l.getReturnedAt() == null) {
        res.add(l);
      }
    }
    return res;
  }

  // ================= Calcul Jours Restants =================
  public long joursRestants(Date dueAt) {
    if (dueAt == null) return 0;
    LocalDate today = LocalDate.now();
    LocalDate due = dueAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    return ChronoUnit.DAYS.between(today, due);
  }

  // ================= Priorité =================
  public String priorite(Date dueAt) {
    long jr = joursRestants(dueAt);
    if (jr < 0) return "HAUTE";
    if (jr <= 3) return "MOYENNE";
    return "BASSE";
  }

  // ================= Maps pour Thymeleaf =================
  public Map<Long, Long> buildJoursRestantsMap(List<Loan> loans) {
    Map<Long, Long> map = new HashMap<>();
    for (Loan l : loans) {
      map.put(l.getId(), joursRestants(l.getDueAt()));
    }
    return map;
  }

  public Map<Long, String> buildPrioriteMap(List<Loan> loans) {
    Map<Long, String> map = new HashMap<>();
    for (Loan l : loans) {
      map.put(l.getId(), priorite(l.getDueAt()));
    }
    return map;
  }

  // ================= Stats =================
  public long countMesPretsEnRetard(User user) {
    List<Loan> loans = getMesPretsEnCours(user);
    long c = 0;
    for (Loan l : loans) {
      if (joursRestants(l.getDueAt()) < 0) c++;
    }
    return c;
  }

  public long countMesPretsRestitues(User user) {
    return loanRepository.countByUserAndStatusIn(user, List.of(LoanStatus.RETOURNE));
  }

  public long countPretsEnCoursAll() {
    return getPretsEnCours().size();
  }

  public long countRetardsAll() {
    List<Loan> loans = getPretsEnCours();
    long c = 0;
    for (Loan l : loans) {
      if (joursRestants(l.getDueAt()) < 0) c++;
    }
    return c;
  }

  public long countARendreAll() {
    List<Loan> loans = getPretsEnCours();
    long c = 0;
    for (Loan l : loans) {
      long jr = joursRestants(l.getDueAt());
      if (jr >= 0 && jr <= 3) c++;
    }
    return c;
  }

  public long tauxRotationSimple() {
    long totalEx = exemplaireRepository.count();
    if (totalEx == 0) return 0;
    long actifs = countPretsEnCoursAll();
    return Math.round((actifs * 100.0) / totalEx);
  }
}
