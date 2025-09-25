package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paciente",
        uniqueConstraints = @UniqueConstraint(name = "uk_paciente_dpi", columnNames = "dpi"))
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El DPI es obligatorio")
    @Pattern(regexp = "\\d{13}", message = "El DPI debe tener 13 dígitos")
    @Column(nullable = false, unique = true, length = 13)
    private String dpi;

    @Past(message = "La fecha de nacimiento debe estar en el pasado")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Pattern(regexp = "\\d{8}", message = "El teléfono debe tener 8 dígitos")
    @Column(length = 8)
    private String telefono;

    @Email(message = "Email inválido")
    @Column(length = 100)
    private String email;

    // OneToOne - Lado NO propietario
    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private HistorialMedico historialMedico;

    // OneToMany - Lado NO propietario
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Cita> citas = new ArrayList<>();

    // Constructores
    public Paciente() {}

    public Paciente(String nombre, String dpi, LocalDate fechaNacimiento, String telefono, String email) {
        this.nombre = nombre;
        this.dpi = dpi;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.email = email;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDpi() { return dpi; }
    public void setDpi(String dpi) { this.dpi = dpi; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public HistorialMedico getHistorialMedico() { return historialMedico; }
    public void setHistorialMedico(HistorialMedico historialMedico) { this.historialMedico = historialMedico; }

    public List<Cita> getCitas() { return citas; }
    public void setCitas(List<Cita> citas) { this.citas = citas; }

    @Override
    public String toString() {
        return String.format("Paciente{id=%d, nombre='%s', dpi='%s'}", id, nombre, dpi);
    }
}