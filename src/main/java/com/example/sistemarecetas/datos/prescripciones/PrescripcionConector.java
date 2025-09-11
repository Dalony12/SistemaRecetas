package com.example.sistemarecetas.datos.prescripciones;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "prescripciones")
@XmlAccessorType(XmlAccessType.FIELD)
public class PrescripcionConector {
    @XmlElement(name = "Prescripcion")
    private List<PrescripcionEntity> prescripciones = new ArrayList();

    public List<PrescripcionEntity> getPrescripciones() {
        return prescripciones;
    }

    public void setPrescripciones(List<PrescripcionEntity> prescripciones) {
        this.prescripciones = prescripciones;
    }
}