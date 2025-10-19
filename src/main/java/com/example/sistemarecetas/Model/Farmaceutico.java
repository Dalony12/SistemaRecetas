package com.example.sistemarecetas.Model;

public class Farmaceutico extends Usuario {

    public Farmaceutico(int id, String identificacion ,String nombre, String password) {
        super(id, identificacion, nombre, password);
    }

    public void mostrarInfo() {
        System.out.println("Medico [ID: " + getId() +
                ", Nombre: " + getNombre() + "]");
    }
}
