package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import org.example.sys.Employee;
import java.util.List;

// TODO: dodać metody do wyszukiwania pracowników po różnych kryteriach

/**
 * Repozytorium do operacji na pracownikach.
 */
public class UserRepository {

    private final EntityManagerFactory emf;

    // Statyczna zmienna przechowująca ID zalogowanego użytkownika
    private static int loggedInEmployeeId = -1;

    /**
     * Konstruktor inicjalizujący EntityManagerFactory.
     */
    public UserRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    /**
     * Pobiera wszystkich aktywnych pracowników.
     *
     * @return lista aktywnych pracowników
     */
    public List<Employee> pobierzWszystkichPracownikow() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Employee e WHERE e.usuniety = FALSE", Employee.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Zwraca listę aktywnych kasjerów.
     *
     * @return lista aktywnych kasjerów
     */
    public List<Employee> pobierzKasjerow() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Employee e WHERE e.stanowisko = 'Kasjer' AND e.usuniety = FALSE",
                    Employee.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje pracownika po loginie (tylko aktywnych).
     *
     * @param login login pracownika
     * @return znaleziony pracownik lub null
     */
    public Employee znajdzPoLoginie(String login) {
        EntityManager em = emf.createEntityManager();
        try {
            Employee employee = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login",
                            Employee.class
                    ).setParameter("login", login)
                    .getSingleResult();

            return employee != null && !employee.isUsuniety() ? employee : null;
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Dodaje nowego pracownika.
     *
     * @param pracownik pracownik do dodania
     */
    public void dodajPracownika(Employee pracownik) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(pracownik);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Aktualizuje dane istniejącego pracownika.
     *
     * @param pracownik pracownik do aktualizacji
     */
    public void aktualizujPracownika(Employee pracownik) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(pracownik);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Oznacza pracownika jako usuniętego.
     *
     * @param pracownik pracownik do usunięcia
     */
    public void usunPracownika(Employee pracownik) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee managed = em.find(Employee.class, pracownik.getId());
            if (managed != null) {
                managed.setUsuniety(true);
                em.merge(managed);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Wyszukuje pracownika po loginie i haśle (tylko aktywnych).
     *
     * @param login login pracownika
     * @param haslo hasło pracownika
     * @return znaleziony pracownik lub null, jeśli brak
     */
    public Employee znajdzPoLoginieIHasle(String login, String haslo) {
        EntityManager em = emf.createEntityManager();
        try {
            Employee employee = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login AND e.password = :haslo",
                            Employee.class
                    ).setParameter("login", login)
                    .setParameter("haslo", haslo)
                    .getSingleResult();

            // Dodatkowe sprawdzenie flagi 'usuniety'
            if (employee != null && !employee.isUsuniety()) {
                setLoggedInEmployee(employee.getId());
                return employee;
            }

            return null;
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje pracownika po identyfikatorze (tylko aktywnych).
     *
     * @param id identyfikator pracownika
     * @return znaleziony pracownik lub null, jeśli brak
     */
    public Employee znajdzPoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            Employee employee = em.find(Employee.class, id);
            return employee != null && !employee.isUsuniety() ? employee : null;
        } finally {
            em.close();
        }
    }

    /**
     * Ustawia ID zalogowanego pracownika.
     *
     * @param employeeId ID pracownika
     */
    public static void setLoggedInEmployee(int employeeId) {
        loggedInEmployeeId = employeeId;
    }

    /**
     * Zwraca aktualnie zalogowanego pracownika (tylko aktywnych).
     *
     * @return pracownik lub null, jeśli brak
     */
    public Employee getCurrentEmployee() {
        if (loggedInEmployeeId == -1) {
            return null;
        }

        EntityManager em = emf.createEntityManager();
        try {
            Employee employee = em.find(Employee.class, loggedInEmployeeId);
            return employee != null && !employee.isUsuniety() ? employee : null;
        } finally {
            em.close();
        }
    }



    /**
     * Resetuje ID zalogowanego pracownika.
     * Używane podczas wylogowywania.
     */
    public static void resetCurrentEmployee() {
        loggedInEmployeeId = -1;
    }

    /**
     * Zamknięcie EntityManagerFactory.
     */
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}