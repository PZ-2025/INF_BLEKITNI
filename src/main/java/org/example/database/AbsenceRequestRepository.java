package org.example.database;

import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.AbsenceRequest;
import org.example.sys.Employee;

import java.util.Date;
import java.util.List;

public class AbsenceRequestRepository {
    private static final Logger logger = LogManager.getLogger(AbsenceRequestRepository.class);
    private final EntityManagerFactory emf;

    public AbsenceRequestRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono AbsenceRequestRepository, EMF={}", emf);
    }

    public void dodajWniosek(AbsenceRequest request) {
        logger.debug("dodajWniosek() - start, request={}", request);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(request);
            tx.commit();
            logger.info("dodajWniosek() - wniosek dodany: {}", request);
        } catch (Exception e) {
            logger.error("dodajWniosek() - błąd podczas dodawania wniosku", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajWniosek() - EM zamknięty");
        }
    }

    public AbsenceRequest znajdzWniosekPoId(int id) {
        logger.debug("znajdzWniosekPoId() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            AbsenceRequest w = em.find(AbsenceRequest.class, id);
            logger.info("znajdzWniosekPoId() - znaleziono: {}", w);
            return w;
        } catch (Exception e) {
            logger.error("znajdzWniosekPoId() - błąd podczas pobierania wniosku o id=" + id, e);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzWniosekPoId() - EM zamknięty");
        }
    }

    public List<AbsenceRequest> pobierzWszystkieWnioski() {
        logger.debug("pobierzWszystkieWnioski() - start");
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery("SELECT w FROM AbsenceRequest w", AbsenceRequest.class)
                    .getResultList();
            logger.info("pobierzWszystkieWnioski() - pobrano {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzWszystkieWnioski() - błąd podczas pobierania wszystkich wniosków", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzWszystkieWnioski() - EM zamknięty");
        }
    }

    public void usunWniosek(int id) {
        logger.debug("usunWniosek() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AbsenceRequest w = em.find(AbsenceRequest.class, id);
            if (w != null) {
                em.remove(w);
                logger.info("usunWniosek() - usunięto wniosek: {}", w);
            } else {
                logger.warn("usunWniosek() - brak wniosku o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("usunWniosek() - błąd podczas usuwania wniosku o id=" + id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("usunWniosek() - EM zamknięty");
        }
    }

    public void aktualizujWniosek(AbsenceRequest request) {
        logger.debug("aktualizujWniosek() - start, request={}", request);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(request);
            tx.commit();
            logger.info("aktualizujWniosek() - zaktualizowano wniosek: {}", request);
        } catch (Exception e) {
            logger.error("aktualizujWniosek() - błąd podczas aktualizacji wniosku", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("aktualizujWniosek() - EM zamknięty");
        }
    }

    // --- metody wyszukiwania ---

    public List<AbsenceRequest> znajdzWnioskiPracownika(Employee pracownik) {
        logger.debug("znajdzWnioskiPracownika() - pracownik={}", pracownik);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.pracownik = :pracownik",
                            AbsenceRequest.class)
                    .setParameter("pracownik", pracownik)
                    .getResultList();
            logger.info("znajdzWnioskiPracownika() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzWnioskiPracownika() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzWnioskiPracownika() - EM zamknięty");
        }
    }

    public List<AbsenceRequest> znajdzWnioskiPracownikaPoId(int idPracownika) {
        logger.debug("znajdzWnioskiPracownikaPoId() - idPracownika={}", idPracownika);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.pracownik.id = :id",
                            AbsenceRequest.class)
                    .setParameter("id", idPracownika)
                    .getResultList();
            logger.info("znajdzWnioskiPracownikaPoId() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzWnioskiPracownikaPoId() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzWnioskiPracownikaPoId() - EM zamknięty");
        }
    }

    public List<AbsenceRequest> znajdzWnioskiPoTypie(String typWniosku) {
        logger.debug("znajdzWnioskiPoTypie() - typWniosku={}", typWniosku);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.typWniosku = :typ",
                            AbsenceRequest.class)
                    .setParameter("typ", typWniosku)
                    .getResultList();
            logger.info("znajdzWnioskiPoTypie() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzWnioskiPoTypie() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzWnioskiPoTypie() - EM zamknięty");
        }
    }

    public List<AbsenceRequest> znajdzWnioskiPoStatusie(AbsenceRequest.StatusWniosku status) {
        logger.debug("znajdzWnioskiPoStatusie() - status={}", status);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.status = :status",
                            AbsenceRequest.class)
                    .setParameter("status", status)
                    .getResultList();
            logger.info("znajdzWnioskiPoStatusie() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzWnioskiPoStatusie() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzWnioskiPoStatusie() - EM zamknięty");
        }
    }

    public List<AbsenceRequest> znajdzWnioskiOdDaty(Date dataOd) {
        logger.debug("znajdzWnioskiOdDaty() - dataOd={}", dataOd);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.dataRozpoczecia >= :dataOd",
                            AbsenceRequest.class)
                    .setParameter("dataOd", dataOd)
                    .getResultList();
            logger.info("znajdzWnioskiOdDaty() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzWnioskiOdDaty() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzWnioskiOdDaty() - EM zamknięty");
        }
    }

    public List<AbsenceRequest> znajdzWnioskiDoDaty(Date dataDo) {
        logger.debug("znajdzWnioskiDoDaty() - dataDo={}", dataDo);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.dataZakonczenia <= :dataDo",
                            AbsenceRequest.class)
                    .setParameter("dataDo", dataDo)
                    .getResultList();
            logger.info("znajdzWnioskiDoDaty() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzWnioskiDoDaty() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzWnioskiDoDaty() - EM zamknięty");
        }
    }

    public List<AbsenceRequest> znajdzWnioskiWZakresieDat(Date dataOd, Date dataDo) {
        logger.debug("znajdzWnioskiWZakresieDat() - dataOd={}, dataDo={}", dataOd, dataDo);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.dataRozpoczecia >= :dataOd AND w.dataZakonczenia <= :dataDo",
                            AbsenceRequest.class)
                    .setParameter("dataOd", dataOd)
                    .setParameter("dataDo", dataDo)
                    .getResultList();
            logger.info("znajdzWnioskiWZakresieDat() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzWnioskiWZakresieDat() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzWnioskiWZakresieDat() - EM zamknięty");
        }
    }

    public List<AbsenceRequest> znajdzWnioskiNachodzaceNaZakresDat(Date dataOd, Date dataDo) {
        logger.debug("znajdzWnioskiNachodzaceNaZakresDat() - dataOd={}, dataDo={}", dataOd, dataDo);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.dataRozpoczecia <= :dataDo AND w.dataZakonczenia >= :dataOd",
                            AbsenceRequest.class)
                    .setParameter("dataDo", dataDo)
                    .setParameter("dataOd", dataOd)
                    .getResultList();
            logger.info("znajdzWnioskiNachodzaceNaZakresDat() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzWnioskiNachodzaceNaZakresDat() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzWnioskiNachodzaceNaZakresDat() - EM zamknięty");
        }
    }

    public void close() {
        logger.debug("close() - zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() - EMF zamknięty");
        }
    }
}
