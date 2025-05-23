/*
 * Classname: Person
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.example.wyjatki.AgeException;
import org.example.wyjatki.NameException;

/**
 * Klasa bazowa reprezentująca osobę w systemie.
 * Zawiera podstawowe informacje o osobie, takie jak imię, nazwisko, wiek i adres e-mail.
 * Klasa jest mapowana do bazy danych jako klasa nadrzędna.
 */
@MappedSuperclass
public abstract class Person {

    @Column(name = "Imie")
    private String name;

    @Column(name = "Nazwisko")
    private String surname;

    @Column(name = "Wiek")
    private int age;

    @Column(name = "Email")
    private String email;
    private boolean usuniety;

    /**
     * Domyślny konstruktor.
     * Używany przez Hibernate do tworzenia instancji klasy.
     */
    public Person() {
    }

    /**
     * Konstruktor z parametrami.
     * Umożliwia ustawienie imienia, nazwiska, wieku i adresu e-mail osoby.
     *
     * @param name    Imię osoby
     * @param surname Nazwisko osoby
     * @param age     Wiek osoby
     * @param email   Adres e-mail osoby
     * @throws AgeException  Jeśli wiek jest nieprawidłowy
     * @throws NameException Jeśli imię lub nazwisko są nieprawidłowe
     */
    public Person(String name, String surname, int age, String email)
            throws AgeException, NameException {
        setName(name);
        setSurname(surname);
        setAge(age);
        this.email = email;
    }

    /**
     * Metoda zwracająca imię osoby.
     *
     * @return Imię osoby
     */
    public String getName() {
        return name;
    }

    /**
     * Metoda ustawiająca imię osoby.
     *
     * @param name Imię do ustawienia
     * @throws NameException Jeśli imię jest nieprawidłowe
     */
    public void setName(String name) throws NameException {
        if (name == null || name.isEmpty()) {
            throw new NameException("Imię nie może być puste");
        } else if (name.length() < 2) {
            throw new NameException("Imię musi mieć co najmniej 2 znaki");
        }
        this.name = name;
    }

    /**
     * Metoda zwracająca nazwisko osoby.
     *
     * @return Nazwisko osoby
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Metoda ustawiająca nazwisko osoby.
     *
     * @param surname Nazwisko do ustawienia
     * @throws NameException Jeśli nazwisko jest nieprawidłowe
     */
    public void setSurname(String surname) throws NameException {
        if (surname == null || surname.isEmpty()) {
            throw new NameException("Nazwisko nie może być puste");
        } else if (surname.length() < 2) {
            throw new NameException("Nazwisko musi mieć co najmniej 2 znaki");
        }
        this.surname = surname;
    }

    /**
     * Metoda zwracająca wiek osoby.
     *
     * @return Wiek osoby
     */
    public int getAge() {
        return age;
    }

    /**
     * Metoda ustawiająca wiek osoby.
     *
     * @param age Wiek do ustawienia
     * @throws AgeException Jeśli wiek jest nieprawidłowy
     */
    public void setAge(int age) throws AgeException {
        if (age < 0 || age > 120) {
            throw new AgeException("Wiek musi być z przedziału 0–120");
        } else if (age < 18) {
            throw new AgeException("Osoba musi mieć co najmniej 18 lat");
        }
        this.age = age;
    }

    /**
     * Metoda zwracająca adres e-mail osoby.
     *
     * @return Adres e-mail osoby
     */
    public String getEmail() {
        return email;
    }

    /**
     * Metoda ustawiająca adres e-mail osoby.
     *
     * @param email Adres e-mail do ustawienia
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Metoda zwracająca reprezentację tekstową osoby.
     *
     * @return Reprezentacja tekstowa osoby
     */
    @Override
    public String toString() {
        return name + " " + surname + " (" + age + "), " + email;
    }


    public void setUsuniety(boolean b) {
        this.usuniety = b;
    }
}
