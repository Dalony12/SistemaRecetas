package com.example.sistemarecetas.datos.prescripciones;

import com.example.sistemarecetas.Model.Medicamento;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"medicamento", "cantidad", "indicaciones", "duracionDias"})
public class PrescripcionEntity {

    @XmlElement
    private Medicamento medicamento;

    @XmlElement
    private int cantidad;

    @XmlElement
    private String indicaciones;

    @XmlElement
    private int duracionDias;

    // Constructor vacío requerido por JAXB
    public PrescripcionEntity() {}

    // Constructor completo para uso en lógica
    public PrescripcionEntity(Medicamento medicamento, int cantidad, String indicaciones, int duracionDias) {
        this.medicamento = medicamento;
        this.cantidad = cantidad;
        this.indicaciones = indicaciones;
        this.duracionDias = duracionDias;
    }

    // Getters y setters
    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(int duracionDias) {
        this.duracionDias = duracionDias;
    }
}
