package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class ExportController {

  private final DashboardService dashboardService;

  @GetMapping(value = "/csv", produces = "text/csv")
  public ResponseEntity<byte[]> exportCsv() {
    var kpis = dashboardService.getDashboardKpis();

    String csv =
        "metric,value\n" +
        "totalBibliotheques," + kpis.getTotalBibliotheques() + "\n" +
        "totalRessources," + kpis.getTotalRessources() + "\n" +
        "totalExemplaires," + kpis.getTotalExemplaires() + "\n" +
        "exemplairesDisponibles," + kpis.getExemplairesDisponibles() + "\n" +
        "exemplairesIndisponibles," + kpis.getExemplairesIndisponibles() + "\n";

    byte[] content = csv.getBytes(StandardCharsets.UTF_8);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=dashboard_kpis_" + LocalDate.now() + ".csv")
        .contentType(MediaType.valueOf("text/csv"))
        .body(content);
  }

  @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> exportPdf() {
    // Version simple (placeholder) : on renvoie un PDF minimal.
    // Pour un vrai PDF pro : iText/OpenPDF (je te le fais après si tu veux).
    byte[] pdfBytes = MinimalPdfBuilder.build("Dashboard KPIs",
        dashboardService.getDashboardKpis().toString());

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=dashboard_kpis.pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdfBytes);
  }
}
