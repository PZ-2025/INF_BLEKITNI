/*
 * Enum : PeriodType
 * Version information: 1.0
 * Date: 2025-05-20
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

/**
 * Typ okresu raportowania.
 * Używany w systemie do określenia, jak często generowane są raporty.
 */
public enum PeriodType {
    DAILY  ("Dzienny"),
    MONTHLY("Miesięczny"),
    YEARLY ("Roczny");

    private final String displayName;

    /**
     * Konstruktor enum.
     * @param displayName
     */
    PeriodType(String displayName) { this.displayName = displayName; }

    /**
     * Zwraca nazwę wyświetlaną dla danego typu okresu.
     * @return Nazwa wyświetlana
     */
    public String getDisplayName() { return displayName; }

    /**
     * Zwraca typ okresu na podstawie nazwy wyświetlanej.
     * @param txt Nazwa wyświetlana
     * @return Typ okresu
     * @throws IllegalArgumentException Jeśli typ nie istnieje
     */
    public static PeriodType fromDisplay(String txt) {
        for (PeriodType p : values())
            if (p.displayName.equalsIgnoreCase(txt)) return p;
        throw new IllegalArgumentException("Nieznany typ: " + txt);
    }
}

