package com.example.sistemarecetas.Gestores;

import com.example.sistemarecetas.Model.Medico;
import java.util.ArrayList;
import java.util.List;

public class GestorMedicos {
    private static GestorMedicos instancia = new GestorMedicos();
    private List<Medico> medicos;

    private GestorMedicos() {
        medicos = new ArrayList<>();
        agregarDatosPrueba();
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

    public boolean actualizarPassword(String id, String nuevaClave) {
        Medico m = buscarPorId(id);
        if (m != null) {
            m.setPassword(nuevaClave);
            return true;
        }
        return false;
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

    public void agregarDatosPrueba() {
        medicos.add(new Medico("med-111", "Dr. Juan Perez", "med-111", "Medico General"));
        medicos.add(new Medico("med-222", "Dra. Maria Gomez", "med-222", "Pediatra"));
        medicos.add(new Medico("med-333", "Dr. Carlos Ruiz", "med-333", "Cardiologo"));
    }

}

