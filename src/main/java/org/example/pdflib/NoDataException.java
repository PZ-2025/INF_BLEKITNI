package org.example.pdflib;

/**
 * Rzucany, gdy nie ma danych do wygenerowania raportu.
 */
public class NoDataException extends Exception {
    public NoDataException(String message) {
        super(message);
    }
}
