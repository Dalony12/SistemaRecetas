package com.example.sistemarecetas.logica.farmaceutas;

import com.example.sistemarecetas.Model.Farmaceutico;
import com.example.sistemarecetas.datos.farmaceutas.FarmaceutaEntity;


public class FarmaceutasMapper {

    public static FarmaceutaEntity toXml(Farmaceutico m) {
        if (m == null) {
            return null;
        }
        FarmaceutaEntity entity = new FarmaceutaEntity();
        entity.setId(m.getId());
        entity.setNombre(m.getNombre());
        entity.setPassword(m.getPassword());
        return entity;
    }

    public static Farmaceutico toModel(FarmaceutaEntity e) {
        if (e == null) {
            return null;
        }
        return new Farmaceutico(
                e.getId(),
                e.getNombre(),
                e.getPassword()
        );
    }
}