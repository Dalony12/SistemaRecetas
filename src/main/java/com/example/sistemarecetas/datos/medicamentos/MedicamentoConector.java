package com.example.sistemarecetas.datos.medicamentos;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "clientesData")
@XmlAccessorType(XmlAccessType.FIELD)
public class MedicamentoConector {
    @XmlElementWrapper(name = "clientes")
    @XmlElement(name = "Cliente")
    private List<MedicamentoEntity> clientes = new ArrayList();

    public List<MedicamentoEntity> getClientes() {
        return clientes;
    }

    public void setClientes(List<MedicamentoEntity> clientes) {
        this.clientes = clientes;
    }
}