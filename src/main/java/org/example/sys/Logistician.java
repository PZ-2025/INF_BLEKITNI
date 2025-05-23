/*
 * Classname: Logistician
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.example.wyjatki.AgeException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

import java.math.BigDecimal;

/**
 * Klasa reprezentująca rolę logistyka – logiczną "nakładkę" na encję Employee.
 */
public class Logistician {

    private final Employee employee;

    /**
     * Konstruktor przyjmujący istniejącego pracownika.
     */
    public Logistician(Employee employee) {
        this.employee = employee;
    }

    /**
     * Zwraca referencję do powiązanego pracownika.
     */
    public Employee getEmployee() {
        return employee;
    }

    // === Metody operujące na danych pracownika ===

    /**
     * Metoda aktualizująca imię logistyka.
     */
    public void updateName(String newName) {
        try {
            employee.setName(newName);
        } catch (Exception e) {
            System.err.println("Błąd zmiany imienia: " + e.getMessage());
        }
    }

    /**
     * Metoda aktualizująca nazwisko logistyka.
     */
    public void updateSurname(String newSurname) {
        try {
            employee.setSurname(newSurname);
        } catch (Exception e) {
            System.err.println("Błąd zmiany nazwiska: " + e.getMessage());
        }
    }

    /**
     * Metoda aktualizująca wiek logistyka.
     */
    public void updateAge(int newAge) {
        try {
            employee.setAge(newAge);
        } catch (AgeException e) {
            System.err.println("Błąd zmiany wieku: " + e.getMessage());
        }
    }

    /**
     * Metoda aktualizująca adres logistyka.
     */
    public void updateAddress(Address newAddress) {
        employee.setAddress(newAddress);
    }

    /**
     * Metoda aktualizująca hasło logistyka.
     */
    public void updatePassword(String newPassword) {
        try {
            employee.setPassword(newPassword);
        } catch (PasswordException e) {
            System.err.println("Błąd zmiany hasła: " + e.getMessage());
        }
    }

    /**
     * Metoda aktualizująca stanowisko logistyka.
     */
    public void updateDepartment(String newDepartment) {
        employee.setPosition(newDepartment);
    }

    /**
     * Metoda aktualizująca zarobki logistyka.
     */
    public void updateSalary(BigDecimal newSalary) {
        try {
            employee.setSalary(newSalary);
        } catch (SalaryException e) {
            System.err.println("Błąd zmiany zarobków: " + e.getMessage());
        }
    }

    // === Przykładowa metoda logistyczna ===

    /**
     * Metoda przydzielająca zamówienie do realizacji.
     *
     * @param orderId ID zamówienia do przydzielenia
     */
    public void assignOrder(int orderId) {
        // tutaj można dodać logikę przydziału zamówienia
        System.out.println("Logistyk " + employee.getName() + " przydzielił zam" +
                "ówienie o ID: " + orderId);
    }
}