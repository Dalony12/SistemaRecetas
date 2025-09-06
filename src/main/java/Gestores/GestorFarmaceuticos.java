package Gestores;

import Model.Farmaceutico;
import Model.Medico;

import java.util.ArrayList;
import java.util.List;

public class GestorFarmaceuticos {
    private static GestorFarmaceuticos instancia = new GestorFarmaceuticos();
    private List<Farmaceutico> farmaceuticos;

    private GestorFarmaceuticos() {
        farmaceuticos = new ArrayList<>();
        agregarDatosPrueba();
    }

    public static GestorFarmaceuticos getInstancia() {
        return instancia;
    }

    public List<Farmaceutico> getFarmaceuticos() {
        return farmaceuticos;
    }

    public void agregarFarmaceuta(Farmaceutico f) {
        farmaceuticos.add(f);
    }

    public boolean actualizarPassword(String id, String nuevaClave) {
        Farmaceutico farma = buscarPorid(id);
        if (farma != null) {
            farma.setPassword(nuevaClave);
            return true;
        }
        return false;
    }

    public Farmaceutico buscarPorid(String id) {
        for (Farmaceutico farma : farmaceuticos) {
            if (farma.getId().equals(id)) {
                return farma;
            }
        }
        return null;
    }

    public Farmaceutico buscarPorNombre (String nombre) {
        for (Farmaceutico farma : farmaceuticos) {
            if (farma.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                return farma;
            }
        }
        return null;
    }

    public boolean modificarFarmaceutico(String id, String nuevoNombre, String nuevaClave, String nuevoid) {
        Farmaceutico farma = buscarPorid(id);
        if (farma != null) {
            farma.setId(nuevoid);
            farma.setNombre(nuevoNombre);
            return true;
        }
        return false;
    }

    public void eliminarFarmaceutico (Farmaceutico f) {
        farmaceuticos.remove(f);
    }

    public void agregarDatosPrueba() {
        farmaceuticos.add(new Farmaceutico("far-111", "Juliana Gonzalez", "far-111"));
        farmaceuticos.add(new Farmaceutico("far-222", "Victor Espinoza", "far-222"));
        farmaceuticos.add(new Farmaceutico("far-333", "Sebastian Sepulveda", "far-333"));
    }
}
