package com.example.sistemarecetas.logica.recetas;

import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.datos.recetas.RecetaEntity;
import com.example.sistemarecetas.logica.pacientes.PacientesMapper;

public class RecetaMapper {

    public static RecetaEntity toXml(Receta r) {
        if (r == null) return null;

        return new RecetaEntity(
                r.getCodigo(),
                PacientesMapper.toXml(r.getPaciente()),
                r.getMedicamentos(),
                r.getFechaConfeccion(),
                r.getFechaRetiro(),
                r.getConfeccionado(),
                r.getEstado()
        );
    }

    public static Receta toModel(RecetaEntity e) {
        if (e == null) return null;

        return new Receta(
                e.getCodigo(),
                PacientesMapper.toModel(e.getPaciente()),
                e.getMedicamentos(),
                e.getFechaConfeccion(),
                e.getFechaRetiro(),
                e.getConfeccionado(),
                e.getEstado()
        );
    }
}

