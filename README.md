# Sistema de Gestión Médica - Clínica

## 👥 Integrantes del Equipo

| Nombre | Carné |
|--------|-------|
| Moisés Emanuel Cabrera Noriega | 2400019 |
| Lourdes Mercedes Alvarado Rodríguez | 2400438 |

---

## 📋 Descripción del Proyecto

Sistema de gestión clínica desarrollado con **JPA/Hibernate** que permite administrar pacientes, médicos, citas médicas e historiales clínicos. El proyecto implementa asociaciones complejas entre entidades y demuestra el uso correcto de cascadas, claves foráneas nombradas y optimización de consultas.

---

## 🏗️ Modelo de Dominio

### Entidades Principales

#### 1. **Paciente**
- **Atributos**: `id`, `nombre`, `dpi` (único), `fechaNacimiento`, `telefono`, `email`
- **Relaciones**:
  - `OneToOne` con `HistorialMedico` (lado **NO propietario**)
  - `OneToMany` con `Cita` (lado **NO propietario**)

#### 2. **HistorialMedico** ⭐ (Dueño del OneToOne)
- **Atributos**: `id`, `alergias`, `antecedentes`, `observaciones`
- **Relaciones**:
  - `OneToOne` con `Paciente` (lado **PROPIETARIO**)
- **Características especiales**:
  - Utiliza `@MapsId` para **PK compartida** con Paciente
  - El `id` del historial es el mismo que el `id` del paciente

#### 3. **Medico**
- **Atributos**: `id`, `nombre`, `colegiado` (único), `especialidad` (enum), `email`
- **Relaciones**:
  - `OneToMany` con `Cita` (lado **NO propietario**)

#### 4. **Cita** (Entidad de unión con metadatos)
- **Atributos**: `id`, `fechaHora`, `estado` (enum), `motivo`
- **Relaciones**:
  - `ManyToOne` con `Paciente` (lado **PROPIETARIO**)
  - `ManyToOne` con `Medico` (lado **PROPIETARIO**)

### Enumeraciones

```java
public enum Especialidad {
    CARDIOLOGIA, DERMATOLOGIA, GINECOLOGIA, 
    NEUROLOGIA, PEDIATRIA, PSIQUIATRIA, 
    TRAUMATOLOGIA, MEDICINA_GENERAL
}

public enum EstadoCita {
    PROGRAMADA, ATENDIDA, CANCELADA
}
```

---

## 🔗 Asociaciones JPA Implementadas

### OneToOne: Paciente ↔ HistorialMedico

**Dueño**: `HistorialMedico`

```java
@Entity
public class HistorialMedico {
    @Id
    private Long id;
    
    @OneToOne
    @MapsId  // ← PK compartida
    @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_historial_paciente"))
    private Paciente paciente;
}
```

**Justificación**: El historial médico no tiene sentido sin un paciente. Al usar `@MapsId`, garantizamos una relación 1:1 real y optimizamos el almacenamiento al compartir la misma clave primaria.

### OneToMany/ManyToOne: Paciente ↔ Cita

```java
// En Paciente (lado NO propietario)
@OneToMany(mappedBy = "paciente", cascade = CascadeType.REMOVE)
private List<Cita> citas;

// En Cita (lado PROPIETARIO)
@ManyToOne
@JoinColumn(name = "paciente_id", foreignKey = @ForeignKey(name = "fk_cita_paciente"))
private Paciente paciente;
```

### OneToMany/ManyToOne: Medico ↔ Cita

```java
// En Medico (lado NO propietario)
@OneToMany(mappedBy = "medico", cascade = CascadeType.REMOVE)
private List<Cita> citas;

// En Cita (lado PROPIETARIO)
@ManyToOne
@JoinColumn(name = "medico_id", foreignKey = @ForeignKey(name = "fk_cita_medico"))
private Medico medico;
```

---

## 🗄️ Estructura de Base de Datos

### Tablas Generadas

