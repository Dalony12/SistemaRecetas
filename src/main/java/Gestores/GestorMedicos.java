package Gestores;

import Backend.Medico;
import java.util.ArrayList;
import java.util.List;

public class GestorMedicos {
    private List<Medico> medicos;

    public GestorMedicos() {
        this.medicos = new ArrayList<>();
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    public void setMedicos(List<Medico> medicos) {
        this.medicos = medicos;
    }

    public void agregarMedico(Medico m) {
        medicos.add(m);
    }

    public void consultarTodosMedicos() {
        if (medicos.isEmpty()) {
            return;
        }
        for (Medico m : medicos) {
            System.out.println(m);
        }
    }

    public Medico buscarPorId(int id) {
        for (Medico m : medicos) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    public Medico buscarPorNombre(String nombre) {
        for (Medico m : medicos) {
            if (m.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                return m;
            }
        }
        return null;
    }

    public boolean modificarMedico(int id, String nuevoNombre, String nuevaEspecialidad, int nuevoId, String nuevaClave) {
        Medico m = buscarPorId(id);
        if (m != null) {
            m.setId(nuevoId);
            m.setNombre(nuevoNombre);
            m.setEspecialidad(nuevaEspecialidad);
            m.setClave(nuevaClave);
            return true;
        }
        return false;
    }

    public boolean eliminarMedico(int id) {
        Medico m = buscarPorId(id);
        if (m != null) {
            medicos.remove(m);
            return true;
        }
        return false;
    }
}
