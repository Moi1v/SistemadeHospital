package service;


import dao.CitaDAO;
import dao.MedicoDAO;
import dao.PacienteDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.*;
import util.JPAUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MedicalService {

    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();
    private final CitaDAO citaDAO = new CitaDAO();

    // Métodos para Paciente
    public void registrarPaciente(Paciente paciente) throws Exception {
        if (pacienteDAO.findByDpi(paciente.getDpi()).isPresent()) {
            throw new Exception("Ya existe un paciente con DPI: " + paciente.getDpi());
        }
        pacienteDAO.save(paciente);
    }

    public List<Paciente> listarPacientes() {
        return pacienteDAO.findAll();
    }

    public Optional<Paciente> buscarPacientePorId(Long id) {
        return pacienteDAO.findById(id);
    }

    public void eliminarPaciente(Long id) {
        pacienteDAO.delete(id);
    }

    // Métodos para Médico
    public void registrarMedico(Medico medico) throws Exception {
        if (medicoDAO.findByColegiado(medico.getColegiado()).isPresent()) {
            throw new Exception("Ya existe un médico con colegiado: " + medico.getColegiado());
        }
        medicoDAO.save(medico);
    }

    public List<Medico> listarMedicos() {
        return medicoDAO.findAll();
    }

    public Optional<Medico> buscarMedicoPorId(Long id) {
        return medicoDAO.findById(id);
    }

    // Métodos para Historial Médico
    public void crearOEditarHistorialMedico(Long pacienteId, String alergias,
                                            String antecedentes, String observaciones) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Paciente paciente = em.find(Paciente.class, pacienteId);
            if (paciente == null) {
                throw new Exception("Paciente no encontrado");
            }

            HistorialMedico historial = paciente.getHistorialMedico();
            if (historial == null) {
                historial = new HistorialMedico(paciente);
                paciente.setHistorialMedico(historial);
            }

            historial.setAlergias(alergias);
            historial.setAntecedentes(antecedentes);
            historial.setObservaciones(observaciones);

            em.merge(paciente);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<HistorialMedico> obtenerHistorialMedico(Long pacienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Paciente paciente = em.find(Paciente.class, pacienteId);
            return paciente != null ? Optional.ofNullable(paciente.getHistorialMedico()) : Optional.empty();
        } finally {
            em.close();
        }
    }

    // Métodos para Cita
    public void agendarCita(Long pacienteId, Long medicoId, LocalDateTime fechaHora, String motivo) throws Exception {
        Optional<Paciente> paciente = pacienteDAO.findById(pacienteId);
        Optional<Medico> medico = medicoDAO.findById(medicoId);

        if (paciente.isEmpty()) {
            throw new Exception("Paciente no encontrado");
        }
        if (medico.isEmpty()) {
            throw new Exception("Médico no encontrado");
        }

        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new Exception("No se puede agendar cita en el pasado");
        }

        Cita cita = new Cita(fechaHora, motivo, paciente.get(), medico.get());
        citaDAO.save(cita);
    }

    public void cambiarEstadoCita(Long citaId, EstadoCita nuevoEstado) throws Exception {
        Optional<Cita> citaOpt = citaDAO.findById(citaId);
        if (citaOpt.isEmpty()) {
            throw new Exception("Cita no encontrada");
        }

        Cita cita = citaOpt.get();
        cita.setEstado(nuevoEstado);
        citaDAO.update(cita);
    }

    public void eliminarCita(Long citaId) {
        citaDAO.delete(citaId);
    }

    public List<Cita> listarCitasPaciente(Long pacienteId) {
        return citaDAO.findByPacienteId(pacienteId);
    }

    public List<Cita> listarProximasCitasMedico(Long medicoId) {
        return citaDAO.findProximasCitasByMedico(medicoId);
    }

    public List<Cita> buscarCitasPorRangoFecha(LocalDateTime inicio, LocalDateTime fin) {
        return citaDAO.findByDateRange(inicio, fin);
    }

    public List<Paciente> listarPacientesConCitas() {
        return pacienteDAO.findAllWithCitas();
    }
}

