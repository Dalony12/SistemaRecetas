package com.example.sistemarecetas.datos.recetas;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "recetas")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecetaConector {

    @XmlElement(name = "Receta")
    private List<RecetaEntity> recetas = new ArrayList<>();

    public List<RecetaEntity> getRecetas() {
        return recetas;
    }

    public void setRecetas(List<RecetaEntity> recetas) {
        this.recetas = recetas;
    }
}