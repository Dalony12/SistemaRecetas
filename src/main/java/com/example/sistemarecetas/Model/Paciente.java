package com.example.sistemarecetas.Model;
import java.time.LocalDate;

public class Paciente extends Persona {
    private int telefono;
    private LocalDate fechaNacimiento;

    public Paciente(int id, String identificacion, String nombre, int telefono, LocalDate fechaNacimiento) {
        super(id, identificacion, nombre);
        this.telefono = telefono; this.fechaNacimiento = fechaNacimiento;
    }

    public Paciente(String identificacion, String nombre, int telefono, LocalDate fechaNacimiento) {
        super(0, identificacion, nombre);
        this.telefono = telefono; this.fechaNacimiento = fechaNacimiento;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void mostrarInfo() {
        System.out.println("Paciente [ID: " + getId() +
                ", Nombre: " + getNombre() +
                ", Telefono: " + getTelefono() +
                ", Fecha Nacimiento: " + getFechaNacimiento() + "]");
    }
}