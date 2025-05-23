/*
 * Enum: Sort
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

/**
 * Klasa reprezentująca różne sposoby sortowania danych w systemie.
 * Umożliwia wybór sposobu sortowania na podstawie nazwy wyświetlanej.
 */
public enum Sort {
    DEFAULT("Domyślne", "DEFAULT"),
    NAME("Nazwa", "NAME"),
    DATE("Data", "DATE"),
    PRIORITY("Priorytet", "PRIORITY");

    private final String displayName;
    private final String value;

    /**
     * Konstruktor enum.
     *
     * @param displayName Nazwa wyświetlana
     * @param value       Wartość sortowania
     */
    Sort(String displayName, String value) {
        this.displayName = displayName;
        this.value = value;
    }

    /**
     * Zwraca wartość sortowania.
     *
     * @return Wartość sortowania
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Zwraca wartość sortowania.
     *
     * @return Wartość sortowania
     */
    public static Sort fromDisplayName(String displayName) {
        for (Sort sort : values()) {
            if (sort.displayName.equalsIgnoreCase(displayName)) {
                return sort;
            }
        }
        return DEFAULT;
    }
}