```sql
-- Tabla paciente
CREATE TABLE paciente (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dpi VARCHAR(13) NOT NULL UNIQUE,
    fecha_nacimiento DATE NOT NULL,
    telefono VARCHAR(8),
    email VARCHAR(100),
    CONSTRAINT uk_paciente_dpi UNIQUE (dpi)
);

-- Tabla historial_medico (PK compartida)
CREATE TABLE historial_medico (
    id BIGINT PRIMARY KEY,
    alergias TEXT,
    antecedentes TEXT,
    observaciones TEXT,
    CONSTRAINT fk_historial_paciente FOREIGN KEY (id) REFERENCES paciente(id)
);

-- Tabla medico
CREATE TABLE medico (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    colegiado VARCHAR(20) NOT NULL UNIQUE,
    especialidad VARCHAR(30) NOT NULL,
    email VARCHAR(100),
    CONSTRAINT uk_medico_colegiado UNIQUE (colegiado)
);

-- Tabla cita
CREATE TABLE cita (
    id BIGSERIAL PRIMARY KEY,
    fecha_hora TIMESTAMP NOT NULL,
    estado VARCHAR(15) NOT NULL,
    motivo TEXT NOT NULL,
    paciente_id BIGINT NOT NULL,
    medico_id BIGINT NOT NULL,
    CONSTRAINT fk_cita_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id),
    CONSTRAINT fk_cita_medico FOREIGN KEY (medico_id) REFERENCES medico(id),
    CONSTRAINT uk_medico_fecha_hora UNIQUE (medico_id, fecha_hora)
);
```

### Índices Creados

```sql
CREATE INDEX idx_cita_medico ON cita(medico_id);
CREATE INDEX idx_cita_paciente ON cita(paciente_id);
CREATE INDEX idx_cita_fecha_hora ON cita(fecha_hora);
```

### Claves Foráneas Nombradas

- `fk_historial_paciente`: historial_medico → paciente
- `fk_cita_paciente`: cita → paciente
- `fk_cita_medico`: cita → medico

---

## ⚙️ Configuración de Cascadas

### Decisiones de Diseño

| Relación | Cascada | Justificación |
|----------|---------|---------------|
| Paciente → HistorialMedico | `CascadeType.ALL` + `orphanRemoval = true` | El historial no existe sin el paciente |
| Paciente → Citas | `CascadeType.REMOVE` | Al eliminar paciente, se eliminan sus citas |
| Medico → Citas | `CascadeType.REMOVE` | Al eliminar médico, se eliminan sus citas |

**Nota**: No usamos `CascadeType.ALL` en citas para evitar eliminaciones accidentales en cascada cuando solo queremos actualizar referencias.

---

## 🏛️ Arquitectura del Sistema

### Estructura de Capas

```
src/main/java/
├── model/                          # Entidades JPA
│   ├── Paciente.java
│   ├── Medico.java
│   ├── Cita.java
│   ├── HistorialMedico.java
│   ├── Especialidad.java
│   └── EstadoCita.java
├── dao/                            # Data Access Objects
│   ├── BaseDAO.java               # DAO genérico base
│   ├── PacienteDAO.java
│   ├── MedicoDAO.java
│   └── CitaDAO.java
├── service/                        # Lógica de negocio
│   └── MedicalService.java
├── util/                           # Utilidades
│   └── JPAUtil.java               # Gestión de EntityManager
└── com.example.sistemadehospital/
    └── MedicalManagementApp.java  # Aplicación principal
```

---

## 🎯 Funcionalidades Implementadas

### Menú Principal

1. **Registrar paciente** - Validación de DPI único
2. **Crear/editar historial médico** - Gestión del OneToOne
3. **Registrar médico** - Validación de colegiado único
4. **Agendar cita** - Validación de conflictos de horario
5. **Cambiar estado de cita** - PROGRAMADA → ATENDIDA/CANCELADA
6. **Consultas**:
   - Listar pacientes con sus citas
   - Listar médicos con próximas citas
   - Buscar citas por rango de fechas
   - Ver historial médico de un paciente
7. **Eliminar**:
   - Eliminar cita
   - Eliminar paciente (con cascada)
8. **Crear datos de semilla** - Datos de prueba

### Consultas Implementadas

```java
// Buscar pacientes con sus citas (evita N+1)
SELECT DISTINCT p FROM Paciente p 
LEFT JOIN FETCH p.citas c 
LEFT JOIN FETCH c.medico 
ORDER BY p.nombre

// Próximas citas de un médico
SELECT c FROM Cita c 
JOIN FETCH c.paciente 
WHERE c.medico.id = :medicoId 
AND c.fechaHora > CURRENT_TIMESTAMP 
AND c.estado = 'PROGRAMADA' 
ORDER BY c.fechaHora

// Buscar citas por rango de fechas
SELECT c FROM Cita c 
JOIN FETCH c.paciente 
JOIN FETCH c.medico 
WHERE c.fechaHora BETWEEN :inicio AND :fin 
ORDER BY c.fechaHora
```

