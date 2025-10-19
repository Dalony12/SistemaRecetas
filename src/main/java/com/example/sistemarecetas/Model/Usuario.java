package com.example.sistemarecetas.Model;

public abstract class Usuario extends Persona {
    private String password;

    public Usuario(int id, String identificacion,String nombre, String password) {
        super(id, identificacion ,nombre); this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
