package com.example.sistemarecetas.logica;

import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.datos.PrescripcionDatos;

import java.sql.SQLException;
import java.util.List;

public class PrescripcionLogica {

    private final PrescripcionDatos store = new PrescripcionDatos();

    /** Obtiene todas las prescripciones asociadas a una receta */
    public List<Prescripcion> findByRecetaId(int idReceta) throws SQLException {
        if (idReceta <= 0) return List.of();
        return store.findByRecetaId(idReceta);
    }

    /** Obtiene una prescripción por su ID */
    public Prescripcion findById(int id) throws SQLException {
        if (id <= 0) return null;
        return store.findById(id);
    }

    /** Crea una nueva prescripción asociada a una receta */
    public Prescripcion create(Prescripcion p, int idReceta) throws SQLException {
        if (p == null || idReceta <= 0) throw new IllegalArgumentException("Prescripción o receta inválida.");
        return store.insert(p, idReceta);
    }

    /** Actualiza una prescripción existente asociada a una receta */
    public Prescripcion update(Prescripcion p, int idReceta) throws SQLException {
        if (p == null || p.getId() <= 0 || idReceta <= 0)
            throw new IllegalArgumentException("Prescripción o receta inválida.");
        return store.update(p, idReceta);
    }

    /** Elimina una prescripción por su ID */
    public boolean deleteById(int id) throws SQLException {
        if (id <= 0) return false;
        return store.delete(id);
    }

    /** Elimina todas las prescripciones de una receta */
    public boolean deleteByRecetaId(int idReceta) throws SQLException {
        if (idReceta <= 0) return false;
        return store.deleteByRecetaId(idReceta);
    }
}