package Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Receta {
    private Paciente paciente;
    private List<Prescripcion> medicamentos;
    private LocalDate fechaConfeccion;
    private LocalDate fechaRetiro;
    private boolean confeccionada;

    public Receta(Paciente paciente) {
        this.paciente = paciente;
        this.medicamentos = new ArrayList<>();
        this.fechaConfeccion = LocalDate.now();
        this.confeccionada = false;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public List<Prescripcion> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<Prescripcion> medicamentos) {
        this.medicamentos = medicamentos;
    }

    public LocalDate getFechaConfeccion() {
        return fechaConfeccion;
    }

    public void setFechaConfeccion(LocalDate fechaConfeccion) {
        this.fechaConfeccion = fechaConfeccion;
    }

    public LocalDate getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(LocalDate fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }

    public boolean isConfeccionada() {
        return confeccionada;
    }

    public void setConfeccionada(boolean confeccionada) {
        this.confeccionada = confeccionada;
    }

    @Override
    public String toString() {
        String resultado = "Receta de " + paciente + ":\n";
        resultado += "Fecha confección: " + fechaConfeccion + "\n";
        resultado += "Fecha retiro: " + fechaRetiro + "\n";
        resultado += "Confeccionada: " + (confeccionada ? "Sí" : "No") + "\n";
        resultado += "Medicamentos:\n";

        for (Prescripcion prescripcion : medicamentos) {

            resultado += "- " + prescripcion.toString() + "\n";
        }

        return resultado;
    }
}