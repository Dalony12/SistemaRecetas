package com.example.sistemarecetas.datos.recetas;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "clientesData")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecetaConector {
    @XmlElementWrapper(name = "clientes")
    @XmlElement(name = "Cliente")
    private List<RecetaEntity> clientes = new ArrayList();

    public List<RecetaEntity> getClientes() {
        return clientes;
    }

    public void setClientes(List<RecetaEntity> clientes) {
        this.clientes = clientes;
    }
}