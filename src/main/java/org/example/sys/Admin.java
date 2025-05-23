/*
 * Classname: Admin
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
 * Klasa reprezentująca rolę administratora systemu – logiczną nakładkę na encję Employee.
 */
public class Admin {

    private final Employee employee;
    private final List<Employee> allEmployees;

    /**
     * Tworzy nową instancję administratora na podstawie istniejącego pracownika.
     *
     * @param employee Pracownik, który staje się administratorem.
     */
    public Admin(Employee employee) {
        this.employee = employee;
        this.allEmployees = new ArrayList<>();
    }

    /**
     * Zwraca pracownika, który jest administratorem.
     *
     * @return Pracownik będący administratorem.
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Zwraca ingfrmację czy użytkownik jest administratorem.
     *
     * @return true, jeśli użytkownik jest administratorem, false w przeciwnym razie.
     */
    public boolean isAdmin() {
        return true;
    }

    /**
     * Zwraca hasło administratora.
     *
     * @return Hasło administratora.
     */
    public void addEmployee(Employee e) {
        if (e != null && !allEmployees.contains(e)) {
            allEmployees.add(e);
        }
    }

    /**
     * Usuwa pracownika z listy wszystkich pracowników.
     */
    public void removeEmployee(Employee e) {
        if (e != null && "root".equalsIgnoreCase(e.getPosition())) {
            System.err.println("Próba usunięcia użytkownika z rolą root została zablokowana");
            return;
        }
        allEmployees.remove(e);
    }

    /**
     * Zwraca listę wszystkich pracowników.
     *
     * @return Lista wszystkich pracowników.
     */
    public List<Employee> getAllEmployees() {
        return allEmployees;
    }

    /**
     * Zwraca hasło administratora.
     *
     * @return Hasło administratora.
     */
    public void updateName(String newName) {
        try {
            employee.setName(newName);
        } catch (Exception e) {
            System.err.println("Błąd zmiany imienia: " + e.getMessage());
        }
    }

    /**
     * Zwraca hasło administratora.
     *
     * @return Hasło administratora.
     */
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

    /**
     * Zwraca hasło administratora.
     *
     * @return Hasło administratora.
     */
    public void updateAddress(Address address) {
        employee.setAddress(address);
    }

    /**
     * Aktualizuje hasło.
     */
    public void updatePassword(String newPassword) {
        try {
            employee.setPassword(newPassword);
        } catch (PasswordException e) {
            System.err.println("Błąd zmiany hasła: " + e.getMessage());
        }
    }

    /**
     * Aktualizuje stanowisko pracownika.
     */
    public void updateDepartment(String newDepartment) {
        employee.setPosition(newDepartment);
    }

    /**
     * Aktualizuje wynagrodzenie pracownika.
     */
    public void updateSalary(BigDecimal newSalary) {
        try {
            employee.setSalary(newSalary);
        } catch (SalaryException e) {
            System.err.println("Błąd zmiany wynagrodzenia: " + e.getMessage());
        }
    }

    /**
     * Rresetuje ustawienia systemowe
     */
    public void resetSystemSettings() {
        System.out.println("Administrator " + employee.getLogin() + " resetuje ustawienia systemowe.");
    }

    /**
     * Generuje pełny raport systemowy.
     */
    public void generateFullSystemReport() {
        System.out.println("Administrator " + employee.getLogin() + " generuje pełny raport systemowy (łącznie: "
                + allEmployees.size() + " pracowników).");
    }
}
