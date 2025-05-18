/*
 * Classname: Registration
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import java.time.LocalDate;

public class Registration {

    private String message;
    private String name;
    private String surname;
    private String title;
    private LocalDate date;
    private StatusRegistration status;

    public Registration(String message, String name, String surname, LocalDate date, String title) {
        this(message, name, surname, date, title, StatusRegistration.OCZEKUJACY);
    }

    public Registration(String message, String name, String surname, LocalDate date, String title, StatusRegistration status) {
        this.message = message;
        this.name = name;
        this.surname = surname;
        this.title = title;
        this.date = date;
        this.status = status;
    }

    public Registration(String message, LocalDate date) {
        this.message = message;
        this.date = date;
        this.status = StatusRegistration.OCZEKUJACY;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getDate() {
        return date;
    }

    public StatusRegistration getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStatus(StatusRegistration status) {
        this.status = status;
    }

    public void accept() {
        this.status = StatusRegistration.ZAAKCEPTOWANY;
    }

    public void reject() {
        this.status = StatusRegistration.ODRZUCONY;
    }

    public void realize() {
        this.status = StatusRegistration.ZREALIZOWANY;
    }

    @Override
    public String toString() {
        return String.format("Registration{name='%s %s', title='%s', date=%s, status=%s}",
                name, surname, title, date, status);
    }
}
