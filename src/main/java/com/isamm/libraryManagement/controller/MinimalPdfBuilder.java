package com.isamm.libraryManagement.controller;

import java.nio.charset.StandardCharsets;

/**
 * PDF minimal "maison" (pour tester Export PDF rapidement).
 * Pour un PDF pro (tableaux, styles), on passera à OpenPDF/iText.
 */
public class MinimalPdfBuilder {

  public static byte[] build(String title, String content) {
    // PDF ultra simple (texte brut)
    String safe = (title + "\n\n" + content).replace("(", "\\(").replace(")", "\\)");
    String pdf =
        "%PDF-1.4\n" +
        "1 0 obj<<>>endobj\n" +
        "2 0 obj<< /Type /Catalog /Pages 3 0 R >>endobj\n" +
        "3 0 obj<< /Type /Pages /Kids [4 0 R] /Count 1 >>endobj\n" +
        "4 0 obj<< /Type /Page /Parent 3 0 R /MediaBox [0 0 612 792] /Contents 5 0 R /Resources<< /Font<< /F1 6 0 R >> >> >>endobj\n" +
        "5 0 obj<< /Length " + (safe.length() + 73) + " >>stream\n" +
        "BT /F1 12 Tf 50 740 Td (" + safe + ") Tj ET\n" +
        "endstream endobj\n" +
        "6 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj\n" +
        "xref\n0 7\n0000000000 65535 f \n" +
        "trailer<< /Size 7 /Root 2 0 R >>\nstartxref\n0\n%%EOF";
    return pdf.getBytes(StandardCharsets.ISO_8859_1);
  }
}
