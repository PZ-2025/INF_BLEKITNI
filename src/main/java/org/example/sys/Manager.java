/*
 * Classname: Manager
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
 * Klasa reprezentująca rolę menedżera – logiczną nakładkę na encję Employee.
 */
public class Manager {

    private final Employee employee;
    private final List<Employee> managedEmployees;

    public Manager(Employee employee) {
        this.employee = employee;
        this.managedEmployees = new ArrayList<>();
    }

    public Employee getEmployee() {
        return employee;
    }

    public void addEmployee(Employee e) {
        if (e != null && !managedEmployees.contains(e)) {
            managedEmployees.add(e);
        }
    }

    public void removeEmployee(Employee e) {
        managedEmployees.remove(e);
    }

    public List<Employee> getManagedEmployees() {
        return managedEmployees;
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
            System.err.println("Błąd zmiany wynagrodzenia: " + e.getMessage());
        }
    }

    public void wygenerujRaportZespolu() {
        System.out.println("Menedżer " + employee.getName() + " generuje raport dla " + managedEmployees.size() + " pracowników.");
    }
}
