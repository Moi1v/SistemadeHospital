package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "cita",
        indexes = {
                @Index(name = "idx_cita_medico", columnList = "medico_id"),
                @Index(name = "idx_cita_paciente", columnList = "paciente_id"),
                @Index(name = "idx_cita_fecha_hora", columnList = "fecha_hora")
        },
        uniqueConstraints = @UniqueConstraint(
                name = "uk_cita_medico_fecha",
                columnNames = {"medico_id", "fecha_hora"}
        ))
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    // ManyToOne - Cita es el dueño
    @NotNull(message = "El paciente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cita_paciente"))  // ✅ CORRECTO
    private Paciente paciente;

    // ManyToOne - Cita es el dueño
    @NotNull(message = "El médico es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cita_medico"))  // ✅ CORRECTO
    private Medico medico;

    // Constructores
    public Cita() {}

    public Cita(LocalDateTime fechaHora, String motivo, Paciente paciente, Medico medico) {
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.paciente = paciente;
        this.medico = medico;
        this.estado = EstadoCita.PROGRAMADA;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }

    @Override
    public String toString() {
        return String.format("Cita{id=%d, fechaHora=%s, estado=%s}",
                id, fechaHora, estado);
    }
}