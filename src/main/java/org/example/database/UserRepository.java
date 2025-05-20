package org.example.database;

import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Employee;

import java.util.List;

public class UserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepository.class);
    private final EntityManagerFactory emf;
    private static int loggedInEmployeeId = -1;

    public UserRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono UserRepository, EMF = {}", emf);
    }

    public List<Employee> pobierzWszystkichPracownikow() {
        logger.debug("pobierzWszystkichPracownikow() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                    "SELECT e FROM Employee e WHERE e.usuniety = FALSE",
                    Employee.class
            ).getResultList();
            logger.info("pobierzWszystkichPracownikow() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzWszystkichPracownikow() – błąd podczas pobierania pracowników", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzWszystkichPracownikow() – EM zamknięty");
        }
    }

    public List<Employee> pobierzKasjerow() {
        logger.debug("pobierzKasjerow() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                    "SELECT e FROM Employee e WHERE e.stanowisko = 'Kasjer' AND e.usuniety = FALSE",
                    Employee.class
            ).getResultList();
            logger.info("pobierzKasjerow() – znaleziono {} kasjerów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzKasjerow() – błąd podczas pobierania kasjerów", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzKasjerow() – EM zamknięty");
        }
    }

    public Employee znajdzPoLoginie(String login) {
        logger.debug("znajdzPoLoginie() – start, login={}", login);
        EntityManager em = emf.createEntityManager();
        try {
            Employee e = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login",
                            Employee.class
                    )
                    .setParameter("login", login)
                    .getSingleResult();
            if (e != null && !e.isUsuniety()) {
                logger.info("znajdzPoLoginie() – znaleziono: {}", e);
                return e;
            } else {
                logger.warn("znajdzPoLoginie() – użytkownik usunięty lub null");
                return null;
            }
        } catch (NoResultException ex) {
            logger.warn("znajdzPoLoginie() – brak wyniku dla login={}", login);
            return null;
        } catch (Exception e) {
            logger.error("znajdzPoLoginie() – błąd podczas wyszukiwania login={}", login, e);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzPoLoginie() – EM zamknięty");
        }
    }

    public Employee znajdzPoLoginieIHasle(String login, String haslo) {
        logger.debug("znajdzPoLoginieIHasle() – start, login={}", login);
        EntityManager em = emf.createEntityManager();
        try {
            Employee e = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.login = :login AND e.password = :haslo",
                            Employee.class
                    )
                    .setParameter("login", login)
                    .setParameter("haslo", haslo)
                    .getSingleResult();
            if (e != null && !e.isUsuniety()) {
                setLoggedInEmployee(e.getId());
                logger.info("znajdzPoLoginieIHasle() – uwierzytelniono, currentId={}", e.getId());
                return e;
            } else {
                logger.warn("znajdzPoLoginieIHasle() – usunięty lub null");
                return null;
            }
        } catch (NoResultException ex) {
            logger.warn("znajdzPoLoginieIHasle() – brak wyniku dla login={}", login);
            return null;
        } catch (Exception e) {
            logger.error("znajdzPoLoginieIHasle() – błąd podczas logowania login={}", login, e);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzPoLoginieIHasle() – EM zamknięty");
        }
    }

    public Employee znajdzPoId(int id) {
        logger.debug("znajdzPoId() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Employee e = em.find(Employee.class, id);
            if (e != null && !e.isUsuniety()) {
                logger.info("znajdzPoId() – znaleziono: {}", e);
                return e;
            } else {
                logger.warn("znajdzPoId() – brak lub usunięty id={}", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("znajdzPoId() – błąd podczas wyszukiwania id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzPoId() – EM zamknięty");
        }
    }

    public void dodajPracownika(Employee pracownik) {
        logger.debug("dodajPracownika() – start, {}", pracownik);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(pracownik);
            tx.commit();
            logger.info("dodajPracownika() – dodano: {}", pracownik);
        } catch (Exception e) {
            logger.error("dodajPracownika() – błąd podczas dodawania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajPracownika() – EM zamknięty");
        }
    }

    public void aktualizujPracownika(Employee pracownik) {
        logger.debug("aktualizujPracownika() – start, {}", pracownik);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(pracownik);
            tx.commit();
            logger.info("aktualizujPracownika() – zaktualizowano: {}", pracownik);
        } catch (Exception e) {
            logger.error("aktualizujPracownika() – błąd podczas aktualizacji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("aktualizujPracownika() – EM zamknięty");
        }
    }

    public void usunPracownika(Employee pracownik) {
        logger.debug("usunPracownika() – start, {}", pracownik);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employee m = em.find(Employee.class, pracownik.getId());
            if (m != null) {
                m.setUsuniety(true);
                em.merge(m);
                logger.info("usunPracownika() – ustawiono usuniety dla id={}", m.getId());
            } else {
                logger.warn("usunPracownika() – brak pracownika id={}", pracownik.getId());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("usunPracownika() – błąd podczas usuwania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("usunPracownika() – EM zamknięty");
        }
    }

    public Employee getCurrentEmployee() {
        logger.debug("getCurrentEmployee() – currentId={}", loggedInEmployeeId);
        if (loggedInEmployeeId < 0) {
            logger.info("getCurrentEmployee() – brak zalogowanego pracownika");
            return null;
        }
        EntityManager em = emf.createEntityManager();
        try {
            Employee e = em.find(Employee.class, loggedInEmployeeId);
            if (e != null && !e.isUsuniety()) {
                logger.info("getCurrentEmployee() – zwrócono: {}", e);
                return e;
            } else {
                logger.warn("getCurrentEmployee() – pracownik usunięty lub nie istnieje");
                return null;
            }
        } finally {
            em.close();
            logger.debug("getCurrentEmployee() – EM zamknięty");
        }
    }

    public static void setLoggedInEmployee(int employeeId) {
        logger.debug("setLoggedInEmployee() – {}", employeeId);
        loggedInEmployeeId = employeeId;
    }

    public static void resetCurrentEmployee() {
        logger.debug("resetCurrentEmployee() – reset ID");
        loggedInEmployeeId = -1;
    }

    public List<Employee> znajdzPoImieniu(String imieFragment) {
        logger.debug("znajdzPoImieniu() – fragment={}", imieFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE LOWER(e.imie) LIKE LOWER(CONCAT('%', :frag, '%')) " +
                                    "AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("frag", imieFragment)
                    .getResultList();
            logger.info("znajdzPoImieniu() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoImieniu() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoImieniu() – EM zamknięty");
        }
    }

    public List<Employee> znajdzPoNazwisku(String nazwiskoFragment) {
        logger.debug("znajdzPoNazwisku() – fragment={}", nazwiskoFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE LOWER(e.nazwisko) LIKE LOWER(CONCAT('%', :frag, '%')) " +
                                    "AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("frag", nazwiskoFragment)
                    .getResultList();
            logger.info("znajdzPoNazwisku() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoNazwisku() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoNazwisku() – EM zamknięty");
        }
    }

    public List<Employee> znajdzPoWieku(int min, int max) {
        logger.debug("znajdzPoWieku() – min={}, max={}", min, max);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.wiek BETWEEN :min AND :max " +
                                    "AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("znajdzPoWieku() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoWieku() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoWieku() – EM zamknięty");
        }
    }

    public List<Employee> znajdzPoAdresie(int addressId) {
        logger.debug("znajdzPoAdresie() – addressId={}", addressId);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.adres.id = :aid AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("aid", addressId)
                    .getResultList();
            logger.info("znajdzPoAdresie() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoAdresie() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoAdresie() – EM zamknięty");
        }
    }

    public List<Employee> znajdzPoEmailu(String emailFragment) {
        logger.debug("znajdzPoEmailu() – fragment={}", emailFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE LOWER(e.email) LIKE LOWER(CONCAT('%', :frag, '%')) " +
                                    "AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("frag", emailFragment)
                    .getResultList();
            logger.info("znajdzPoEmailu() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoEmailu() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoEmailu() – EM zamknięty");
        }
    }

    public List<Employee> znajdzPoZarobkach(double min, double max) {
        logger.debug("znajdzPoZarobkach() – min={}, max={}", min, max);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.zarobki BETWEEN :min AND :max " +
                                    "AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("znajdzPoZarobkach() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoZarobkach() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoZarobkach() – EM zamknięty");
        }
    }

    public List<Employee> znajdzPoStanowisku(String stanowisko) {
        logger.debug("znajdzPoStanowisku() – stanowisko={}", stanowisko);
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                            "SELECT e FROM Employee e " +
                                    "WHERE e.stanowisko = :st AND e.usuniety = FALSE",
                            Employee.class
                    )
                    .setParameter("st", stanowisko)
                    .getResultList();
            logger.info("znajdzPoStanowisku() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoStanowisku() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoStanowisku() – EM zamknięty");
        }
    }

    public List<Employee> pobierzNaSickLeave() {
        logger.debug("pobierzNaSickLeave() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                    "SELECT e FROM Employee e " +
                            "WHERE e.onSickLeave = TRUE AND e.usuniety = FALSE",
                    Employee.class
            ).getResultList();
            logger.info("pobierzNaSickLeave() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzNaSickLeave() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzNaSickLeave() – EM zamknięty");
        }
    }

    public List<Employee> pobierzNieNaSickLeave() {
        logger.debug("pobierzNieNaSickLeave() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> list = em.createQuery(
                    "SELECT e FROM Employee e " +
                            "WHERE e.onSickLeave = FALSE AND e.usuniety = FALSE",
                    Employee.class
            ).getResultList();
            logger.info("pobierzNieNaSickLeave() – znaleziono {} pracowników", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzNieNaSickLeave() – błąd", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzNieNaSickLeave() – EM zamknięty");
        }
    }

    public void close() {
        logger.debug("close() – zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() – EMF zamknięty");
        }
    }
}
