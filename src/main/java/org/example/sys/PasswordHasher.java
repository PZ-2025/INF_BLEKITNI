/*
 * Classname: PasswordHasher
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    public static String hashPassword(String password, int userId)
            throws NoSuchAlgorithmException, InvalidKeyException {

        // Konwersja ID użytkownika na bajty (klucz HMAC)
        byte[] keyBytes = String.valueOf(userId).getBytes(StandardCharsets.UTF_8);
        SecretKeySpec key = new SecretKeySpec(keyBytes, HMAC_ALGORITHM);

        // Tworzenie instancji MAC
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(key);

        // Obliczanie HMAC dla hasła
        byte[] hmacBytes = mac.doFinal(password.getBytes(StandardCharsets.UTF_8));

        // Konwersja wyniku na heksadecymalny string
        return bytesToHex(hmacBytes);
    }

    public static boolean verifyPassword(String storedHash, String password, int userId)
            throws NoSuchAlgorithmException, InvalidKeyException {

        // Obliczamy nowy hash i porównujemy bezpiecznie
        String newHash = hashPassword(password, userId);
        return MessageDigest.isEqual(
                newHash.getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Pomocnicza metoda do konwersji bajtów na hex
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Przykład użycia
    public static void main(String[] args) {
        try {
            int userId = 123;
            String password = "moje_tajne_haslo";

            // Hashowanie
            String hash = hashPassword(password, userId);
            System.out.println("Hasło: " + password);
            System.out.println("Hash: " + hash);

            // Weryfikacja
            boolean isValid = verifyPassword(hash, password, userId);
            System.out.println("Hasło poprawne: " + isValid);  // true

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}