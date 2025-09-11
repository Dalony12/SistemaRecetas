package com.example.sistemarecetas.datos.pacientes;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "pacientes")
@XmlAccessorType(XmlAccessType.FIELD)
public class PacienteConector {
    @XmlElement(name = "Paciente")
    private List<PacienteEntity> pacientes = new ArrayList();

    public List<PacienteEntity> getPacientes() {
        return pacientes;
    }

    public void setPacientes(List<PacienteEntity> pacientes) {
        this.pacientes = pacientes;
    }
}
