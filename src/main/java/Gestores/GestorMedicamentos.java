package Gestores;

import Model.Medicamento;
import java.util.ArrayList;
import java.util.List;

public class GestorMedicamentos {
    private List<Medicamento> medicamentos;

    public GestorMedicamentos() {
        this.medicamentos = new ArrayList<>();
    }

    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = medicamentos;
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

    public Medicamento buscarPorCodigo(int codigo) {
        for (Medicamento medi : medicamentos) {
            if (medi.getCodigo() == codigo) {
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

    public boolean modificarMedicamento(int codigo, String nuevoNombre, String nuevaPresentacion, int nuevoCodigo) {
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