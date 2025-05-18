/*
 * Classname: Admin
 * Version information: 1.0
 * Date: 2025-05-16
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

    public Admin(Employee employee) {
        this.employee = employee;
        this.allEmployees = new ArrayList<>();
    }

    public Employee getEmployee() {
        return employee;
    }

    public boolean isAdmin() {
        return true;
    }

    public void addEmployee(Employee e) {
        if (e != null && !allEmployees.contains(e)) {
            allEmployees.add(e);
        }
    }

    public void removeEmployee(Employee e) {
        if (e != null && "root".equalsIgnoreCase(e.getStanowisko())) {
            System.err.println("Próba usunięcia użytkownika z rolą root została zablokowana");
            return;
        }
        allEmployees.remove(e);
    }

    public List<Employee> getAllEmployees() {
        return allEmployees;
    }

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

    public void updateAddress(Address address) {
        employee.setAdres(address);
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
            System.err.println("Błąd zmiany wynagrodzenia: " + e.getMessage());
        }
    }

    public void resetSystemSettings() {
        System.out.println("Administrator " + employee.getLogin() + " resetuje ustawienia systemowe.");
    }

    public void generateFullSystemReport() {
        System.out.println("Administrator " + employee.getLogin() + " generuje pełny raport systemowy (łącznie: "
                + allEmployees.size() + " pracowników).");
    }
}
