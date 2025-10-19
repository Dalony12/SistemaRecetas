package com.example.sistemarecetas.Model;

public class Medico extends Usuario {
    private String especialidad;

    // Constructor para crear un nuevo médico (ID se asigna por BD)
    public Medico(String identificacion, String nombre, String especialidad) {
        super(0, identificacion, nombre, identificacion); // ID = 0 temporal
        this.especialidad = especialidad;
    }

    // Constructor completo para cuando tenemos ID desde la BD
    public Medico(int id, String identificacion, String nombre, String especialidad) {
        super(id, identificacion, nombre, identificacion);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public void mostrarInfo() {
        System.out.println("Medico [ID: " + getId() +
                ", Nombre: " + getNombre() +
                ", Especialidad: " + getEspecialidad() + "]");
    }
}
