package com.example.sistemarecetas.Logica.Farmaceuticos;


import com.example.sistemarecetas.datos.farmaceutas.FarmaceutaDatos;
import com.example.sistemarecetas.datos.farmaceutas.FarmaceutaConector;
import com.example.sistemarecetas.datos.farmaceutas.FarmaceutaDatos;

import com.example.sistemarecetas.Model.Farmaceutico;
import com.example.sistemarecetas.datos.farmaceutas.FarmaceutaEntity;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FarmaceuticoLogica {
    // Referenciar a la fuente de datos
    private final FarmaceutaDatos store;

    public FarmaceuticoLogica(String rutaArchivo) {
        this.store = new FarmaceutaDatos(Path.of(rutaArchivo));
    }

    // Funciones de lectura
    public List<Farmaceutico> findAll() {
        FarmaceutaConector data = store.load(); //Aqui obtenemos el xml
        return data.getFarmaceuticos().stream().map(FarmaceuticoMapper :: toModel) // Aqui cpopiamos el xml al modelo
                .collect(Collectors.toList());  // y lo convertimos en una lista
    }

    public List<Farmaceutico> findAllByParameters(String text) {
        FarmaceutaConector data = store.load();
        return data.getFarmaceuticos().stream().
                filter(c -> c.getNombre() == text ||
                        c.getId() == text ||
                        c.getPassword() == text).
                map(FarmaceuticoMapper :: toModel).collect(Collectors.toList());
    }

    // Funciones de escritura
    public Farmaceutico create(Farmaceutico nuevo) {
        // Todo este codigo deberia estar dentro de un try and catch
        FarmaceutaConector data = store.load();

        // Podriamos validad que el cliente no se repita, la longitud de la identificacion, la edad, etc...
        // Todas las validaciones deben de hacerse en la capa de logica

        FarmaceutaEntity farmaceutaDatos = FarmaceuticoMapper.toXML(nuevo);
        data.getFarmaceuticos().add(farmaceutaDatos);
        store.save(data);
        return FarmaceuticoMapper.toModel(farmaceutaDatos);
    }

    public Farmaceutico actualizar(Farmaceutico actualizado) {
        // Todo este codigo deberia estar dentro de un try and catch
        FarmaceutaConector data = store.load();

        for (int i = 0; i < data.getFarmaceuticos().size(); i++) {
            FarmaceutaEntity actual = data.getFarmaceuticos().get(i);
            if (actual.getId() == actualizado.getId()) {
                // Aqui encontramos el cliente a modificar
                // y aplicamos los cambios

                data.getFarmaceuticos().set(i, FarmaceuticoMapper.toXML(actualizado));
                store.save(data);
                break;
            }
        }
        return actualizado;
    }

    public boolean deleteById(String id) {
        // Todo este codigo deberia estar dentro de un try and catch

        // que el id no sea 0 o un numero negativo
        FarmaceutaConector data = store.load();
        boolean eliminado = data.getFarmaceuticos().
                removeIf(farma -> farma.getId().equals(id));
        if (eliminado) {
            store.save(data);
        }
        return eliminado;
    }
}
