package com.example.sistemarecetas.datos.farmaceutas;

import com.example.sistemarecetas.datos.farmaceutas.FarmaceutaEntity;
import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "farmaceuticosData")
@XmlAccessorType(XmlAccessType.FIELD)
public class FarmaceutaConector {
    @XmlElementWrapper(name = "farmaceuticos")
    @XmlElement(name = "Farmaceutico")
    private List<FarmaceutaEntity> farmaceuticos = new ArrayList();

    public List<FarmaceutaEntity> getFarmaceuticos() {
        return farmaceuticos;
    }

    public void setFarmaceuticos(List<FarmaceutaEntity> farmaceuticos) {
        this.farmaceuticos = farmaceuticos;
    }
}