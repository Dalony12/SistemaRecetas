package com.example.sistemarecetas.datos.farmaceutas;

import com.example.sistemarecetas.datos.farmaceutas.FarmaceutaEntity;
import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "clientesData")
@XmlAccessorType(XmlAccessType.FIELD)
public class FarmaceutaConector {
    @XmlElementWrapper(name = "clientes")
    @XmlElement(name = "Cliente")
    private List<FarmaceutaEntity> clientes = new ArrayList();

    public List<FarmaceutaEntity> getClientes() {
        return clientes;
    }

    public void setClientes(List<FarmaceutaEntity> clientes) {
        this.clientes = clientes;
    }
}