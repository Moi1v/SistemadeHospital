package model;

import jakarta.persistence.*;

@Entity
@Table(name = "historial_medico")
public class HistorialMedico {

    @Id
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String alergias;

    @Column(columnDefinition = "TEXT")
    private String antecedentes;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // OneToOne - HistorialMedico ES el dueño usando @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_historial_paciente"))  // ✅ CORRECTO
    private Paciente paciente;

    // Constructor vacío (OBLIGATORIO)
    public HistorialMedico() {
        // Constructor requerido por JPA
    }

    // Constructor con parámetros CORREGIDO
    public HistorialMedico(String alergias, String antecedentes, String observaciones, Paciente paciente) {
        this.alergias = alergias;
        this.antecedentes = antecedentes;
        this.observaciones = observaciones;
        this.paciente = paciente;
        if (paciente != null) {
            this.id = paciente.getId();
        }
    }

    // Constructor con solo paciente
    public HistorialMedico(Paciente paciente) {
        this.paciente = paciente;
        if (paciente != null) {
            this.id = paciente.getId();
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    public String getAntecedentes() { return antecedentes; }
    public void setAntecedentes(String antecedentes) { this.antecedentes = antecedentes; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        if (paciente != null) {
            this.id = paciente.getId();
        }
    }

    @Override
    public String toString() {
        return String.format("HistorialMedico{paciente='%s'}",
                paciente != null ? paciente.getNombre() : "N/A");
    }
}