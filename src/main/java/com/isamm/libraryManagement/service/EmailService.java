package com.isamm.libraryManagement.service;

public interface EmailService {
    void sendHtml(String to, String subject, String htmlBody);
    
}
