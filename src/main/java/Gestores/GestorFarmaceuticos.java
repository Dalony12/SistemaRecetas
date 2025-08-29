package Gestores;

import Model.Farmaceutico;
import java.util.ArrayList;
import java.util.List;

public class GestorFarmaceuticos {
    private List<Farmaceutico> farmaceuticos;

    public GestorFarmaceuticos() {
        this.farmaceuticos = new ArrayList<>();
    }

    public List<Farmaceutico> getFarmaceuticos() {
        return farmaceuticos;
    }

    public void setFarmaceuticos(List<Farmaceutico> farmaceuticos) {
        this.farmaceuticos = farmaceuticos;
    }

    public void agregarFarmaceuta(Farmaceutico f) {
        farmaceuticos.add(f);
    }

    public void consultarTodosFarmaceuticos() {
        if (farmaceuticos.isEmpty()) {
            return;
        }
        for (Farmaceutico f : farmaceuticos) {
            System.out.println(f);
        }
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
}
