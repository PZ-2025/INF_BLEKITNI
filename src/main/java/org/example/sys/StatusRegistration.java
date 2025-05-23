/*
 * Enum: StatusRegistration
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

/**
 * Klasa reprezentująca różne statusy rejestracji w systemie.
 * Umożliwia wybór statusu na podstawie nazwy wyświetlanej.
 */
public enum StatusRegistration {
    PENDING("Oczekujący"),
    ACCEPTED("Zaakceptowany"),
    REJECTED("Odrzucony"),
    COMPLETED("Zrealizowany");
    private final String displayName;

    /**
     * Konstruktor enum.
     *
     * @param displayName Nazwa wyświetlana
     */
    StatusRegistration(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Zwraca nazwę wyświetlaną dla danego statusu.
     *
     * @return Nazwa wyświetlana
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Nadpisana metoda toString() do zwracania nazwy wyświetlanej.
     * @return Status rejestracji w formie tekstowej
     */
    @Override
    public String toString() {
        return displayName;
    }
}
