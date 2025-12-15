package com.isamm.libraryManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardKpis {
  private long totalBibliotheques;
  private long totalRessources;
  private long totalExemplaires;
  private long exemplairesDisponibles;
  private long exemplairesIndisponibles;
}
