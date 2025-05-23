/*
 * Classname: ConfigPdf
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

/**
 * Klasa ConfigPdf przechowuje konfigurację dla generowania plików PDF.
 * Zawiera ścieżki do logo i pliku PDF oraz informacje o sortowaniu.
 */

public class ConfigPdf {
    private static String pathLogo = "src/main/resources/logo.png";
    private static String pathPdf = "src/main/resources/raport.pdf";
    private static Sort sort = Sort.DEFAULT;

    /**
     * Ustawia wszystkie parametry konfiguracyjne.
     *
     * @param pathLogo Ścieżka do logo.
     * @param pathPdf  Ścieżka do pliku PDF.
     * @param sort     Obiekt Sort definiujący sposób sortowania.
     */
    public void setAll(String pathLogo, String pathPdf, Sort sort) {
        ConfigPdf.pathLogo = pathLogo;
        ConfigPdf.pathPdf = pathPdf;
        ConfigPdf.sort = sort;
    }

    /**
     * Ustawia domyślne parametry konfiguracyjne.
     */
    public static String getPathLogo() {
        return ConfigPdf.pathLogo;
    }

    /**
     * Ustawia ścieżkę do logo.
     *
     * @param pathLogo Ścieżka do logo.
     */
    public static void setPathLogo(String pathLogo) {
        ConfigPdf.pathLogo = pathLogo;
    }

    /**
     * Zwraca ścieżkę do pliku PDF.
     *
     * @return zwraca ścieżkę do pliku PDF.
     */
    public static String getPathPdf() {
        return ConfigPdf.pathPdf;
    }

    /**
     * Ustawia ścieżkę do pliku PDF.
     *
     * @param pathPdf Ścieżka do pliku PDF.
     */
    public static void setPathPdf(String pathPdf) {
        ConfigPdf.pathPdf = pathPdf;
    }

    /**
     * Zwraca obiekt Sort definiujący sposób sortowania.
     *
     * @return Obiekt Sort definiujący sposób sortowania.
     */
    public static Sort getSort() {
        return ConfigPdf.sort;
    }

    /**
     * Ustawia obiekt Sort definiujący sposób sortowania.
     *
     * @param sort Obiekt Sort definiujący sposób sortowania.
     */
    public static void setSort(Sort sort) {
        ConfigPdf.sort = sort;
    }

}