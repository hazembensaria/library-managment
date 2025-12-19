package com.isamm.libraryManagement.service.impl;

import com.isamm.libraryManagement.entity.Exemplaire;
import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.entity.LoanStatus;
import com.isamm.libraryManagement.entity.NotificationType;
import com.isamm.libraryManagement.entity.User;
import com.isamm.libraryManagement.repository.ExemplaireRepository;
import com.isamm.libraryManagement.repository.LoanRepository;
import com.isamm.libraryManagement.repository.UserRepository;
import com.isamm.libraryManagement.service.EmailService;
import com.isamm.libraryManagement.service.LoanService;
import com.isamm.libraryManagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private static final int MAX_LOANS_PER_USER = 3;
    private static final SimpleDateFormat DF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final LoanRepository loanRepository;
    private final ExemplaireRepository exemplaireRepository;
    private final UserRepository userRepository;

    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    public Loan reserve(Long exemplaireId) {

        Exemplaire exemplaire = exemplaireRepository.findById(exemplaireId)
                .orElseThrow(() -> new IllegalArgumentException("Copy not found"));

        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new IllegalStateException("No authenticated user");
        }

        // auth.getName() = username/email (in your case = email)
        String usernameOrEmail = auth.getName();

        User user = userRepository.findByEmail(usernameOrEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return doReserve(exemplaire, user);
    }

    @Override
    public Loan reserveForUser(Long exemplaireId, Integer userId) {

        Exemplaire exemplaire = exemplaireRepository.findById(exemplaireId)
                .orElseThrow(() -> new IllegalArgumentException("Copy not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return doReserve(exemplaire, user);
    }

    private Loan doReserve(Exemplaire exemplaire, User user) {

        // 1) availability
        if (Boolean.FALSE.equals(exemplaire.getDisponible())) {
            throw new IllegalStateException("This copy is not available");
        }

        // 2) already reserved/borrowed?
        if (loanRepository.existsByExemplaireAndStatusIn(
                exemplaire, Arrays.asList(LoanStatus.RESERVE, LoanStatus.EMPRUNTE))) {
            throw new IllegalStateException("This copy is already reserved or borrowed");
        }

        // 3) loan limit (USER only)
        if (user.getRole() == com.isamm.libraryManagement.entity.Role.USER) {
            long activeLoans = loanRepository.countByUserAndStatusIn(
                    user, Arrays.asList(LoanStatus.RESERVE, LoanStatus.EMPRUNTE));

            if (activeLoans >= MAX_LOANS_PER_USER) {
                throw new IllegalStateException("Loan limit reached for this user");
            }
        }

        // 4) create loan
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setExemplaire(exemplaire);
        loan.setStatus(LoanStatus.RESERVE);

        Date now = new Date();
        loan.setCreatedAt(now);

        long fourteenDaysMillis = 14L * 24 * 60 * 60 * 1000;
        loan.setDueAt(new Date(now.getTime() + fourteenDaysMillis));

        // make copy unavailable
        exemplaire.setDisponible(false);
        exemplaireRepository.save(exemplaire);

        // 5) save loan
        Loan saved = loanRepository.save(loan);

        // 6) safe display info
        String title = "—";
        String barcode = "—";
        String libraryName = "—";

        try { title = (exemplaire.getRessource() != null) ? exemplaire.getRessource().getTitre() : "—"; } catch (Exception ignored) {}
        try { barcode = (exemplaire.getCodeBarre() != null) ? exemplaire.getCodeBarre().toString() : "—"; } catch (Exception ignored) {}
        try { libraryName = (exemplaire.getBibliotheque() != null) ? exemplaire.getBibliotheque().getNom() : "—"; } catch (Exception ignored) {}

        String msg = "📌 Reservation confirmed: \"" + title + "\" (barcode: " + barcode + ", library: " + libraryName + ").";

        // 7) WOW EMAIL (reservation only)
        try {
            sendReserveWowMail(saved, title, barcode, libraryName);
        } catch (Exception e) {
            System.out.println("Reserve email error: " + e.getMessage());
        }

        // 8) NOTIFICATION (DB + WS + email via NotificationService)
        try {
            notificationService.envoyerNotification(user, msg, NotificationType.RAPPEL);
        } catch (Exception e) {
            System.out.println("Reserve notification error: " + e.getMessage());
        }

        return saved;
    }

    @Override
    public Loan borrow(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (loan.getStatus() != LoanStatus.RESERVE) {
            throw new IllegalStateException("Only reservations can be validated as a borrow");
        }

        loan.setStatus(LoanStatus.EMPRUNTE);
        loan.setBorrowedAt(new Date());

        // no email/notification here (as you want)
        return loanRepository.save(loan);
    }

    @Override
    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (loan.getStatus() == LoanStatus.RETOURNE) {
            return loan;
        }

        loan.setStatus(LoanStatus.RETOURNE);
        loan.setReturnedAt(new Date());

        Exemplaire exemplaire = loan.getExemplaire();
        exemplaire.setDisponible(true);
        exemplaireRepository.save(exemplaire);

        // no email/notification here (as you want)
        return loanRepository.save(loan);
    }

    // =========================
    // WOW EMAIL : RESERVATION ONLY
    // =========================

    private void sendReserveWowMail(Loan loan, String title, String barcode, String libraryName) {

        User user = loan.getUser();
        if (user == null) return;

        // in your system getUsername() = email
        String email = user.getUsername();
        if (email == null || email.isBlank()) return;

        String fullName = buildFullName(user);

        String due = loan.getDueAt() != null ? DF.format(loan.getDueAt()) : "—";
        String created = loan.getCreatedAt() != null ? DF.format(loan.getCreatedAt()) : "—";

        String subject = "📌 Reservation saved";
        String html = wowEmailHtml(
                "📌 Reservation saved",
                "Hello " + fullName + ",",
                "Your reservation has been successfully recorded.",
                new String[][]{
                        {"Loan ID", String.valueOf(loan.getId())},
                        {"Resource", title},
                        {"Barcode", barcode},
                        {"Library", libraryName},
                        {"Created at", created},
                        {"Due date", due},
                        {"Status", String.valueOf(loan.getStatus())}
                },
                "You will receive another notification when the librarian validates the borrow."
        );

        emailService.sendHtml(email, subject, html);
        System.out.println("RESERVE EMAIL sent to: " + email);
    }

    private String buildFullName(User user) {
        String fn = user.getFirstname() != null ? user.getFirstname() : "";
        String ln = user.getLastname() != null ? user.getLastname() : "";
        String name = (fn + " " + ln).trim();
        return name.isEmpty() ? "dear reader" : name;
    }

    private String wowEmailHtml(String headerTitle,
                                String hello,
                                String subtitle,
                                String[][] rows,
                                String note) {

        StringBuilder rowsHtml = new StringBuilder();
        for (int i = 0; i < rows.length; i++) {
            String label = escapeHtml(rows[i][0]);
            String value = escapeHtml(rows[i][1]);
            rowsHtml.append("""
                <div style="display:flex;gap:12px;justify-content:space-between;padding:12px 14px;%s">
                  <div style="font-size:13px;color:#6b7280;">%s</div>
                  <div style="font-size:13px;font-weight:700;color:#111827;text-align:right;">%s</div>
                </div>
            """.formatted(i == rows.length - 1 ? "" : "border-bottom:1px solid #e5e7eb;", label, value));
        }

        return """
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
</head>
<body style="margin:0;padding:0;background:#f3f4f6;font-family:Arial,Helvetica,sans-serif;">
  <div style="max-width:680px;margin:0 auto;padding:24px;">
    <div style="background:linear-gradient(135deg,#0f172a,#2563eb);border-radius:18px;padding:22px 24px;color:#fff;">
      <div style="font-size:13px;opacity:.9;letter-spacing:.3px;">Library Management System</div>
      <div style="font-size:24px;font-weight:800;margin-top:6px;line-height:1.2;">%s</div>
      <div style="font-size:13px;opacity:.9;margin-top:6px;">Automatic notification</div>
    </div>

    <div style="background:#ffffff;border-radius:18px;padding:22px 24px;margin-top:14px;box-shadow:0 10px 25px rgba(0,0,0,.06);">
      <div style="font-size:16px;font-weight:800;color:#111827;">%s</div>
      <div style="margin-top:10px;font-size:14px;line-height:1.7;color:#374151;">%s</div>

      <div style="margin-top:16px;border:1px solid #e5e7eb;border-radius:14px;overflow:hidden;">
        %s
      </div>

      <div style="margin-top:16px;font-size:13px;line-height:1.6;color:#6b7280;">
        %s
      </div>

      <div style="margin-top:18px;">
        <a href="#" style="display:inline-block;background:#2563eb;color:#fff;text-decoration:none;padding:12px 16px;border-radius:12px;font-weight:800;font-size:14px;">
          Open the platform
        </a>
      </div>
    </div>

    <div style="text-align:center;color:#9ca3af;font-size:12px;margin-top:16px;">
      ISAMM Library • This is an automatic message, please do not reply.
    </div>
  </div>
</body>
</html>
""".formatted(escapeHtml(headerTitle), escapeHtml(hello), escapeHtml(subtitle), rowsHtml, escapeHtml(note));
    }

    private String escapeHtml(String input) {
        if (input == null) return "—";
        return input.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&#39;");
    }

    @Override
    public List<Loan> getAll() {
        return loanRepository.findAll();
    }

    @Override
    public List<Loan> getByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return loanRepository.findByUser(user);
    }

    @Override
    public List<Loan> getByStatus(LoanStatus status) {
        return loanRepository.findByStatus(status);
    }
}
