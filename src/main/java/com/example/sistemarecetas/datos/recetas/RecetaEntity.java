package com.example.sistemarecetas.datos.recetas;

import com.example.sistemarecetas.datos.LocalDateAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDate;

@XmlAccessorType(XmlAccessType.FIELD)
public class RecetaEntity {
    private String paciente;
    private String medicamentos;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaConfeccion;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaRetiro;
    private int confeccionado;
    private String estado;

    public RecetaEntity() {
    }

    public RecetaEntity(String paciente, String medicamentos, LocalDate fechaConfeccion, LocalDate fechaRetiro, int confeccionado, String estado) {
        this.paciente = paciente;
        this.medicamentos = medicamentos;
        this.fechaConfeccion = fechaConfeccion;
        this.fechaRetiro = fechaRetiro;
        this.confeccionado = confeccionado;
        this.estado = estado;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(String medicamentos) {
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
