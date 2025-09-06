package com.example.sistemarecetas.Gestores;

import java.util.ArrayList;
import java.util.List;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.Model.Prescripcion;

public class GestorRecetas {

    private static GestorRecetas instancia = new GestorRecetas();
    private List<Receta> recetas;

    private GestorRecetas() {
        recetas = new ArrayList<>();
    }

    public static GestorRecetas getInstancia() {
        return instancia;
    }

    public void agregarReceta(Receta receta) {
        recetas.add(receta);
    }

    public List<Receta> getRecetas() {
        return recetas;
    }

    public void eliminarReceta(Receta receta) {
        recetas.remove(receta);
    }

    public boolean modificarReceta(String codigoMedicamento, Receta nuevaReceta) {
        for (int i = 0; i < recetas.size(); i++) {
            Receta r = recetas.get(i);
            for (Prescripcion p : r.getMedicamentos()) {
                if (p.getMedicamento().getCodigo().equals(codigoMedicamento)) {
                    recetas.set(i, nuevaReceta);
                    return true;
                }
            }
        }
        return false;
    }

    public Receta buscarPorCodigo(String codigo) {
        for (Receta r : recetas) {
            for (Prescripcion p : r.getMedicamentos()) {
                if (p.getMedicamento().getCodigo().equals(codigo)) {
                    return r;
                }
            }
        }
        return null;
    }

    public List<Receta> buscarPorDescripcion(String descripcion) {
        List<Receta> resultado = new ArrayList<>();
        for (Receta r : recetas) {
            for (Prescripcion p : r.getMedicamentos()) {
                if (p.getMedicamento().getDescripcion().toLowerCase().contains(descripcion.toLowerCase())) {
                    resultado.add(r);
                    break;
                }
            }
        }
        return resultado;
    }

    public void mostrarRecetas() {
        for (Receta r : recetas) {
            System.out.println(r);
        }
    }

    public boolean actualizarEstadoReceta(Receta receta, String nuevoEstado) {
        GestorMedicamentos gestorMeds = GestorMedicamentos.getInstancia();

        for (Prescripcion pres : receta.getMedicamentos()) {
            Medicamento m = gestorMeds.buscarPorCodigo(pres.getMedicamento().getCodigo());
            if (m == null) {
                return false; // fallo validación
            }
        }

        receta.setEstado(nuevoEstado);
        return true;
    }
}
