package com.isamm.libraryManagement.service;

public interface EmailService {

    void sendEmail(String to, String subject, String content);
}
