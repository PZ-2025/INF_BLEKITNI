package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Repozytorium do operacji na pracownikach.
 */
public class UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private final EntityManagerFactory emf;
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
            List<Employee> employees = em.createQuery(
                    "SELECT e FROM Employee e WHERE e.usuniety = FALSE", Employee.class
            ).getResultList();
            logger.info("Pobrano {} aktywnych pracowników", employees.size());
            return employees;
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera wszystkich aktywnych kasjerów.
     *
     * @return lista kasjerów
     */
    public List<Employee> pobierzKasjerow() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> cashiers = em.createQuery(
                    "SELECT e FROM Employee e WHERE e.stanowisko = 'Kasjer' AND e.usuniety = FALSE",
                    Employee.class
            ).getResultList();
            logger.info("Pobrano {} aktywnych kasjerów", cashiers.size());
            return cashiers;
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje pracownika po loginie.
     *
     * @param login login pracownika
     * @return pracownik lub null
     */
    public Employee znajdzPoLoginie(String login) {
        EntityManager em = emf.createEntityManager();
        try {
            Employee employee = em.createQuery(
                    "SELECT e FROM Employee e WHERE e.login = :login",
                    Employee.class
            ).setParameter("login", login).getSingleResult();

            if (!employee.isUsuniety()) {
                logger.info("Znaleziono pracownika po loginie: {}", login);
                return employee;
            }
            logger.warn("Pracownik {} jest oznaczony jako usunięty", login);
            return null;
        } catch (NoResultException e) {
            logger.warn("Nie znaleziono pracownika o loginie: {}", login);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje pracownika po loginie i haśle.
     *
     * @param login login
     * @param haslo hasło
     * @return pracownik lub null
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

            if (!employee.isUsuniety()) {
                setLoggedInEmployee(employee.getId());
                logger.info("Zalogowano pracownika: {}", login);
                return employee;
            }
            logger.warn("Pracownik {} jest usunięty", login);
            return null;
        } catch (NoResultException e) {
            logger.warn("Nieprawidłowe dane logowania dla loginu: {}", login);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje pracownika po ID.
     *
     * @param id identyfikator pracownika
     * @return pracownik lub null
     */
    public Employee znajdzPoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            Employee employee = em.find(Employee.class, id);
            if (employee != null && !employee.isUsuniety()) {
                logger.info("Znaleziono pracownika o ID {}", id);
                return employee;
            }
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Dodaje nowego pracownika.
     *
     * @param pracownik nowy pracownik
     */
    public void dodajPracownika(Employee pracownik) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(pracownik);
            tx.commit();
            logger.info("Dodano pracownika: {}", pracownik.getLogin());
        } catch (Exception e) {
            logger.error("Błąd podczas dodawania pracownika: {}", e.getMessage());
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Aktualizuje dane pracownika.
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
            logger.info("Zaktualizowano dane pracownika: {}", pracownik.getLogin());
        } catch (Exception e) {
            logger.error("Błąd podczas aktualizacji pracownika: {}", e.getMessage());
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Usuwa (oznacza jako usunięty) pracownika.
     *
     * @param pracownik pracownik do usunięcia
     * @throws SecurityException jeśli próbujemy usunąć użytkownika "root"
     */
    public void usunPracownika(Employee pracownik) throws SecurityException {
        if (pracownik != null && "root".equalsIgnoreCase(pracownik.getStanowisko())) {
            logger.warn("Próba usunięcia użytkownika z rolą root");
            throw new SecurityException("Nie można usunąć użytkownika z rolą root");
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee managed = em.find(Employee.class, pracownik.getId());
            if (managed != null && !"root".equalsIgnoreCase(managed.getStanowisko())) {
                managed.setUsuniety(true);
                em.merge(managed);
                logger.info("Oznaczono pracownika jako usuniętego: {}", pracownik.getLogin());
            } else {
                logger.warn("Nie można usunąć pracownika: root lub nie znaleziono");
                throw new SecurityException("Nie można usunąć użytkownika z rolą root");
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("Błąd przy usuwaniu pracownika: {}", e.getMessage());
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    /**
     * Zwraca aktualnie zalogowanego pracownika.
     *
     * @return zalogowany pracownik lub null
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
     * Ustawia ID zalogowanego pracownika.
     *
     * @param employeeId ID pracownika
     */
    public static void setLoggedInEmployee(int employeeId) {
        loggedInEmployeeId = employeeId;
    }

    /**
     * Resetuje ID zalogowanego pracownika.
     */
    public static void resetCurrentEmployee() {
        loggedInEmployeeId = -1;
    }

    /**
     * Zamyka EntityManagerFactory.
     */
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            logger.info("Zamknięto EntityManagerFactory");
        }
    }
}
