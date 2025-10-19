package com.example.sistemarecetas.logica;

import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.datos.PrescripcionDatos;

import java.sql.SQLException;
import java.util.List;

public class PrescripcionLogica {

    private final PrescripcionDatos store = new PrescripcionDatos();

    public List<Prescripcion> findAllByRecetaId(int idReceta) throws SQLException {
        if (idReceta <= 0) return List.of();
        return store.findByRecetaId(idReceta);
    }

    public Prescripcion findById(int id) throws SQLException {
        if (id <= 0) return null;
        return store.findById(id);
    }

    public Prescripcion create(Prescripcion p, int idReceta) throws SQLException {
        if (p == null || idReceta <= 0) throw new IllegalArgumentException("Prescripción o receta inválida.");
        return store.insert(p, idReceta);
    }

    public Prescripcion update(Prescripcion p, int idReceta) throws SQLException {
        if (p == null || p.getId() <= 0 || idReceta <= 0)
            throw new IllegalArgumentException("Prescripción o receta inválida.");
        return store.update(p, idReceta);
    }

    public boolean deleteById(int id) throws SQLException {
        if (id <= 0) return false;
        return store.delete(id);
    }

    public boolean deleteByRecetaId(int idReceta) throws SQLException {
        if (idReceta <= 0) return false;
        return store.deleteByRecetaId(idReceta);
    }
}