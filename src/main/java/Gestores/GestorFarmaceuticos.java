package Gestores;

import Backend.Farmaceutico;
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

    public void agregarMedicamento(Farmaceutico f) {
        farmaceuticos.add(f);
    }

    public void consultarTodosFarmaceuticos() {
        if (farmaceuticos.isEmpty()) return;
        for (Farmaceutico f : farmaceuticos) System.out.println(f);
    }

    public Farmaceutico buscarPorid(int id) {
        for (Farmaceutico farma : farmaceuticos) if (farma.getId() == id) return farma;
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

    public boolean modificarFarmaceutico(int id, String nuevoNombre, String nuevaClave, int nuevoid) {
        Farmaceutico farma = buscarPorid(id);
        if (farma != null) {
            farma.setId(nuevoid);
            farma.setNombre(nuevoNombre);
            farma.setClave(nuevaClave);
            return true;
        }
        return false;
    }

    public boolean eliminarFarmaceutico (int id) {
        Farmaceutico f = buscarPorid(id);
        if (f != null) {
            farmaceuticos.remove(f);
            return true;
        }
        return false;
    }
}
