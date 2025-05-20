package org.example.sys;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.example.wyjatki.AgeException;
import org.example.wyjatki.NameException;

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

    public Person() {}

    public Person(String name, String surname, int age, String email)
            throws AgeException, NameException {
        setName(name);
        setSurname(surname);
        setAge(age);
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws NameException {
        if (name == null || name.isEmpty()) {
            throw new NameException("Imię nie może być puste");
        } else if (name.length() < 2) {
            throw new NameException("Imię musi mieć co najmniej 2 znaki");
        }
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) throws NameException {
        if (surname == null || surname.isEmpty()) {
            throw new NameException("Nazwisko nie może być puste");
        } else if (surname.length() < 2) {
            throw new NameException("Nazwisko musi mieć co najmniej 2 znaki");
        }
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) throws AgeException {
        if (age < 0 || age > 120) {
            throw new AgeException("Wiek musi być z przedziału 0–120");
        } else if (age < 18) {
            throw new AgeException("Osoba musi mieć co najmniej 18 lat");
        }
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return name + " " + surname + " (" + age + "), " + email;
    }
}
