package dao;

import jakarta.persistence.EntityManager;
import model.Cita;
import util.JPAUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CitaDAO {

    public void save(Cita cita) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cita);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar cita", e);
        } finally {
            em.close();
        }
    }

    public void update(Cita cita) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(cita);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar cita", e);
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cita cita = em.find(Cita.class, id);
            if (cita != null) {
                em.remove(cita);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar cita", e);
        } finally {
            em.close();
        }
    }

    public Optional<Cita> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Cita cita = em.find(Cita.class, id);
            return Optional.ofNullable(cita);
        } finally {
            em.close();
        }
    }

    // MÉTODO AGREGADO: findAll()
    public List<Cita> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Cita c " +
                                    "JOIN FETCH c.paciente " +
                                    "JOIN FETCH c.medico " +
                                    "ORDER BY c.fechaHora", Cita.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Cita> findByPacienteId(Long pacienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Cita c " +
                                    "JOIN FETCH c.medico " +
                                    "WHERE c.paciente.id = :pacienteId " +
                                    "ORDER BY c.fechaHora", Cita.class)
                    .setParameter("pacienteId", pacienteId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Cita> findProximasCitasByMedico(Long medicoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Cita c " +
                                    "JOIN FETCH c.paciente " +
                                    "WHERE c.medico.id = :medicoId AND c.fechaHora > :now " +
                                    "ORDER BY c.fechaHora", Cita.class)
                    .setParameter("medicoId", medicoId)
                    .setParameter("now", LocalDateTime.now())
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Cita> findByDateRange(LocalDateTime inicio, LocalDateTime fin) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Cita c " +
                                    "JOIN FETCH c.paciente " +
                                    "JOIN FETCH c.medico " +
                                    "WHERE c.fechaHora BETWEEN :inicio AND :fin " +
                                    "ORDER BY c.fechaHora", Cita.class)
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}