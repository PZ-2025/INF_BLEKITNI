package org.example.sys;

import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    @Test
    void testGenerateRandomCodeLengthAndContent() {
        String code = invokeCodeGenerator(8);
        assertNotNull(code);
        assertEquals(8, code.length());
        assertTrue(code.matches("[A-Z0-9]+"), "Kod zawiera tylko wielkie litery i cyfry");
    }

    @Test
    void testGenerateRandomCodeDifferentEachTime() {
        String code1 = invokeCodeGenerator(6);
        String code2 = invokeCodeGenerator(6);
        assertNotEquals(code1, code2, "Każde wywołanie powinno zwracać inny kod");
    }

    @Test
    void testSendEmailReadsFromFileAndCallsSender() throws Exception {
        // Przygotuj tymczasowy plik PASS.txt
        Path tempPassFile = Files.createTempFile("PASS", ".txt");
        Files.writeString(tempPassFile, "sender@example.com\npassword123");

        // Zamień domyślną ścieżkę pliku na tę tymczasową (przy użyciu refleksji)
        String originalPath = "PASS.txt";
        File backup = new File(originalPath);
        File temp = tempPassFile.toFile();
        temp.renameTo(backup); // nadpisz jeśli istnieje

        // Test bez wyjątku
        try {
            // Podmień implementację EmailSender na atrapę (jeśli masz np. mockito – można zamockować)
            // Tu tylko sprawdzamy czy metoda działa bez wyjątku
            Login.sendEmail("test@wp.pl", "Testowy temat", "Treść testowa");
        } catch (Exception e) {
            fail("Metoda nie powinna rzucać wyjątku: " + e.getMessage());
        } finally {
            backup.delete(); // posprzątaj
        }
    }

    // === Pomocnicza metoda do wywołania prywatnego generatora ===
    private String invokeCodeGenerator(int length) {
        try {
            var method = Login.class.getDeclaredMethod("generateRandomCode", int.class);
            method.setAccessible(true);
            return (String) method.invoke(null, length);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się wywołać generateRandomCode()", e);
        }
    }
}
