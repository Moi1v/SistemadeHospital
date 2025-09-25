package com.example.sistemadehospital;

import model.*;
import service.MedicalService;
import util.JPAUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MedicalManagementApp {

    private Scanner scanner = new Scanner(System.in);
    private MedicalService medicalService = new MedicalService();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void main(String[] args) {
        MedicalManagementApp app = new MedicalManagementApp();
        app.iniciar();
    }

    public void iniciar() {
        System.out.println("=== SISTEMA DE GESTIÓN MÉDICA ===");

        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> registrarPaciente();
                case 2 -> crearEditarHistorial();
                case 3 -> registrarMedico();
                case 4 -> agendarCita();
                case 5 -> cambiarEstadoCita();
                case 6 -> mostrarMenuConsultas();
                case 7 -> mostrarMenuEliminacion();
                case 8 -> crearDatosSemilla();
                case 0 -> continuar = false;
                default -> System.out.println("Opción inválida");
            }
        }

        JPAUtil.closeEntityManagerFactory();
        System.out.println("¡Gracias por usar el sistema!");
    }

    private void mostrarMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("               MENÚ PRINCIPAL");
        System.out.println("=".repeat(50));
        System.out.println("1. Registrar paciente");
        System.out.println("2. Crear/editar historial médico");
        System.out.println("3. Registrar médico");
        System.out.println("4. Agendar cita");
        System.out.println("5. Cambiar estado de cita");
        System.out.println("6. Consultas");
        System.out.println("7. Eliminar");
        System.out.println("8. Crear datos de semilla");
        System.out.println("0. Salir");
        System.out.println("=".repeat(50));
    }

    private void registrarPaciente() {
        System.out.println("\n--- REGISTRAR PACIENTE ---");

        try {
            System.out.print("Nombre completo: ");
            String nombre = scanner.nextLine();

            System.out.print("DPI (13 dígitos): ");
            String dpi = scanner.nextLine();

            System.out.print("Fecha de nacimiento (dd/MM/yyyy): ");
            LocalDate fechaNacimiento = leerFecha();
            if (fechaNacimiento == null) return;

            System.out.print("Teléfono (8 dígitos): ");
            String telefono = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            Paciente paciente = new Paciente(nombre, dpi, fechaNacimiento, telefono, email);
            medicalService.registrarPaciente(paciente);

            System.out.println("Paciente registrado exitosamente");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void crearEditarHistorial() {
        System.out.println("\n--- CREAR/EDITAR HISTORIAL MÉDICO ---");

        try {
            listarPacientes();
            long pacienteId = leerEntero("ID del paciente: ");

            Optional<Paciente> pacienteOpt = medicalService.buscarPacientePorId(pacienteId);
            if (pacienteOpt.isEmpty()) {
                System.out.println("Paciente no encontrado");
                return;
            }

            System.out.println("Paciente: " + pacienteOpt.get().getNombre());

            System.out.print("Alergias: ");
            String alergias = scanner.nextLine();

            System.out.print("Antecedentes: ");
            String antecedentes = scanner.nextLine();

            System.out.print("Observaciones: ");
            String observaciones = scanner.nextLine();

            medicalService.crearOEditarHistorialMedico(pacienteId, alergias, antecedentes, observaciones);
            System.out.println("Historial médico guardado exitosamente");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registrarMedico() {
        System.out.println("\n--- REGISTRAR MÉDICO ---");

        try {
            System.out.print("Nombre completo: ");
            String nombre = scanner.nextLine();

            System.out.print("Número de colegiado: ");
            String colegiado = scanner.nextLine();

            System.out.println("Especialidades disponibles:");
            Especialidad[] especialidades = Especialidad.values();
            for (int i = 0; i < especialidades.length; i++) {
                System.out.println((i + 1) + ". " + especialidades[i]);
            }

            int opcionEsp = leerEntero("Seleccione especialidad (1-" + especialidades.length + "): ");
            if (opcionEsp < 1 || opcionEsp > especialidades.length) {
                System.out.println("Opción inválida");
                return;
            }

            System.out.print("Email: ");
            String email = scanner.nextLine();

            Medico medico = new Medico(nombre, colegiado, especialidades[opcionEsp - 1], email);
            medicalService.registrarMedico(medico);

            System.out.println("Médico registrado exitosamente");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void agendarCita() {
        System.out.println("\n--- AGENDAR CITA ---");

        try {
            listarPacientes();
            long pacienteId = leerEntero("ID del paciente: ");

            listarMedicos();
            long medicoId = leerEntero("ID del médico: ");

            System.out.print("Fecha y hora (dd/MM/yyyy HH:mm): ");
            LocalDateTime fechaHora = leerFechaHora();
            if (fechaHora == null) return;

            System.out.print("Motivo de la consulta: ");
            String motivo = scanner.nextLine();

            medicalService.agendarCita(pacienteId, medicoId, fechaHora, motivo);
            System.out.println("Cita agendada exitosamente");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void cambiarEstadoCita() {
        System.out.println("\n--- CAMBIAR ESTADO DE CITA ---");

        try {
            listarCitas();
            long citaId = leerEntero("ID de la cita: ");

            System.out.println("Estados disponibles:");
            EstadoCita[] estados = EstadoCita.values();
            for (int i = 0; i < estados.length; i++) {
                System.out.println((i + 1) + ". " + estados[i].getDescripcion());
            }

            int opcionEstado = leerEntero("Seleccione nuevo estado (1-" + estados.length + "): ");
            if (opcionEstado < 1 || opcionEstado > estados.length) {
                System.out.println("Opción inválida");
                return;
            }

            medicalService.cambiarEstadoCita(citaId, estados[opcionEstado - 1]);
            System.out.println("Estado de cita actualizado exitosamente");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void mostrarMenuConsultas() {
        System.out.println("\n--- MENÚ DE CONSULTAS ---");
        System.out.println("1. Listar pacientes con sus citas");
        System.out.println("2. Listar médicos con próximas citas");
        System.out.println("3. Buscar citas por rango de fechas");
        System.out.println("4. Ver historial médico de un paciente");

        int opcion = leerEntero("Seleccione una opción (1-4): ");

        switch (opcion) {
            case 1 -> listarPacientesConCitas();
            case 2 -> listarMedicosConProximasCitas();
            case 3 -> buscarCitasPorRangoFechas();
            case 4 -> verHistorialMedico();
            default -> System.out.println("Opción inválida");
        }
    }

    private void mostrarMenuEliminacion() {
        System.out.println("\n--- MENÚ DE ELIMINACIÓN ---");
        System.out.println("1. Eliminar cita");
        System.out.println("2. Eliminar paciente");

        int opcion = leerEntero("Seleccione una opción (1-2): ");

        switch (opcion) {
            case 1 -> eliminarCita();
            case 2 -> eliminarPaciente();
            default -> System.out.println("Opción inválida");
        }
    }

    private void listarPacientes() {
        System.out.println("\n--- PACIENTES ---");
        List<Paciente> pacientes = medicalService.listarPacientes();

        if (pacientes.isEmpty()) {
            System.out.println("No hay pacientes registrados");
        } else {
            for (Paciente p : pacientes) {
                System.out.printf("ID: %d | %s | DPI: %s | Tel: %s\n",
                        p.getId(), p.getNombre(), p.getDpi(), p.getTelefono());
            }
        }
    }

    private void listarMedicos() {
        System.out.println("\n--- MÉDICOS ---");
        List<Medico> medicos = medicalService.listarMedicos();

        if (medicos.isEmpty()) {
            System.out.println("No hay médicos registrados");
        } else {
            for (Medico m : medicos) {
                System.out.printf("ID: %d | Dr. %s | %s | Colegiado: %s\n",
                        m.getId(), m.getNombre(), m.getEspecialidad(), m.getColegiado());
            }
        }
    }

    private void listarCitas() {
        System.out.println("\n--- CITAS ---");
        // Aquí necesitarías un método en MedicalService para obtener todas las citas
        // Por ahora usaremos el DAO directamente
        try {
            List<Cita> citas = medicalService.listarTodasLasCitas();

            if (citas.isEmpty()) {
                System.out.println("No hay citas registradas");
            } else {
                System.out.println("ID | Fecha/Hora | Paciente | Médico | Estado");
                System.out.println("-".repeat(70));
                for (Cita c : citas) {
                    System.out.printf("%d | %s | %s | Dr. %s | %s\n",
                            c.getId(),
                            c.getFechaHora().format(dateTimeFormatter),
                            c.getPaciente().getNombre(),
                            c.getMedico().getNombre(),
                            c.getEstado().getDescripcion());
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar citas: " + e.getMessage());
        }
    }

    private void listarPacientesConCitas() {
        System.out.println("\n--- PACIENTES CON SUS CITAS ---");

        try {
            List<Paciente> pacientes = medicalService.listarPacientesConCitas();

            if (pacientes.isEmpty()) {
                System.out.println("No hay pacientes registrados");
                return;
            }

            for (Paciente p : pacientes) {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("PACIENTE: " + p.getNombre());
                System.out.println("DPI: " + p.getDpi());
                System.out.println("Teléfono: " + p.getTelefono());

                List<Cita> citas = medicalService.listarCitasPaciente(p.getId());
                if (citas.isEmpty()) {
                    System.out.println("Sin citas registradas");
                } else {
                    System.out.println("CITAS:");
                    for (Cita c : citas) {
                        System.out.printf("  • %s - Dr. %s (%s) - %s\n",
                                c.getFechaHora().format(dateTimeFormatter),
                                c.getMedico().getNombre(),
                                c.getMedico().getEspecialidad(),
                                c.getEstado().getDescripcion());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarMedicosConProximasCitas() {
        System.out.println("\n--- MÉDICOS CON PRÓXIMAS CITAS ---");

        try {
            List<Medico> medicos = medicalService.listarMedicos();

            for (Medico m : medicos) {
                List<Cita> proximasCitas = medicalService.listarProximasCitasMedico(m.getId());

                if (!proximasCitas.isEmpty()) {
                    System.out.println("\n" + "=".repeat(60));
                    System.out.println("MÉDICO: Dr. " + m.getNombre());
                    System.out.println("Especialidad: " + m.getEspecialidad());
                    System.out.println("PRÓXIMAS CITAS:");

                    for (Cita c : proximasCitas) {
                        System.out.printf("  • %s - %s - %s\n",
                                c.getFechaHora().format(dateTimeFormatter),
                                c.getPaciente().getNombre(),
                                c.getMotivo());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarCitasPorRangoFechas() {
        System.out.println("\n--- BUSCAR CITAS POR RANGO DE FECHAS ---");

        try {
            System.out.print("Fecha de inicio (dd/MM/yyyy HH:mm): ");
            LocalDateTime fechaInicio = leerFechaHora();
            if (fechaInicio == null) return;

            System.out.print("Fecha de fin (dd/MM/yyyy HH:mm): ");
            LocalDateTime fechaFin = leerFechaHora();
            if (fechaFin == null) return;

            if (fechaInicio.isAfter(fechaFin)) {
                System.out.println("Error: La fecha de inicio debe ser anterior a la fecha de fin");
                return;
            }

            List<Cita> citas = medicalService.buscarCitasPorRangoFecha(fechaInicio, fechaFin);

            if (citas.isEmpty()) {
                System.out.println("No se encontraron citas en el rango especificado");
            } else {
                System.out.println("\nCITAS ENCONTRADAS (" + citas.size() + "):");
                System.out.println("=".repeat(80));

                for (Cita c : citas) {
                    System.out.printf("ID: %d | %s | %s -> Dr. %s | %s\n",
                            c.getId(),
                            c.getFechaHora().format(dateTimeFormatter),
                            c.getPaciente().getNombre(),
                            c.getMedico().getNombre(),
                            c.getEstado().getDescripcion());
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void verHistorialMedico() {
        System.out.println("\n--- VER HISTORIAL MÉDICO ---");

        try {
            listarPacientes();
            long pacienteId = leerEntero("ID del paciente: ");

            Optional<HistorialMedico> historialOpt = medicalService.obtenerHistorialMedico(pacienteId);

            if (historialOpt.isEmpty()) {
                System.out.println("No se encontró historial médico para ese paciente");
            } else {
                HistorialMedico h = historialOpt.get();
                Paciente p = h.getPaciente();

                System.out.println("\n" + "=".repeat(60));
                System.out.println("HISTORIAL MÉDICO");
                System.out.println("=".repeat(60));
                System.out.println("Paciente: " + p.getNombre());
                System.out.println("DPI: " + p.getDpi());
                System.out.println("Fecha de nacimiento: " + p.getFechaNacimiento().format(dateFormatter));
                System.out.println("Teléfono: " + p.getTelefono());
                System.out.println("Email: " + p.getEmail());
                System.out.println("\nAlergias: " + (h.getAlergias() != null ? h.getAlergias() : "Ninguna"));
                System.out.println("Antecedentes: " + (h.getAntecedentes() != null ? h.getAntecedentes() : "Ninguno"));
                System.out.println("Observaciones: " + (h.getObservaciones() != null ? h.getObservaciones() : "Ninguna"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarCita() {
        System.out.println("\n--- ELIMINAR CITA ---");

        try {
            listarCitas();
            long citaId = leerEntero("ID de la cita a eliminar: ");

            System.out.print("¿Está seguro de eliminar esta cita? (s/n): ");
            String confirmacion = scanner.nextLine().trim().toLowerCase();

            if (confirmacion.startsWith("s")) {
                medicalService.eliminarCita(citaId);
                System.out.println("Cita eliminada exitosamente");
            } else {
                System.out.println("Eliminación cancelada");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarPaciente() {
        System.out.println("\n--- ELIMINAR PACIENTE ---");
        System.out.println("IMPORTANTE: Se eliminarán también el historial médico y las citas del paciente");

        try {
            listarPacientes();
            long pacienteId = leerEntero("ID del paciente a eliminar: ");

            System.out.print("¿Está seguro de eliminar este paciente y todos sus datos? (s/n): ");
            String confirmacion = scanner.nextLine().trim().toLowerCase();

            if (confirmacion.startsWith("s")) {
                medicalService.eliminarPaciente(pacienteId);
                System.out.println("Paciente eliminado exitosamente");
            } else {
                System.out.println("Eliminación cancelada");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void crearDatosSemilla() {
        System.out.println("\n--- CREAR DATOS DE SEMILLA ---");
        System.out.print("¿Está seguro de crear datos de prueba? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();

        if (!confirmacion.startsWith("s")) {
            System.out.println("Operación cancelada");
            return;
        }

        try {
            // Crear pacientes de prueba
            Paciente p1 = new Paciente("Juan Carlos Pérez", "1234567890123",
                    LocalDate.of(1985, 5, 15), "12345678", "juan@email.com");
            Paciente p2 = new Paciente("María Elena García", "9876543210987",
                    LocalDate.of(1990, 8, 20), "87654321", "maria@email.com");

            medicalService.registrarPaciente(p1);
            medicalService.registrarPaciente(p2);

            // Crear médicos de prueba
            Medico m1 = new Medico("Dr. Roberto Hernández", "12345",
                    Especialidad.CARDIOLOGIA, "roberto@clinica.com");
            Medico m2 = new Medico("Dra. Ana Sofía López", "67890",
                    Especialidad.PEDIATRIA, "ana@clinica.com");

            medicalService.registrarMedico(m1);
            medicalService.registrarMedico(m2);

            // Obtener IDs generados
            List<Paciente> pacientes = medicalService.listarPacientes();
            List<Medico> medicos = medicalService.listarMedicos();

            if (pacientes.size() >= 2 && medicos.size() >= 2) {
                // Crear historiales médicos
                medicalService.crearOEditarHistorialMedico(pacientes.get(pacientes.size()-2).getId(),
                        "Alergia a penicilina", "Hipertensión familiar", "Paciente estable");
                medicalService.crearOEditarHistorialMedico(pacientes.get(pacientes.size()-1).getId(),
                        "Sin alergias conocidas", "Sin antecedentes relevantes", "Primera consulta");

                // Crear citas de prueba
                LocalDateTime fechaCita1 = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
                LocalDateTime fechaCita2 = LocalDateTime.now().plusDays(2).withHour(15).withMinute(30).withSecond(0).withNano(0);

                medicalService.agendarCita(pacientes.get(pacientes.size()-2).getId(),
                        medicos.get(medicos.size()-2).getId(), fechaCita1, "Control cardiológico");
                medicalService.agendarCita(pacientes.get(pacientes.size()-1).getId(),
                        medicos.get(medicos.size()-1).getId(), fechaCita2, "Consulta pediátrica");
            }

            System.out.println("Datos de semilla creados exitosamente:");
            System.out.println("- 2 pacientes");
            System.out.println("- 2 médicos");
            System.out.println("- 2 historiales médicos");
            System.out.println("- 2 citas");

        } catch (Exception e) {
            System.out.println("Error al crear datos de semilla: " + e.getMessage());
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido");
            }
        }
    }

    private LocalDate leerFecha() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return LocalDate.parse(input, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.print("Formato inválido. Use dd/MM/yyyy: ");
            }
        }
    }

    private LocalDateTime leerFechaHora() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return LocalDateTime.parse(input, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                System.out.print("Formato inválido. Use dd/MM/yyyy HH:mm: ");
            }
        }
    }
}