/*
 * Classname: Manager
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import org.example.wyjatki.AgeException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca rolę menedżera – logiczną nakładkę na encję Employee.
 */
public class Manager {

    private final Employee employee;
    private final List<Employee> managedEmployees;

    /**
     * Tworzy nową instancję menedżera na podstawie istniejącego pracownika.
     *
     * @param employee Pracownik, który staje się menedżerem.
     */
    public Manager(Employee employee) {
        this.employee = employee;
        this.managedEmployees = new ArrayList<>();
    }

    /**
     * Zwraca pracownika, który jest menedżerem.
     *
     * @return Pracownik będący menedżerem.
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Dodaje pracownika do listy pracowników zarządzanych przez menedżera.
     * */
    public void addEmployee(Employee e) {
        if (e != null && !managedEmployees.contains(e)) {
            managedEmployees.add(e);
        }
    }

    /**
     * Usuwa pracownika z listy pracowników zarządzanych przez menedżera.
     */
    public void removeEmployee(Employee e) {
        managedEmployees.remove(e);
    }

    /**
     * Zwraca listę pracowników
     */
    public List<Employee> getManagedEmployees() {
        return managedEmployees;
    }

    /**
     * Aktualizuje imię menedżera.
     */
    public void updateName(String newName) {
        try {
            employee.setName(newName);
        } catch (Exception e) {
            System.err.println("Błąd zmiany imienia: " + e.getMessage());
        }
    }

    /**
     * Aktualizuje nazwisko menedżera.
     */
    public void updateSurname(String newSurname) {
        try {
            employee.setSurname(newSurname);
        } catch (Exception e) {
            System.err.println("Błąd zmiany nazwiska: " + e.getMessage());
        }
    }

    /**
     * Aktualizuje wiek menedżera.
     *
     * @param newAge aktualizuje wiek menedżera
     */
    public void updateAge(int newAge) {
        try {
            employee.setAge(newAge);
        } catch (AgeException e) {
            System.err.println("Błąd zmiany wieku: " + e.getMessage());
        }
    }

    /**
     * Aktualizuje adres menedżera.
     *
     * @param newAddress aktualizuje adres menedżera
     */
    public void updateAddress(Address newAddress) {
        employee.setAddress(newAddress);
    }

    /**
     * Aktualizuje hasło menedżera.
     * @param newPassword
     */
    public void updatePassword(String newPassword) {
        try {
            employee.setPassword(newPassword);
        } catch (PasswordException e) {
            System.err.println("Błąd zmiany hasła: " + e.getMessage());
        }
    }

    /**
     * Aktualizuje stanowisko menedżera.
     *
     * @param newDepartment aktualizuje stanowisko menedżera
     */
    public void updateDepartment(String newDepartment) {
        employee.setPosition(newDepartment);
    }

    /**
     * Aktualizuje wynagrodzenie menedżera.
     *
     * @param newSalary aktualizuje wynagrodzenie menedżera
     */
    public void updateSalary(BigDecimal newSalary) {
        try {
            employee.setSalary(newSalary);
        } catch (SalaryException e) {
            System.err.println("Błąd zmiany wynagrodzenia: " + e.getMessage());
        }
    }

    /**
     * Generuje raport dla zespołu menedżera.
     */
    public void generateTeamReport() {
        System.out.println("Menedżer " + employee.getName() + " generuje raport " +
                "dla " + managedEmployees.size() + " pracowników.");
    }
}