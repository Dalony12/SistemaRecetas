package com.example.sistemarecetas.logica;

import com.example.sistemarecetas.Model.Paciente;
import com.example.sistemarecetas.datos.PacienteDatos;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PacienteLogica {

    private final PacienteDatos store = new PacienteDatos();

    public List<Paciente> findAll() throws SQLException {
        return store.findAll();
    }

    public Paciente findById(int id) throws SQLException {
        if (id <= 0) return null;
        return store.findById(id);
    }

    public Paciente create(Paciente nuevo) throws SQLException {
        validarNuevo(nuevo);
        return store.insert(nuevo);
    }

    public Paciente update(Paciente p) throws SQLException {
        if (p == null || p.getId() <= 0)
            throw new IllegalArgumentException("El paciente a actualizar requiere un ID válido.");
        validarCampos(p);
        return store.update(p);
    }

    public boolean deleteById(int id) throws SQLException {
        if (id <= 0) return false;
        return store.delete(id);
    }

    private void validarNuevo(Paciente p) {
        if (p == null) throw new IllegalArgumentException("Paciente nulo.");
        validarCampos(p);
    }

    public Paciente findByIdentificacion(String identificacion) throws SQLException {
        if (identificacion == null || identificacion.isBlank()) return null;
        return store.findByIdentificacion(identificacion);
    }

    public boolean deleteByIdentificacion(String identificacion) throws SQLException {
        if (identificacion == null || identificacion.isBlank()) return false;
        return store.deleteByIdentificacion(identificacion);
    }

    public List<Paciente> search(String identificacion, String nombre) throws SQLException {
        return store.search(identificacion, nombre);
    }

    private void validarCampos(Paciente p) {
        if (p.getNombre() == null || p.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (p.getIdentificacion() == null || p.getIdentificacion().isBlank())
            throw new IllegalArgumentException("La identificación es obligatoria.");
        if (p.getFechaNacimiento() != null && p.getFechaNacimiento().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        if (p.getTelefono() < 0)
            throw new IllegalArgumentException("El teléfono debe ser positivo.");
    }
}