---

## ✅ Validaciones Implementadas

### Validaciones de Bean Validation

```java
@Pattern(regexp = "\\d{13}", message = "El DPI debe tener 13 dígitos")
private String dpi;

@Email(message = "Email inválido")
private String email;

@Past(message = "La fecha de nacimiento debe estar en el pasado")
private LocalDate fechaNacimiento;
```

### Validaciones de Negocio

- DPI único por paciente
- Colegiado único por médico
- No permitir citas duplicadas (mismo médico, misma fecha/hora)
- No permitir agendar citas en el pasado
- Validación de conflictos de horario

---

## 🚀 Instrucciones de Ejecución

### Prerrequisitos

- **Java 17** o superior
- **PostgreSQL 12** o superior
- **Maven 3.6** o superior

### Configuración de Base de Datos

1. Crear base de datos:
```sql
CREATE DATABASE jpql;
```

2. Configurar en `persistence.xml`:
```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5433/jpql"/>
<property name="jakarta.persistence.jdbc.user" value="postgres"/>
<property name="jakarta.persistence.jdbc.password" value="admin123"/>
```

### Compilación y Ejecución

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn exec:java -Dexec.mainClass="com.example.sistemadehospital.MedicalManagementApp"
```

### Primera Ejecución

1. Ejecutar la aplicación
2. Seleccionar opción **8** (Crear datos de semilla)
3. Verificar que se crearon los datos de prueba
4. Explorar las funcionalidades del menú

---

## 🔍 Demostración del Modelo

### Verificación en Base de Datos

```sql
-- Ver estructura de tablas
\d paciente
\d historial_medico
\d medico
\d cita

-- Verificar claves foráneas
SELECT constraint_name, table_name, column_name
FROM information_schema.key_column_usage
WHERE table_schema = 'public';

-- Verificar índices
SELECT indexname, tablename, indexdef
FROM pg_indexes
WHERE schemaname = 'public';

-- Ver relación OneToOne (PK compartida)
SELECT p.id, p.nombre, h.id as historial_id, h.alergias
FROM paciente p
INNER JOIN historial_medico h ON p.id = h.id;
```

---

## 📊 Características Técnicas Destacadas

### 1. **PK Compartida con @MapsId**
- HistorialMedico comparte el ID con Paciente
- Garantiza relación 1:1 verdadera
- Optimiza consultas y almacenamiento

### 2. **Prevención del Problema N+1**
- Uso de `JOIN FETCH` en consultas críticas
- Carga eager donde es necesario
- Optimización de rendimiento

### 3. **Claves Foráneas Nombradas**
- Todas las FK tienen nombres descriptivos
- Facilita debugging y mantenimiento
- Cumple con mejores prácticas

### 4. **Índices Estratégicos**
- Índices en columnas de búsqueda frecuente
- Mejora rendimiento de consultas
- Optimización de joins

### 5. **Restricciones de Unicidad**
- `uk_medico_fecha_hora` previene doble reserva
- `uk_paciente_dpi` garantiza identidad única
- `uk_medico_colegiado` valida registro profesional

---

## 🎓 Conceptos Aplicados

- ✅ Asociaciones JPA (OneToOne, OneToMany, ManyToOne)
- ✅ Cascadas y ciclo de vida de entidades
- ✅ PK compartida con @MapsId
- ✅ Named Foreign Keys
- ✅ Índices y optimización
- ✅ Bean Validation
- ✅ JPQL con JOIN FETCH
- ✅ Patrón DAO
- ✅ Arquitectura en capas
- ✅ Transacciones JPA

---

## 📝 Conclusiones

Este proyecto demuestra una implementación completa de JPA/Hibernate con:

1. **Modelo de dominio robusto** con asociaciones correctamente configuradas
2. **Optimización de base de datos** mediante índices y restricciones
3. **Arquitectura escalable** con separación de responsabilidades
4. **Validaciones en múltiples capas** para integridad de datos
5. **Decisiones de diseño documentadas** sobre cascadas y relaciones

El sistema está preparado para producción con todas las mejores prácticas aplicadas.

---

## 📚 Referencias

- [Jakarta Persistence API](https://jakarta.ee/specifications/persistence/)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

**Fecha de entrega**: 19 de Septiembre 2025  
**Universidad**: Universidad San Pablo de Guatemala  
**Curso**: Programación IV
