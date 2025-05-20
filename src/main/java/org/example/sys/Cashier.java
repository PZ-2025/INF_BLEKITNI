package org.example.sys;

import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import java.math.BigDecimal;

/**
 * Klasa reprezentująca rolę kasjera – logiczną nakładkę na encję Employee.
 */
public class Cashier {

    private final Employee employee;

    public Cashier(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void updatePassword(String newPassword) {
        try {
            employee.setPassword(newPassword);
        } catch (PasswordException e) {
            System.err.println("Błąd zmiany hasła: " + e.getMessage());
        }
    }

    public void updateSalary(BigDecimal newSalary) {
        try {
            employee.setZarobki(newSalary);
        } catch (SalaryException e) {
            System.err.println("Błąd zmiany wynagrodzenia: " + e.getMessage());
        }
    }

    public void zeskanujProdukt(String productName) {
        System.out.println("Kasjer " + employee.getName() + " zeskanował produkt: " + productName);
    }

    public void zakonczTransakcje() {
        System.out.println("Kasjer " + employee.getName() + " zakończył transakcję.");
    }
}
