package Gestores;

import Backend.Medico;
import java.util.ArrayList;
import java.util.List;

public class GestorMedicos {
    private static GestorMedicos instancia = new GestorMedicos();
    private List<Medico> medicos;

    private GestorMedicos() {
        medicos = new ArrayList<>();
    }

    public static GestorMedicos getInstancia() {
        return instancia;
    }

    public List<Medico> getMedicos() {
        return medicos;
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

    public Medico buscarPorId(String id) {
        for (Medico m : medicos) {
            if (m.getId().equals(id)) {
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

    public boolean modificarMedico( String id, String nuevoNombre, String nuevaEspecialidad, String nuevoId, String nuevaClave) {
        Medico m = buscarPorId(id);
        if (m != null) {
            m.setId(nuevoId);
            m.setNombre(nuevoNombre);
            m.setEspecialidad(nuevaEspecialidad);
            return true;
        }
        return false;
    }

    public void eliminarMedico(Medico m) {
        medicos.remove(m);
    }
}
