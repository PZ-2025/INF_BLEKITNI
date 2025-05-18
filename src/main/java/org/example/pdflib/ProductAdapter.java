/*
 * Classname: ProductAdapter
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.pdflib;

import sys.Product;

/**
 * Klasa adaptera konwertująca obiekt Product z głównego projektu
 * na obiekt Product używany przez bibliotekę do generowania PDF.
 */
public class ProductAdapter {

    /**
     * Konwertuje obiekt Product z głównego projektu na obiekt Product
     * używany przez bibliotekę PDF.
     *
     * @param sourceProduct obiekt Product z głównego projektu
     * @return obiekt Product kompatybilny z biblioteką PDF
     */
    public static sys.Product toPdfProduct(Product sourceProduct) {
        if (sourceProduct == null) {
            return null;
        }
        return new sys.Product(
                sourceProduct.getName(),
                sourceProduct.getCategory(),
                sourceProduct.getPrice(),
                sourceProduct.getQuantity()
        );
    }

    /**
     * Konwertuje listę obiektów Product z głównego projektu na listę obiektów
     * Product używanych przez bibliotekę PDF.
     *
     * @param sourceProducts lista obiektów Product z głównego projektu
     * @return lista obiektów Product kompatybilnych z biblioteką PDF
     */
    public static java.util.List<sys.Product> toPdfProducts(java.util.List<Product> sourceProducts) {
        if (sourceProducts == null) {
            return null;
        }

        java.util.List<sys.Product> pdfProducts = new java.util.ArrayList<>(sourceProducts.size());
        for (Product sourceProduct : sourceProducts) {
            pdfProducts.add(toPdfProduct(sourceProduct));
        }

        return pdfProducts;
    }

    /**
     * Konwertuje obiekt Product z biblioteki PDF z powrotem na obiekt Product
     * z głównego projektu.
     *
     * @param pdfProduct obiekt Product z biblioteki PDF
     * @return obiekt Product z głównego projektu
     */
    public static Product fromPdfProduct(sys.Product pdfProduct) {
        if (pdfProduct == null) {
            return null;
        }

        return new Product(
                pdfProduct.getName(),
                pdfProduct.getCategory(),
                pdfProduct.getPrice(),
                pdfProduct.getQuantity()
        );
    }

    /**
     * Konwertuje listę obiektów Product z biblioteki PDF na listę obiektów
     * Product z głównego projektu.
     *
     * @param pdfProducts lista obiektów Product z biblioteki PDF
     * @return lista obiektów Product z głównego projektu
     */
    public static java.util.List<Product> fromPdfProducts(java.util.List<sys.Product> pdfProducts) {
        if (pdfProducts == null) {
            return null;
        }

        java.util.List<Product> sourceProducts = new java.util.ArrayList<>(pdfProducts.size());
        for (sys.Product pdfProduct : pdfProducts) {
            sourceProducts.add(fromPdfProduct(pdfProduct));
        }

        return sourceProducts;
    }
}