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
    private String estado;

    public Receta(Paciente paciente, List<Prescripcion> medicamentos, LocalDate fechaRetiro, boolean confeccionada) {
        this.paciente = paciente;
        this.medicamentos = new ArrayList<>(medicamentos);
        this.fechaConfeccion = LocalDate.now();
        this.fechaRetiro = fechaRetiro;
        this.confeccionada = confeccionada;
        this.estado = "En proceso";
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

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
}