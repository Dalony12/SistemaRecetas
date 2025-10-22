package com.example.sistemarecetas.logica;

import com.example.sistemarecetas.Model.Farmaceutico;
import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.Model.Medico;
import com.example.sistemarecetas.datos.MedicoDatos;

import java.sql.SQLException;
import java.util.List;

public class MedicoLogica {

    private final MedicoDatos store = new MedicoDatos();

    public List<Medico> findAll() throws SQLException {
        return store.findAll();
    }

    public Medico findByIdentificacion(String identificacion) throws SQLException {
        if (identificacion == null || identificacion.isBlank()) return null;
        return store.findByIdentificacion(identificacion);
    }

    public List<Medico> search(String identificacion, String nombre) throws SQLException {
        return store.search(identificacion, nombre);
    }

    public List<Medico> searchByCodigo(String codigo) throws SQLException {
        List<Medico> todos = store.search(codigo, "");
        return todos.stream()
                .filter(m -> m.getIdentificacion().toLowerCase().contains(codigo.toLowerCase()))
                .toList();
    }

    public Medico create(Medico m) throws SQLException {
        validarCampos(m, true);
        return store.insert(m);
    }

    public Medico update(Medico m) throws SQLException {
        validarCampos(m, false);
        return store.update(m);
    }

    public boolean deleteByIdentificacion(String identificacion) throws SQLException {
        if (identificacion == null || identificacion.isBlank()) return false;
        return store.deleteByIdentificacion(identificacion);
    }

    private void validarCampos(Medico m, boolean esNuevo) {
        if (m.getNombre() == null || m.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del médico es obligatorio.");
        if (m.getIdentificacion() == null || m.getIdentificacion().isBlank())
            throw new IllegalArgumentException("La identificación del médico es obligatoria.");
        if (m.getEspecialidad() == null) m.setEspecialidad("");
        if (esNuevo && (m.getPassword() == null || m.getPassword().isBlank()))
            throw new IllegalArgumentException("La contraseña es obligatoria para nuevos médicos.");
    }
}