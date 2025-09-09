package com.example.sistemarecetas.datos.pacientes;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "clientesData")
@XmlAccessorType(XmlAccessType.FIELD)
public class PacienteConector {
    @XmlElementWrapper(name = "clientes")
    @XmlElement(name = "Cliente")
    private List<PacienteEntity> clientes = new ArrayList();

    public List<PacienteEntity> getClientes() {
        return clientes;
    }

    public void setClientes(List<PacienteEntity> clientes) {
        this.clientes = clientes;
    }
}