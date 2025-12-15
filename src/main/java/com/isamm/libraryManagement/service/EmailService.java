package com.isamm.libraryManagement.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }

    public void sendDisponibilite(String to, String titre) {
        send(to, "Ressource disponible",
                "Bonjour,\n\nVotre ressource \"" + titre + "\" est prête à être empruntée.\n\nCordialement.");
    }

    public void sendRappelRetour(String to, String titre, String dateRetourPrevu) {
        send(to, "Rappel de retour",
                "Bonjour,\n\nRappel : la ressource \"" + titre + "\" doit être retournée avant le " + dateRetourPrevu + ".\n\nCordialement.");
    }

    public void sendRetardToStaff(String to, String details) {
        send(to, "Retard détecté", details);
    }
}
