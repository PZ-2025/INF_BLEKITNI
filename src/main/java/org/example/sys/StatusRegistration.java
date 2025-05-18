/*
 * Enum: StatusRegistration
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

public enum StatusRegistration {
    OCZEKUJACY("Oczekujący"),
    ZAAKCEPTOWANY("Zaakceptowany"),
    ODRZUCONY("Odrzucony"),
    ZREALIZOWANY("Zrealizowany");

    private final String displayName;

    StatusRegistration(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
