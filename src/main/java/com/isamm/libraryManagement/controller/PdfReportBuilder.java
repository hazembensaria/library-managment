package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.dto.DashboardKpis;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

public class PdfReportBuilder {

  // ---------- Colors (consistent with your Bootstrap design) ----------
  private static final Color BLUE = new Color(74, 144, 226);
  private static final Color BLUE_DARK = new Color(55, 121, 233);
  private static final Color TEXT = new Color(33, 37, 41);
  private static final Color MUTED = new Color(90, 98, 110);
  private static final Color LINE = new Color(230, 230, 230);
  private static final Color CARD_BG = new Color(248, 250, 252);

  private static final Color GREEN = new Color(34, 197, 94);
  private static final Color RED = new Color(239, 68, 68);
  private static final Color PURPLE = new Color(124, 58, 237);
  private static final Color TEAL = new Color(14, 165, 233);

  public static byte[] buildDashboardKpisPdf(DashboardKpis kpis, String generatedAt, String generatedBy) {

    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

      Document document = new Document(PageSize.A4, 36, 36, 48, 50);
      PdfWriter writer = PdfWriter.getInstance(document, out);

      writer.setPageEvent(new PdfPageEventHelper() {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
          PdfContentByte cb = writer.getDirectContent();
          Font f = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(120, 120, 120));
          Phrase p = new Phrase("Library Hub • KPI Report • Page " + writer.getPageNumber(), f);
          ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, p,
              (document.left() + document.right()) / 2, document.bottom() - 18, 0);
        }
      });

      document.open();

      // ------------------ VISUAL HEADER (banner) ------------------
      addHeaderBanner(writer, document, generatedAt, generatedBy);

      // Spacing after header
      document.add(Chunk.NEWLINE);
      document.add(Chunk.NEWLINE);

      // ------------------ KPI CARDS (dashboard look) ------------------
      PdfPTable cards = new PdfPTable(2);
      cards.setWidthPercentage(100);
      cards.setSpacingBefore(6);
      cards.setSpacingAfter(14);
      cards.setWidths(new float[]{1f, 1f});

      cards.addCell(kpiCard("Libraries", String.valueOf(kpis.getTotalBibliotheques()), BLUE, "\uD83C\uDFE2"));
      cards.addCell(kpiCard("Resources", String.valueOf(kpis.getTotalRessources()), PURPLE, "\uD83D\uDCD6"));
      cards.addCell(kpiCard("Copies", String.valueOf(kpis.getTotalExemplaires()), TEAL, "\uD83D\uDCE6"));
      cards.addCell(kpiCard("Available", String.valueOf(kpis.getExemplairesDisponibles()), GREEN, "✓"));
      cards.addCell(kpiCard("Unavailable", String.valueOf(kpis.getExemplairesIndisponibles()), RED, "✕"));
      // (small filler to complete the grid)
      cards.addCell(emptyCard());

      document.add(cards);

      // ------------------ MINI CHART (bars) ------------------
      Paragraph chartTitle = new Paragraph("Copies Availability", new Font(Font.HELVETICA, 12, Font.BOLD, TEXT));
      chartTitle.setSpacingAfter(8);
      document.add(chartTitle);

      PdfPTable chartBox = new PdfPTable(1);
      chartBox.setWidthPercentage(100);
      PdfPCell chartCell = new PdfPCell();
      chartCell.setPadding(14);
      chartCell.setBorderColor(LINE);
      chartCell.setBackgroundColor(Color.WHITE);

      PdfContentByte cb = writer.getDirectContent();
      PdfTemplate template = cb.createTemplate(500, 90);

      long available = kpis.getExemplairesDisponibles();
      long unavailable = kpis.getExemplairesIndisponibles();
      long total = Math.max(1, available + unavailable);

      // Bar sizes
      float w = 420f;
      float availableW = (float) (w * ((double) available / total));
      float unavailableW = (float) (w * ((double) unavailable / total));

      // Draw bars
      template.setColorFill(GREEN);
      template.rectangle(0, 45, availableW, 16);
      template.fill();

      template.setColorFill(RED);
      template.rectangle(0, 18, unavailableW, 16);
      template.fill();

      // Labels
      ColumnText.showTextAligned(template, Element.ALIGN_LEFT,
          new Phrase("Available: " + available, new Font(Font.HELVETICA, 10, Font.BOLD, TEXT)),
          0, 65, 0);

      ColumnText.showTextAligned(template, Element.ALIGN_LEFT,
          new Phrase("Unavailable: " + unavailable, new Font(Font.HELVETICA, 10, Font.BOLD, TEXT)),
          0, 38, 0);

      Image chartImg = Image.getInstance(template);
      chartImg.scaleToFit(500, 90);
      chartCell.addElement(chartImg);
      chartBox.addCell(chartCell);

      document.add(chartBox);

      document.add(Chunk.NEWLINE);

      // ------------------ DETAILED TABLE (professional) ------------------
      Paragraph tableTitle = new Paragraph("KPI Details", new Font(Font.HELVETICA, 12, Font.BOLD, TEXT));
      tableTitle.setSpacingAfter(8);
      document.add(tableTitle);

      PdfPTable table = new PdfPTable(new float[]{3.2f, 1.2f});
      table.setWidthPercentage(100);

      addHeaderCell(table, "Metric");
      addHeaderCell(table, "Value");

      addRow(table, "Total libraries", String.valueOf(kpis.getTotalBibliotheques()));
      addRow(table, "Total resources", String.valueOf(kpis.getTotalRessources()));
      addRow(table, "Total copies", String.valueOf(kpis.getTotalExemplaires()));
      addRow(table, "Available copies", String.valueOf(kpis.getExemplairesDisponibles()));
      addRow(table, "Unavailable copies", String.valueOf(kpis.getExemplairesIndisponibles()));

      document.add(table);

      Paragraph note = new Paragraph(
          "\nSource: values computed from the database (Library / Resource / Copy).",
          new Font(Font.HELVETICA, 10, Font.ITALIC, new Color(120, 120, 120))
      );
      document.add(note);

      document.close();
      return out.toByteArray();

    } catch (Exception e) {
      throw new RuntimeException("PDF generation error", e);
    }
  }

  // ---------------------- HEADER BANNER ----------------------
  private static void addHeaderBanner(PdfWriter writer, Document doc, String generatedAt, String generatedBy) {
    PdfContentByte cb = writer.getDirectContent();
    float left = doc.left();
    float right = doc.right();
    float top = doc.top() + 40;          // area above content
    float height = 80;

    // “gradient-like”: 2 rectangles
    cb.setColorFill(BLUE);
    cb.rectangle(left, top - height, (right - left) * 0.62f, height);
    cb.fill();

    cb.setColorFill(BLUE_DARK);
    cb.rectangle(left + (right - left) * 0.62f, top - height, (right - left) * 0.38f, height);
    cb.fill();

    // LH logo (white rounded square)
    cb.setColorFill(Color.WHITE);
    cb.roundRectangle(left + 14, top - 60, 42, 42, 10);
    cb.fill();

    ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
        new Phrase("LH", new Font(Font.HELVETICA, 16, Font.BOLD, BLUE_DARK)),
        left + 35, top - 40, 0);

    // Title
    ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
        new Phrase("Dashboard • KPI Report", new Font(Font.HELVETICA, 16, Font.BOLD, Color.WHITE)),
        left + 70, top - 30, 0);

    // Meta
    String meta = "Generated at: " + generatedAt + "   |   Generated by: " + generatedBy;
    ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
        new Phrase(meta, new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(240, 248, 255))),
        left + 70, top - 48, 0);
  }

  // ---------------------- KPI CARD ----------------------
  private static PdfPCell kpiCard(String label, String value, Color accent, String iconText) {
    PdfPCell cell = new PdfPCell();
    cell.setBorderColor(LINE);
    cell.setBackgroundColor(Color.WHITE);
    cell.setPadding(14);
    cell.setMinimumHeight(82);

    PdfPTable inner = new PdfPTable(new float[]{1.0f, 3.2f});
    inner.setWidthPercentage(100);

    PdfPCell icon = new PdfPCell(new Phrase(iconText, new Font(Font.HELVETICA, 16, Font.BOLD, Color.WHITE)));
    icon.setHorizontalAlignment(Element.ALIGN_CENTER);
    icon.setVerticalAlignment(Element.ALIGN_MIDDLE);
    icon.setBackgroundColor(accent);
    icon.setBorderColor(accent);
    icon.setPadding(10);
    icon.setFixedHeight(52);

    PdfPCell txt = new PdfPCell();
    txt.setBorderColor(Color.WHITE);
    txt.setPaddingLeft(12);

    Paragraph pLabel = new Paragraph(label, new Font(Font.HELVETICA, 10, Font.BOLD, MUTED));
    Paragraph pValue = new Paragraph(value, new Font(Font.HELVETICA, 18, Font.BOLD, TEXT));
    pValue.setSpacingBefore(4);

    txt.addElement(pLabel);
    txt.addElement(pValue);

    inner.addCell(icon);
    inner.addCell(txt);

    cell.addElement(inner);
    return cell;
  }

  private static PdfPCell emptyCard() {
    PdfPCell cell = new PdfPCell(new Phrase(""));
    cell.setBorderColor(Color.WHITE);
    cell.setBackgroundColor(CARD_BG);
    cell.setPadding(0);
    return cell;
  }

  // ---------------------- TABLE HELPERS ----------------------
  private static void addHeaderCell(PdfPTable table, String text) {
    Font f = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
    PdfPCell cell = new PdfPCell(new Phrase(text, f));
    cell.setBackgroundColor(BLUE);
    cell.setPadding(10);
    cell.setBorderColor(LINE);
    table.addCell(cell);
  }

  private static void addRow(PdfPTable table, String left, String right) {
    Font f = new Font(Font.HELVETICA, 11, Font.NORMAL, TEXT);

    PdfPCell c1 = new PdfPCell(new Phrase(left, f));
    c1.setPadding(10);
    c1.setBorderColor(LINE);
    table.addCell(c1);

    PdfPCell c2 = new PdfPCell(new Phrase(right, f));
    c2.setPadding(10);
    c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
    c2.setBorderColor(LINE);
    table.addCell(c2);
  }
}
