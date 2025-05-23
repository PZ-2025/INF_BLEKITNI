/*
 * Classname: Registration
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import java.time.LocalDate;

/**
 * Klasa reprezentująca rejestrację w systemie.
 * Zawiera informacje o wiadomości, imieniu, nazwisku, tytule,
 * dacie oraz statusie rejestracji.
 */
public class Registration {

    private String message;
    private String name;
    private String surname;
    private String title;
    private LocalDate date;
    private StatusRegistration status;

    /**
     * Domyślny konstruktor.
     * Używany przez Hibernate do tworzenia instancji klasy.
     */
    public Registration(String message, String name, String surname, LocalDate date, String title) {
        this(message, name, surname, date, title, StatusRegistration.PENDING);
    }

    /**
     * Konstruktor z parametrami.
     * Umożliwia ustawienie wiadomości, imienia, nazwiska, tytułu,
     * daty oraz statusu rejestracji.
     *
     * @param message  Wiadomość
     * @param name     Imię
     * @param surname  Nazwisko
     * @param date     Data
     * @param title    Tytuł
     * @param status   Status rejestracji
     */
    public Registration(String message, String name, String surname, LocalDate date, String title, StatusRegistration status) {
        this.message = message;
        this.name = name;
        this.surname = surname;
        this.title = title;
        this.date = date;
        this.status = status;
    }

    /**
     * Konstruktor z parametrami.
     * Umożliwia ustawienie wiadomości oraz daty rejestracji.
     *
     * @param message Wiadomość
     * @param date    Data
     */
    public Registration(String message, LocalDate date) {
        this.message = message;
        this.date = date;
        this.status = StatusRegistration.PENDING;
    }

    /**
     * Domyślny konstruktor.
     * Używany przez Hibernate do tworzenia instancji klasy.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Ustawia tytuł rejestracji.
     *
     * @return title Tytuł rejestracji
     */
    public String getTitle() {
        return title;
    }

    /**
     * Ustawia imię rejestracji.
     *
     * @return name Imię rejestracji
     */
    public String getName() {
        return name;
    }

    /**
     * Ustawia nazwisko rejestracji.
     *
     * @return surname Nazwisko rejestracji
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Ustawia datę rejestracji.
     *
     * @return date Data rejestracji
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Ustawia status rejestracji.
     *
     * @return status Status rejestracji
     */
    public StatusRegistration getStatus() {
        return status;
    }

    /**
     * Ustawia wiadomość rejestracji.
     *
     * @param message Wiadomość rejestracji
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Ustawia tytuł rejestracji.
     *
     * @param title Tytuł rejestracji
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Ustawia imię rejestracji.
     *
     * @param name Imię rejestracji
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Ustawia nazwisko rejestracji.
     *
     * @param surname Nazwisko rejestracji
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Ustawia datę rejestracji.
     *
     * @param date Data rejestracji
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Ustawia status rejestracji.
     *
     * @param status Status rejestracji
     */
    public void setStatus(StatusRegistration status) {
        this.status = status;
    }

    /**
     * Ustawia status rejestracji na zaakceptowany.
     */
    public void accept() {
        this.status = StatusRegistration.ACCEPTED;
    }

    /**
     * Ustawia status rejestracji na odrzucony.
     */
    public void reject() {
        this.status = StatusRegistration.REJECTED;
    }

    /**
     * Ustawia status rejestracji na zrealizowany.
     */
    public void realize() {
        this.status = StatusRegistration.COMPLETED;
    }

    /**
     * Ustawia status rejestracji na oczekujący.
     */
    @Override
    public String toString() {
        return String.format("Registration{name='%s %s', title='%s', date=%s, " +
                        "status=%s}",
                name, surname, title, date, status);
    }
}
