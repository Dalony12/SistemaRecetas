package com.example.sistemarecetas.Model;

public class Administrador extends Usuario{

    public Administrador(int id, String identificacion ,String nombre, String password) {
        super(id, identificacion, nombre, password);
    }

    public void mostrarInfo() {
        System.out.println("Administrador [ID: " + getId() + ", Nombre: " + getNombre() + "]");
    }
}
