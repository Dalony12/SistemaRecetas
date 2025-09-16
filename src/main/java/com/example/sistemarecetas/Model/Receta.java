package com.example.sistemarecetas.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Receta {

    private static int contador = 1;

    private String codigo;
    private Paciente paciente;
    private List<Prescripcion> medicamentos;
    private LocalDate fechaConfeccion;
    private LocalDate fechaRetiro;
    private int confeccionado;
    private String estado;

    public Receta(Paciente paciente, List<Prescripcion> medicamentos, LocalDate fechaRetiro) {
        this.codigo = generarCodigo();
        this.paciente = paciente;
        this.medicamentos = new ArrayList<>(medicamentos);
        this.fechaConfeccion = LocalDate.now();
        this.fechaRetiro = fechaRetiro;
        this.confeccionado = 1;
        this.estado = "Confeccionada";
    }

    public Receta(String codigo, Paciente paciente, List<Prescripcion> medicamentos,
                  LocalDate fechaConfeccion, LocalDate fechaRetiro,
                  int confeccionado, String estado) {
        this.codigo = codigo;
        this.paciente = paciente;
        this.medicamentos = new ArrayList<>(medicamentos);
        this.fechaConfeccion = fechaConfeccion;
        this.fechaRetiro = fechaRetiro;
        this.confeccionado = confeccionado;
        this.estado = estado;
    }


    private static String generarCodigo() {
        return String.format("REC-%03d", contador++);
    }

    public String getCodigo() {
        return codigo;
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