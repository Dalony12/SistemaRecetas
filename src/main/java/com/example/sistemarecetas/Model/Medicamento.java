package com.example.sistemarecetas.Model;

public class Medicamento {
    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String presentacion;

    // Constructor vacío
    public Medicamento() {}

    // Constructor sin ID (nuevo medicamento)
    public Medicamento(String codigo, String nombre, String presentacion, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.presentacion = presentacion;
        this.descripcion = descripcion;
    }

    // Constructor completo con ID (para cargar desde DB)
    public Medicamento(int id, String codigo, String nombre, String presentacion, String descripcion) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.presentacion = presentacion;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getPresentacion() {return presentacion;}

    public void setPresentacion(String presentacion) { this.presentacion = presentacion;}

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String toString() {
        return "Medicamento [Codigo: " + getCodigo() +
                ", Nombre: " + getNombre() +
                ", Presentación: " + getDescripcion() + "]";
    }
}


