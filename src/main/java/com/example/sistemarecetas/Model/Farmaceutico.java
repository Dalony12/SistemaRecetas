package com.example.sistemarecetas.Model;

public class Farmaceutico extends Usuario {

    public Farmaceutico(String id, String nombre, String password) {
        super(id, nombre, password);
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Medico [ID: " + getId() +
                ", Nombre: " + getNombre() + "]");
    }
}
