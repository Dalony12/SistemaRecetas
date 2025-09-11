package com.example.sistemarecetas.logica.medicamentos;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.datos.medicamentos.MedicamentoEntity;

public class MedicamentoMapper {

    public static MedicamentoEntity toXml(Medicamento m) {
        if (m == null) {
            return null;
        }
        MedicamentoEntity entity = new MedicamentoEntity();
        entity.setCodigo(m.getCodigo());
        entity.setNombre(m.getNombre());
        entity.setDescripcion(m.getDescripcion());
        entity.setPresentacion(m.getPresentacion());
        return entity;
    }

    public static Medicamento toModel(MedicamentoEntity e) {
        if (e == null) {
            return null;
        }
        return new Medicamento(
                e.getCodigo(),
                e.getNombre(),
                e.getDescripcion(),
                e.getPresentacion()
        );
    }
}
