package com.example.sistemarecetas.Model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UsuarioActivo {

    private final StringProperty id;
    private final BooleanProperty seleccionado;

    public UsuarioActivo(String id) {
        this.id = new SimpleStringProperty(id);
        this.seleccionado = new SimpleBooleanProperty(false);
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public boolean isSeleccionado() {
        return seleccionado.get();
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado.set(seleccionado);
    }

    public BooleanProperty seleccionadoProperty() {
        return seleccionado;
    }
}
