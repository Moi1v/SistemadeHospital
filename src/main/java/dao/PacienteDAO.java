package dao;

import model.Paciente;
import util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class PacienteDAO extends BaseDAO<Paciente, Long> {

    public PacienteDAO() {
        super(Paciente.class);
    }

    public Optional<Paciente> findByDpi(String dpi) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Paciente> result = em.createQuery(
                            "FROM Paciente p WHERE p.dpi = :dpi", Paciente.class)
                    .setParameter("dpi", dpi)
                    .getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } finally {
            em.close();
        }
    }

    public List<Paciente> findAllWithCitas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT p FROM Paciente p LEFT JOIN FETCH p.citas",
                            Paciente.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}