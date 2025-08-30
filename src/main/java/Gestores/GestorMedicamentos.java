package Gestores;

import Model.Medicamento;
import java.util.ArrayList;
import java.util.List;

public class GestorMedicamentos {
    private static GestorMedicamentos instancia = new GestorMedicamentos();
    private List<Medicamento> medicamentos;

    private GestorMedicamentos() {
        medicamentos = new ArrayList<>();
    }

    public static GestorMedicamentos getInstancia() {
        return instancia;
    }

    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public void agregarMedicamento(Medicamento m) {
        medicamentos.add(m);
    }

    public void consultarTodosMedicamentos() {
        if (medicamentos.isEmpty()) {
            return;
        }
        for (Medicamento m : medicamentos) {
            System.out.println(m);
        }
    }

    public Medicamento buscarPorCodigo(String codigo) {
        for (Medicamento medi : medicamentos) {
            if (medi.getCodigo().equals(codigo)) {
                return medi;
            }
        }
        return null;
    }

    public Medicamento buscarPorDescripcion(String nombre) {
        for (Medicamento medica : medicamentos) {
            if (medica.getDescripcion().toLowerCase().contains(nombre.toLowerCase())) {
                return medica;
            }
        }
        return null;
    }

    public boolean modificarMedicamento(String codigo, String nuevoNombre, String nuevaPresentacion, String nuevoCodigo) {
        Medicamento m = buscarPorCodigo(codigo);
        if (m != null) {
            m.setCodigo(nuevoCodigo);
            m.setNombre(nuevoNombre);
            m.setDescripcion(nuevaPresentacion);
            return true;
        }
        return false;
    }

    public void eliminarMedicamento(Medicamento medi) {
        medicamentos.remove(medi);
    }
}