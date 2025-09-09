package com.example.sistemarecetas.Logica.Farmaceuticos;


import com.example.sistemarecetas.Model.Farmaceutico;
import com.example.sistemarecetas.datos.farmaceutas.FarmaceutaEntity;

public class FarmaceuticoMapper {

    public static FarmaceutaEntity toXML(Farmaceutico c) {
        if (c == null) {
            return null;
        }
        FarmaceutaEntity farma = new FarmaceutaEntity();
        farma.setId(c.getId());
        farma.setNombre(c.getNombre());
        farma.setPassword(c.getPassword());
        return farma;
    }

    public static Farmaceutico toModel(FarmaceutaEntity c) {
        if (c == null) {
            return null;
        }
        Farmaceutico farma = new Farmaceutico(c.getId(), c.getNombre(),
                c.getPassword());
        return farma;
    }
}

