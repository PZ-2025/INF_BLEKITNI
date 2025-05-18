/*
 * Enum: Sort
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

public enum Sort {
    DEFAULT("Domyślne", "DEFAULT"),
    NAME("Nazwa", "NAME"),
    DATE("Data", "DATE"),
    PRIORITY("Priorytet", "PRIORITY");

    private final String displayName;
    private final String value;

    Sort(String displayName, String value) {
        this.displayName = displayName;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Sort fromDisplayName(String displayName) {
        for (Sort sort : values()) {
            if (sort.displayName.equalsIgnoreCase(displayName)) {
                return sort;
            }
        }
        return DEFAULT;
    }
}