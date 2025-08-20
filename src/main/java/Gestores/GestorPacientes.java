package Gestores;

import Backend.Paciente;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class GestorPacientes {
    private List<Paciente> pacientes;

    public GestorPacientes() {
        this.pacientes = new ArrayList<>();
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public void setPacientes(List<Paciente> pacientes) {
        this.pacientes = pacientes;
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

    public Paciente buscarPorId(int id) {
        for (Paciente p : pacientes) {
            if (p.getId() == id) {
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

    public boolean modificarPaciente(int id, String nuevoNombre, int nuevoId, int nuevotelefono, LocalDate nuevafecha) {
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

    public boolean eliminarPaciente(int id) {
        Paciente p = buscarPorId(id);
        if (p != null) {
            pacientes.remove(p);
            return true;
        }
        return false;
    }
}
