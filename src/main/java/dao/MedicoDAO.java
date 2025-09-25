package dao;

import model.Medico;
import util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class MedicoDAO extends BaseDAO<Medico, Long> {

    public MedicoDAO() {
        super(Medico.class);
    }

    public Optional<Medico> findByColegiado(String colegiado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Medico> result = em.createQuery(
                            "FROM Medico m WHERE m.colegiado = :colegiado", Medico.class)
                    .setParameter("colegiado", colegiado)
                    .getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } finally {
            em.close();
        }
    }
}

