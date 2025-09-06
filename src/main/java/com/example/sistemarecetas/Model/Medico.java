package com.example.sistemarecetas.Model;

public class Medico extends Usuario  {
    private String especialidad;

    public Medico(String id, String nombre, String password, String especialidad) {
        super(id, nombre, password);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public String getNombre() {
        return super.getNombre();
    }


    @Override
    public void mostrarInfo() {
        System.out.println("Medico [ID: " + getId() +
                ", Nombre: " + getNombre() +
                ", Especialidad: " + getEspecialidad() + "]");
    }
}
