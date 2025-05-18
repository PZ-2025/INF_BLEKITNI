package org.example.sys;

public class EmailValidator {
    public static boolean isValid(String email) {
        // Sprawdzenie, czy adres e-mail zawiera znak '@'
        if (email == null || !email.contains("@")) {
            return false;
        }

        // Podział adresu e-mail na lokalną część i domenę
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false;
        }

        String localPart = parts[0];
        String domainPart = parts[1];

        // Sprawdzenie długości lokalnej części
        if (localPart.length() < 1 || localPart.length() > 64) {
            return false;
        }

        // Sprawdzenie długości domeny
        if (domainPart.length() < 3 || domainPart.length() > 253) {
            return false;
        }

        // Sprawdzenie, czy domena zawiera co najmniej jedną kropkę
        if (!domainPart.contains(".")) {
            return false;
        }

        // Sprawdzenie, czy domena nie zaczyna się ani nie kończy kropką
        if (domainPart.startsWith(".") || domainPart.endsWith(".")) {
            return false;
        }

        // Sprawdzenie, czy lokalna część nie zawiera niedozwolonych znaków
        String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._%+-";
        for (char c : localPart.toCharArray()) {
            if (allowedChars.indexOf(c) == -1) {
                return false;
            }
        }

        return true;
    }
}
