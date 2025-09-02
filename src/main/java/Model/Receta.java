package Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Receta {
    private Paciente paciente;
    private List<Prescripcion> medicamentos;
    private LocalDate fechaConfeccion;
    private LocalDate fechaRetiro;
    private int estado;

    public Receta(Paciente paciente, List<Prescripcion> medicamentos, LocalDate fechaRetiro) {
        this.paciente = paciente;
        this.medicamentos = new ArrayList<>(medicamentos);
        this.fechaConfeccion = LocalDate.now();
        this.fechaRetiro = fechaRetiro;
        this.estado = 1;
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

    public int getEstado() {return estado;}

    public void setEstado(int estado) {
        this.estado = estado;
    }
}