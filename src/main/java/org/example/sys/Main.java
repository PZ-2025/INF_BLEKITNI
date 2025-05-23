/*
 * Classname: Main
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

// Jakub
// klasa typowo do testów aktualnie jest puta

import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class Main {

    public static void main(String[] args) {
        // TODO: Założyć jakiś mail i ustawić hasło aplikacji
        // https://myaccount.google.com/apppasswords?spm=a2ty_o01.29997173.0.0.5d57c9212jdST3
        // zapisałem linka do stworzenia hasła aplikacji
        String toEmailAddress = "";
        String fromEmailAddress = "";
        String password = ""; // UWAGA: Nie przechowuj hasła w kodzie!
        String subject = "test";
        String bodyText = "nie wiem czy to działa";

        try {
            EmailSender.sendEmail(toEmailAddress, fromEmailAddress, password, subject, bodyText);
            System.out.println("Wiadomość została wysłana!");
        } catch (MessagingException e) {
            System.out.println("Nie udało się wysłać wiadomości.");
            e.printStackTrace();
        }
    }
}

