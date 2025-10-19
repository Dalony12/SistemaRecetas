package com.example.sistemarecetas.Model;

public class Medico extends Usuario  {
    private String especialidad;

    public Medico(int id, String identificacion ,String nombre, String password, String especialidad) {
        super(id, identificacion, nombre, password);
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
