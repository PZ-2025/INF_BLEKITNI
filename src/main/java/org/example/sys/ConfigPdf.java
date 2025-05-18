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

    public void setAll(String pathLogo, String pathPdf, Sort sort) {
        ConfigPdf.pathLogo = pathLogo;
        ConfigPdf.pathPdf = pathPdf;
        ConfigPdf.sort = sort;
    }

    public static String getPathLogo() {
        return ConfigPdf.pathLogo;
    }
    public static void setPathLogo(String pathLogo) {
        ConfigPdf.pathLogo = pathLogo;
    }
    public static String getPathPdf() {
        return ConfigPdf.pathPdf;
    }
    public static void setPathPdf(String pathPdf) {
        ConfigPdf.pathPdf = pathPdf;
    }
    public static Sort getSort() {
        return ConfigPdf.sort;
    }
    public static void setSort(Sort sort) {
        ConfigPdf.sort = sort;
    }

}
