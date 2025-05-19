// src/main/java/org/example/sys/PeriodType.java
package org.example.sys;

public enum PeriodType {
    DAILY  ("Dzienny"),
    MONTHLY("Miesięczny"),
    YEARLY ("Roczny");

    private final String displayName;
    PeriodType(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }

    /** konwersja z nazwy w ComboBox-ie */
    public static PeriodType fromDisplay(String txt) {
        for (PeriodType p : values())
            if (p.displayName.equalsIgnoreCase(txt)) return p;
        throw new IllegalArgumentException("Nieznany typ: " + txt);
    }
}

