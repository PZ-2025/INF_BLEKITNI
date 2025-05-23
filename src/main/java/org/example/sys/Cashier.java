/*
 * Classname: Cashier
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import java.math.BigDecimal;

/**
 * Klasa reprezentująca rolę kasjera – logiczną nakładkę na encję Employee.
 */
public class Cashier {

    private final Employee employee;

    /**
     * Tworzy nową instancję kasjera na podstawie istniejącego pracownika.
     *
     * @param employee Pracownik, który staje się kasjerem.
     */
    public Cashier(Employee employee) {
        this.employee = employee;
    }

    /**
     * Zwraca pracownika, który jest kasjerem.
     *
     * @return Pracownik będący kasjerem.
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Zwraca informację czy użytkownik jest kasjerem.
     *
     * @return true, jeśli użytkownik jest kasjerem, false w przeciwnym razie.
     */
    public void updatePassword(String newPassword) {
        try {
            employee.setPassword(newPassword);
        } catch (PasswordException e) {
            System.err.println("Błąd zmiany hasła: " + e.getMessage());
        }
    }

    /**
     * Zwraca hasło kasjera.
     *
     * @return Hasło kasjera.
     */
    public void updateSalary(BigDecimal newSalary) {
        try {
            employee.setSalary(newSalary);
        } catch (SalaryException e) {
            System.err.println("Błąd zmiany wynagrodzenia: " + e.getMessage());
        }
    }

    /**
     * Zwraca wynagrodzenie kasjera.
     *
     * @return Wynagrodzenie kasjera.
     */
    public void scanProduct(String productName) {
        System.out.println("Kasjer " + employee.getName() + " zeskanował produkt: " + productName);
    }

    /**
     * Zwraca informację czy użytkownik jest kasjerem.
     *
     * @return true, jeśli użytkownik jest kasjerem, false w przeciwnym razie.
     */
    public void endTransaction() {
        System.out.println("Kasjer " + employee.getName() + " zakończył transakcję.");
    }
}