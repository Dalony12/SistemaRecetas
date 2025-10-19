package com.example.sistemarecetas.Model;

public abstract class Persona {
    private int id;
    private String identificacion;
    private String nombre;

    public Persona(int id, String identificacion, String nombre) {
        this.id = id; this.identificacion = identificacion ; this.nombre = nombre;
    }

    public String getIdentificacion() { return identificacion;}

    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
