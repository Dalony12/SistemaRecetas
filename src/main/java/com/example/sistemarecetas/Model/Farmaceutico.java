package com.example.sistemarecetas.Model;

public class Farmaceutico extends Usuario {

    /** Constructor para cargar desde la base de datos (con ID conocido) */
    public Farmaceutico(int id, String identificacion, String nombre, String password) {
        super(id, identificacion, nombre, password);
    }

    /** Constructor para crear un nuevo farmacéutico (sin ID aún), password por defecto = "user" */
    public Farmaceutico(String nombre, String identificacion) {
        super(0, identificacion, nombre, identificacion); // id = 0 temporal, se asigna al insertar en DB
    }

    public void mostrarInfo() {
        System.out.println("Medico [ID: " + getId() +
                ", Nombre: " + getNombre() + "]");
    }
}
