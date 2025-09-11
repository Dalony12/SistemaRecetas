package com.example.sistemarecetas.datos.medicamentos;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "medicamentos")
@XmlAccessorType(XmlAccessType.FIELD)
public class MedicamentoConector {

    @XmlElement(name = "medicamento")
    private List<MedicamentoEntity> medicamentos = new ArrayList<>();

    public List<MedicamentoEntity> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<MedicamentoEntity> medicamentos) {
        this.medicamentos = medicamentos;
    }
}