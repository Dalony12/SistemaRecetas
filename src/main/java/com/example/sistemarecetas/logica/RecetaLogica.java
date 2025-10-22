package com.example.sistemarecetas.logica;

import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.Model.Paciente;
import com.example.sistemarecetas.datos.PacienteDatos;
import com.example.sistemarecetas.datos.PrescripcionDatos;
import com.example.sistemarecetas.datos.RecetaDatos;

import java.sql.SQLException;
import java.util.List;

public class RecetaLogica {

    private final RecetaDatos recetaStore = new RecetaDatos();
    private final PrescripcionDatos prescripcionStore = new PrescripcionDatos();
    private final PacienteDatos pacienteStore = new PacienteDatos();

    /** Retorna todas las recetas con sus prescripciones y pacientes asociados */
    public List<Receta> findAll() {
        try {
            List<Receta> recetas = recetaStore.findAll();
            for (Receta r : recetas) {
                // Cargar prescripciones de la receta
                List<Prescripcion> prescripciones = prescripcionStore.findByRecetaId(r.getId());
                r.setMedicamentos(prescripciones);

                // Cargar paciente
                Paciente paciente = pacienteStore.findById(r.getPaciente().getId());
                r.setPaciente(paciente);
            }
            return recetas;
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /** Encuentra receta por ID */
    public Receta findById(int id) {
        try {
            Receta receta = recetaStore.findById(id);
            if (receta != null) {
                receta.setMedicamentos(prescripcionStore.findByRecetaId(id));
                receta.setPaciente(pacienteStore.findById(receta.getPaciente().getId()));
            }
            return receta;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Crea una receta con sus prescripciones */
    public Receta create(Receta nueva) {
        try {
            // Generar código consecutivo si no existe
            if (nueva.getCodigo() == null || nueva.getCodigo().isEmpty()) {
                String codigo = recetaStore.generarCodigoReceta();
                nueva.setCodigo(codigo);
            }

            // Insertar receta y sus prescripciones (transacción interna)
            Receta recetaInsertada = recetaStore.insert(nueva);

            // Recargar prescripciones desde BD (ya se guardaron dentro del insert)
            if (recetaInsertada != null) {
                recetaInsertada.setMedicamentos(
                        prescripcionStore.findByRecetaId(recetaInsertada.getId())
                );
            }

            return recetaInsertada;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Actualiza receta y sus prescripciones */
    public Receta update(Receta r) {
        try {
            Receta updated = recetaStore.update(r);
            if (updated != null) {
                // Actualizar cada prescripcion (requiere que ya tengan ID)
                for (Prescripcion p : r.getMedicamentos()) {
                    if (p.getId() > 0) {
                        prescripcionStore.update(p, r.getId());
                    } else {
                        // Si es nueva prescripción, insertarla
                        prescripcionStore.insert(p, r.getId());
                    }
                }
                updated.setMedicamentos(prescripcionStore.findByRecetaId(r.getId()));
            }
            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Elimina receta y sus prescripciones */
    public boolean delete(int id) {
        try {
            // Primero eliminar prescripciones
            prescripcionStore.deleteByRecetaId(id);
            // Luego eliminar receta
            return recetaStore.delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}