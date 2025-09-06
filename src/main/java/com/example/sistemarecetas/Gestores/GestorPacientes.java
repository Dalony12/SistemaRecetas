package com.example.sistemarecetas.Gestores;

import com.example.sistemarecetas.Model.Paciente;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class GestorPacientes {
    private static GestorPacientes instancia = new GestorPacientes();
    private List<Paciente> pacientes;

    private GestorPacientes() {
        pacientes = new ArrayList<>();
    }

    public static GestorPacientes getInstancia() {
        return instancia;
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public void agregarPaciente(Paciente p) {
        pacientes.add(p);
    }

    public void consultarTodosPacientes() {
        if (pacientes.isEmpty()) {
            return;
        }
        for (Paciente p : pacientes) {
            System.out.println(p);
        }
    }

    public Paciente buscarPorId(String id) {
        for (Paciente p : pacientes) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public Paciente buscarPorNombre(String nombre) {
        for (Paciente p : pacientes) {
            if (p.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                return p;
            }
        }
        return null;
    }

    public boolean modificarPaciente(String id, String nuevoNombre, String nuevoId, int nuevotelefono, LocalDate nuevafecha) {
        Paciente p = buscarPorId(id);
        if (p != null) {
            p.setId(nuevoId);
            p.setNombre(nuevoNombre);
            p.setTelefono(nuevotelefono);
            p.setFechaNacimiento(nuevafecha);
            return true;
        }
        return false;
    }

    public void eliminarPaciente(Paciente p) {
        pacientes.remove(p);
    }
}
