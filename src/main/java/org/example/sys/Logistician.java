/*
 * Classname: Logistician
 * Version information: 1.0
 * Date: 2025-05-16
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

    public void updateName(String newName) {
        try {
            employee.setName(newName);
        } catch (Exception e) {
            System.err.println("Błąd zmiany imienia: " + e.getMessage());
        }
    }

    public void updateSurname(String newSurname) {
        try {
            employee.setSurname(newSurname);
        } catch (Exception e) {
            System.err.println("Błąd zmiany nazwiska: " + e.getMessage());
        }
    }

    public void updateAge(int newAge) {
        try {
            employee.setAge(newAge);
        } catch (AgeException e) {
            System.err.println("Błąd zmiany wieku: " + e.getMessage());
        }
    }

    public void updateAddress(Address newAddress) {
        employee.setAdres(newAddress);
    }

    public void updatePassword(String newPassword) {
        try {
            employee.setPassword(newPassword);
        } catch (PasswordException e) {
            System.err.println("Błąd zmiany hasła: " + e.getMessage());
        }
    }

    public void updateDepartment(String newDepartment) {
        employee.setStanowisko(newDepartment);
    }

    public void updateSalary(BigDecimal newSalary) {
        try {
            employee.setZarobki(newSalary);
        } catch (SalaryException e) {
            System.err.println("Błąd zmiany zarobków: " + e.getMessage());
        }
    }

    // === Przykładowa metoda logistyczna ===

    public void przydzielZamowienie(int orderId) {
        // tutaj można dodać logikę przydziału zamówienia
        System.out.println("Logistyk " + employee.getName() + " przydzielił zamówienie o ID: " + orderId);
    }
}
