package org.example.pdflib;

/** Rzucane, gdy brak danych do wygenerowania raportu. */
public class NoDataException extends Exception {
  public NoDataException(String msg) {
    super(msg);
  }
}
