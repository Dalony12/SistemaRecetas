package com.example.sistemarecetas.logica.pacientes;

import com.example.sistemarecetas.Model.Paciente;
import com.example.sistemarecetas.datos.pacientes.PacienteEntity;


public class PacientesMapper {

    public static PacienteEntity toXml(Paciente m) {
        if (m == null) {
            return null;
        }
        PacienteEntity entity = new PacienteEntity();
        entity.setId(m.getId());
        entity.setNombre(m.getNombre());
        entity.setTelefono(m.getTelefono());
        entity.setFechaNacimiento(m.getFechaNacimiento());
        return entity;
    }

    public static Paciente toModel(PacienteEntity e) {
        if (e == null) {
            return null;
        }
        return new Paciente(
                e.getId(),
                e.getNombre(),
                e.getTelefono(),
                e.getFechaNacimiento()
        );
    }
}
