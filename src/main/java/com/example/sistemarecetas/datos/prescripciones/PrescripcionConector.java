package com.example.sistemarecetas.datos.prescripciones;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "clientesData")
@XmlAccessorType(XmlAccessType.FIELD)
public class PrescripcionConector {
    @XmlElementWrapper(name = "clientes")
    @XmlElement(name = "Cliente")
    private List<PrescripcionEntity> clientes = new ArrayList();

    public List<PrescripcionEntity> getClientes() {
        return clientes;
    }

    public void setClientes(List<PrescripcionEntity> clientes) {
        this.clientes = clientes;
    }
}