package com.example.sistemarecetas.logica.pacientes;

import com.example.sistemarecetas.Model.Paciente;
import com.example.sistemarecetas.datos.pacientes.PacienteConector;
import com.example.sistemarecetas.datos.pacientes.PacienteDatos;
import com.example.sistemarecetas.datos.pacientes.PacienteEntity;

import java.util.*;
import java.util.stream.Collectors;

/** CRUD para pacientes usando un archivo XML como almacenamiento. */
public class PacientesLogica {

    private final PacienteDatos store;

    /** rutaArchivoEj: "datos/pacientes.xml" */
    public PacientesLogica(String rutaArchivo) {
        this.store = new PacienteDatos(rutaArchivo);
    }

    // --------- Lectura ---------

    public List<Paciente> findAll() {
        PacienteConector data = store.load();
        return data.getPacientes().stream()
                .map(PacientesMapper::toModel)
                .collect(Collectors.toList());
    }

    public Optional<Paciente> findByCodigo(String codigo) {
        PacienteConector data = store.load();
        return data.getPacientes().stream()
                .filter(x -> codigo.equalsIgnoreCase(x.getId()))
                .findFirst()
                .map(PacientesMapper::toModel);
    }

    public List<Paciente> search(String id, String nombre) {
        String qId = (id == null) ? "" : id.trim().toLowerCase();
        String qNombre = (nombre == null) ? "" : nombre.trim().toLowerCase();

        PacienteConector data = store.load();

        return data.getPacientes().stream()
                .filter(x -> qId.isEmpty() || (x.getId() != null && x.getId().toLowerCase().contains(qId)))
                .filter(x -> qNombre.isEmpty() || (x.getNombre() != null && x.getNombre().toLowerCase().contains(qNombre)))
                .map(PacientesMapper::toModel)
                .toList();
    }

    // --------- Escritura ---------

    public Paciente create(Paciente nuevo) {
        validarNuevo(nuevo);
        PacienteConector data = store.load();

        if (nuevo.getId() != null && !nuevo.getId().isBlank()) {
            boolean exists = data.getPacientes().stream()
                    .anyMatch(x -> nuevo.getId().equalsIgnoreCase(x.getId()));
            if (exists) throw new IllegalArgumentException("Ya existe un paciente con ese ID.");
        }

        PacienteEntity x = PacientesMapper.toXml(nuevo);
        data.getPacientes().add(x);
        store.save(data);
        return PacientesMapper.toModel(x);
    }

    public Paciente update(Paciente paciente) {
        if (paciente == null || paciente.getId() == null || paciente.getId().isBlank())
            throw new IllegalArgumentException("El paciente a actualizar requiere un ID válido.");
        validarCampos(paciente);

        PacienteConector data = store.load();

        for (int i = 0; i < data.getPacientes().size(); i++) {
            PacienteEntity actual = data.getPacientes().get(i);
            if (actual.getId().equalsIgnoreCase(paciente.getId())) {
                data.getPacientes().set(i, PacientesMapper.toXml(paciente));
                store.save(data);
                return paciente;
            }
        }
        throw new NoSuchElementException("No existe Paciente con ID: " + paciente.getId());
    }

    public boolean deleteByCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return false;
        PacienteConector data = store.load();
        boolean removed = data.getPacientes().removeIf(x -> codigo.equalsIgnoreCase(x.getId()));
        if (removed) store.save(data);
        return removed;
    }

    // --------- Helpers ---------

    private void validarNuevo(Paciente m) {
        if (m == null) throw new IllegalArgumentException("Paciente nulo.");
        validarCampos(m);
    }

    private void validarCampos(Paciente m) {
        if (m.getId() == null || m.getId().isBlank())
            throw new IllegalArgumentException("El ID es obligatorio.");
        if (m.getNombre() == null || m.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (m.getTelefono() <= 0)
            throw new IllegalArgumentException("El teléfono debe ser un número válido.");
        if (m.getFechaNacimiento() == null)
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
    }
}
