package com.example.sistemarecetas.logica.medicos;

import com.example.sistemarecetas.Model.Medico;
import com.example.sistemarecetas.datos.medicos.MedicoEntity;


public class MedicosMapper {

    public static MedicoEntity toXml(Medico m) {
        if (m == null) {
            return null;
        }
        MedicoEntity entity = new MedicoEntity();
        entity.setId(m.getId());
        entity.setNombre(m.getNombre());
        entity.setPassword(m.getPassword());
        entity.setEspecialidad(m.getEspecialidad());
        return entity;
    }

    public static Medico toModel(MedicoEntity e) {
        if (e == null) {
            return null;
        }
        return new Medico(
                e.getId(),
                e.getNombre(),
                e.getPassword(),
                e.getEspecialidad()
        );
    }
}