package Gestores;

import java.util.ArrayList;
import java.util.List;
import Backend.Receta;
import Backend.Prescripcion;

public class GestorRecetas {

    private List<Receta> recetas;

    public GestorRecetas() {
        recetas = new ArrayList<>();
    }

    public void agregarReceta(Receta receta) {
        recetas.add(receta);
    }


    public void eliminarReceta(Receta receta) {
        recetas.remove(receta);
    }

    public boolean modificarReceta(int codigoMedicamento, Receta nuevaReceta) {
        for (int i = 0; i < recetas.size(); i++) {
            Receta r = recetas.get(i);
            for (Prescripcion p : r.getMedicamentos()) {
                if (p.getMedicamento().getCodigo() == codigoMedicamento) {
                    recetas.set(i, nuevaReceta);
                    return true;
                }
            }
        }
        return false;
    }

    public Receta buscarPorCodigo(int codigo) {
        for (Receta r : recetas) {
            for (Prescripcion p : r.getMedicamentos()) {
                if (p.getMedicamento().getCodigo() == codigo) {
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
}
