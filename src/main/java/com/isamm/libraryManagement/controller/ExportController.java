package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class ExportController {

  private final DashboardService dashboardService;

  @GetMapping(value = "/csv", produces = "text/csv")
  public ResponseEntity<byte[]> exportCsv(Authentication auth) {

    var kpis = dashboardService.getDashboardKpis();

    String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    String username = (auth != null) ? auth.getName() : "unknown";
    String sep = ";";

    String csv =
        "Library Hub - Dashboard KPI\n" +
        "Genere le" + sep + date + "\n" +
        "Genere par" + sep + username + "\n\n" +
        "Indicateur" + sep + "Valeur\n" +
        "Total bibliotheques" + sep + kpis.getTotalBibliotheques() + "\n" +
        "Total ressources" + sep + kpis.getTotalRessources() + "\n" +
        "Total exemplaires" + sep + kpis.getTotalExemplaires() + "\n" +
        "Exemplaires disponibles" + sep + kpis.getExemplairesDisponibles() + "\n" +
        "Exemplaires indisponibles" + sep + kpis.getExemplairesIndisponibles() + "\n";

    // BOM UTF-8 pour Excel
    byte[] bom = new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF};
    byte[] data = csv.getBytes(StandardCharsets.UTF_8);

    byte[] content = new byte[bom.length + data.length];
    System.arraycopy(bom, 0, content, 0, bom.length);
    System.arraycopy(data, 0, content, bom.length, data.length);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=dashboard_kpis_" + LocalDate.now() + ".csv")
        .contentType(MediaType.valueOf("text/csv; charset=UTF-8"))
        .body(content);
  }


  
  @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> exportPdf() {

    var kpis = dashboardService.getDashboardKpis();
    String generatedAt = java.time.LocalDateTime.now().toString();
    String generatedBy = org.springframework.security.core.context.SecurityContextHolder
        .getContext().getAuthentication().getName();

    byte[] pdfBytes = PdfReportBuilder.buildDashboardKpisPdf(kpis, generatedAt, generatedBy);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=dashboard_kpis.pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdfBytes);
  }

}
