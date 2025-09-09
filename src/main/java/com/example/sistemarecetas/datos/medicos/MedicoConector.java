package com.example.sistemarecetas.datos.medicos;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "clientesData")
@XmlAccessorType(XmlAccessType.FIELD)
public class MedicoConector {
    @XmlElementWrapper(name = "clientes")
    @XmlElement(name = "Cliente")
    private List<MedicoEntity> clientes = new ArrayList();

    public List<MedicoEntity> getClientes() {
        return clientes;
    }

    public void setClientes(List<MedicoEntity> clientes) {
        this.clientes = clientes;
    }
}