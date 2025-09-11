package com.example.sistemarecetas.datos.medicos;


import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "medicos")
@XmlAccessorType(XmlAccessType.FIELD)
public class MedicoConector {
    @XmlElement(name = "Medico")
    private List<MedicoEntity> medicos = new ArrayList();

    public List<MedicoEntity> getMedicos() {
        return medicos;
    }

    public void setMedicos(List<MedicoEntity> medicos) {
        this.medicos = medicos;
    }
}