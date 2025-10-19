package com.example.sistemarecetas.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Receta {
    int id;
    private Paciente paciente;
    private List<Prescripcion> medicamentos;
    private LocalDate fechaConfeccion;
    private LocalDate fechaRetiro;
    private int confeccionado;
    private String estado;

    public Receta(int id, Paciente paciente, List<Prescripcion> medicamentos,
                  LocalDate fechaConfeccion, LocalDate fechaRetiro,
                  int confeccionado, String estado) {
        this.id = id;
        this.paciente = paciente;
        this.medicamentos = new ArrayList<>(medicamentos);
        this.fechaConfeccion = fechaConfeccion;
        this.fechaRetiro = fechaRetiro;
        this.confeccionado = confeccionado;
        this.estado = estado;
    }

    public int getId() { return id;}

    public  void setId(int id) { this.id = id; }

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

    public int getConfeccionado() {
        return confeccionado;
    }

    public void setConfeccionado(int confeccionado) {
        this.confeccionado = confeccionado;
    }

    public String getEstado() {return estado;}

    public void setEstado(String estado) {
        this.estado = estado;
    }
}