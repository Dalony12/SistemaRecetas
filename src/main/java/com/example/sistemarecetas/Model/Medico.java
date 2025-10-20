package com.example.sistemarecetas.Model;

public class Medico extends Usuario {
    private String especialidad;

    // ===== Constructor para crear un nuevo médico (ID asignado por la BD) =====
    public Medico(String identificacion, String nombre, String especialidad, String password) {
        super(0, identificacion, nombre, password); // ID = 0 temporal
        this.especialidad = especialidad != null ? especialidad : "";
    }

    // ===== Constructor completo para cuando tenemos todos los datos desde la BD =====
    public Medico(int id, String identificacion, String nombre, String especialidad, String password) {
        super(id, identificacion, nombre, password);
        this.especialidad = especialidad != null ? especialidad : "";
    }

    // ===== Getters y Setters =====
    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad != null ? especialidad : "";
    }

    // ===== Método de utilidad =====
    public void mostrarInfo() {
        System.out.println("Medico [ID: " + getId() +
                ", Nombre: " + getNombre() +
                ", Especialidad: " + getEspecialidad() +
                ", Identificación: " + getIdentificacion() + "]");
    }
}