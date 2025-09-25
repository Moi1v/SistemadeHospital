package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medico",
        uniqueConstraints = @UniqueConstraint(name = "uk_medico_colegiado", columnNames = "colegiado"))
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El número de colegiado es obligatorio")
    @Column(nullable = false, unique = true, length = 20)
    private String colegiado;

    @NotNull(message = "La especialidad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Especialidad especialidad;

    @Email(message = "Email debe tener formato válido")
    @Column(length = 100)
    private String email;

    // OneToMany - Medico es el lado inverso
    @OneToMany(mappedBy = "medico", cascade = CascadeType.REMOVE)
    private List<Cita> citas = new ArrayList<>();

    // Constructores
    public Medico() {}

    public Medico(String nombre, String colegiado, Especialidad especialidad, String email) {
        this.nombre = nombre;
        this.colegiado = colegiado;
        this.especialidad = especialidad;
        this.email = email;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getColegiado() { return colegiado; }
    public void setColegiado(String colegiado) { this.colegiado = colegiado; }

    public Especialidad getEspecialidad() { return especialidad; }
    public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Cita> getCitas() { return citas; }
    public void setCitas(List<Cita> citas) { this.citas = citas; }

    @Override
    public String toString() {
        return String.format("Medico{id=%d, nombre='%s', especialidad=%s}",
                id, nombre, especialidad);
    }
}
