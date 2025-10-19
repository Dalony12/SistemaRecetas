package com.example.sistemarecetas.Model;

public class Prescripcion {
    private int id;
    private Medicamento medicamento;
    private int cantidad;
    private String indicaciones;
    private int duracionDias;

    public Prescripcion(Medicamento medicamento, int cantidad, String indicaciones, int duracionDias) {
        this.id = id;
        this.medicamento = medicamento;
        this.cantidad = cantidad;
        this.indicaciones = indicaciones;
        this.duracionDias = duracionDias;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Medicamento getMedicamento() { return medicamento;}

    public void setMedicamento(Medicamento medicamento) { this.medicamento = medicamento;}

    public int getCantidad() { return cantidad;}

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public int getDuracionDias() { return duracionDias; }

    public void setDuracionDias(int duracionDias) { this.duracionDias = duracionDias; }

    public String getIndicaciones() { return indicaciones;}

    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones;}

    @Override
    public String toString() {
        return "Prescripcion{" +
                "medicamento=" + medicamento.toString() +
                ", cantidad=" + cantidad +
                ", indicaciones='" + indicaciones + '\'' +
                ", duracionDias=" + duracionDias +
                '}';
    }
}