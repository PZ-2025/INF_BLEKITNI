/*
 * Classname: EmailSender
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

/**
 * Klasa do wysyłania wiadomości e-mail.
 * Używa protokołu SMTP do wysyłania wiadomości.
 */
public class EmailSender {

    /**
     * Tworzy i wysyła wiadomość e-mail.
     *
     * @param toEmailAddress   adres e-mail odbiorcy
     * @param fromEmailAddress adres e-mail nadawcy
     * @param password         hasło do konta nadawcy
     * @param subject          temat wiadomości
     * @param bodyText         treść wiadomości
     * @throws MessagingException jeśli wystąpi błąd podczas tworzenia lub wysyłania wiadomości
     */
    public static void sendEmail(String toEmailAddress,
                                 String fromEmailAddress,
                                 String password,
                                 String subject,
                                 String bodyText)
            throws MessagingException {
        // Konfiguracja serwera SMTP (Gmail)
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Utworzenie sesji
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmailAddress, password);
            }
        });

        // Utworzenie wiadomości
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmailAddress));
        email.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmailAddress));
        email.setSubject(subject);
        email.setText(bodyText);

        // Wysłanie wiadomości
        Transport.send(email);
    }

    /**
     * Wysyła wiadomość e-mail z kodem resetowania hasła.
     *
     * @param email     adres e-mail odbiorcy
     * @param resetCode kod resetowania hasła
     */
    public static void sendResetEmail(String email, String resetCode) {
    }
}