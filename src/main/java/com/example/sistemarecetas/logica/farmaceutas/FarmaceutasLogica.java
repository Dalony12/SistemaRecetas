package com.example.sistemarecetas.logica.farmaceutas;

import com.example.sistemarecetas.Model.Farmaceutico;
import com.example.sistemarecetas.datos.farmaceutas.FarmaceutaConector;
import com.example.sistemarecetas.datos.farmaceutas.FarmaceutaDatos;
import com.example.sistemarecetas.datos.farmaceutas.FarmaceutaEntity;

import java.util.*;
import java.util.stream.Collectors;

/** CRUD para farmaceutas usando un archivo XML como almacenamiento. */
public class FarmaceutasLogica {

    private final FarmaceutaDatos store;

    /** rutaArchivoEj: "datos/farmaceutas.xml" */
    public FarmaceutasLogica(String rutaArchivo) {
        this.store = new FarmaceutaDatos(rutaArchivo);
    }

    // --------- Lectura ---------

    public List<Farmaceutico> findAll() {
        FarmaceutaConector data = store.load();
        return data.getFarmaceuticos().stream()
                .map(FarmaceutasMapper::toModel)
                .collect(Collectors.toList());
    }

    public Optional<Farmaceutico> findByCodigo(String codigo) {
        FarmaceutaConector data = store.load();
        return data.getFarmaceuticos().stream()
                .filter(x -> codigo.equalsIgnoreCase(x.getId()))
                .findFirst()
                .map(FarmaceutasMapper::toModel);
    }

    public List<Farmaceutico> search(String id, String nombre) {
        String qId = (id == null) ? "" : id.trim().toLowerCase();
        String qNombre = (nombre == null) ? "" : nombre.trim().toLowerCase();

        FarmaceutaConector data = store.load();

        return data.getFarmaceuticos().stream()
                // filtra solo si el ID no está vacío
                .filter(x -> qId.isEmpty() ||
                        (x.getId() != null && x.getId().toLowerCase().contains(qId)))
                // filtra solo si el nombre no está vacío
                .filter(x -> qNombre.isEmpty() ||
                        (x.getNombre() != null && x.getNombre().toLowerCase().contains(qNombre)))
                .map(FarmaceutasMapper::toModel)
                .toList();
    }

    // --------- Escritura ---------

    public Farmaceutico create(Farmaceutico nuevo) {
        validarNuevo(nuevo);
        FarmaceutaConector data = store.load();

        // Unicidad por código
        if (nuevo.getId() != null && !nuevo.getId().isBlank()) {
            boolean exists = data.getFarmaceuticos().stream()
                    .anyMatch(x -> nuevo.getId().equalsIgnoreCase(x.getId()));
            if (exists) throw new IllegalArgumentException("Ya existe un farmaceutico con ese ID.");
        }

        FarmaceutaEntity x = FarmaceutasMapper.toXml(nuevo);
        data.getFarmaceuticos().add(x);
        store.save(data);
        return FarmaceutasMapper.toModel(x);
    }

    public Farmaceutico update(Farmaceutico farmaceutico) {
        if (farmaceutico == null || farmaceutico.getId() == null || farmaceutico.getId().isBlank())
            throw new IllegalArgumentException("El farmaceutico  a actualizar requiere un ID válido.");
        validarCampos(farmaceutico);

        FarmaceutaConector data = store.load();

        for (int i = 0; i < data.getFarmaceuticos().size(); i++) {
            FarmaceutaEntity actual = data.getFarmaceuticos().get(i);
            if (actual.getId().equalsIgnoreCase(farmaceutico.getId())) {
                data.getFarmaceuticos().set(i, FarmaceutasMapper.toXml(farmaceutico));
                store.save(data);
                return farmaceutico;
            }
        }
        throw new NoSuchElementException("No existe Faramaceutico con ID: " + farmaceutico.getId());
    }

    public String generarNextID() {
        List<Farmaceutico> lista = findAll();

        int max = lista.stream()
                .map(Farmaceutico::getId)
                .filter(Objects::nonNull)
                .filter(id -> id.startsWith("far-"))
                .mapToInt(id -> {
                    try {
                        return Integer.parseInt(id.replace("far-", ""));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);

        return String.format("%03d", max + 1);
    }


    public boolean deleteByCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return false;
        FarmaceutaConector data = store.load();
        boolean removed = data.getFarmaceuticos().removeIf(x -> codigo.equalsIgnoreCase(x.getId()));
        if (removed) store.save(data);
        return removed;
    }

    // --------- Helpers ---------

    private void validarNuevo(Farmaceutico m) {
        if (m == null) throw new IllegalArgumentException("Farmaceutico nulo.");
        validarCampos(m);
    }

    private void validarCampos(Farmaceutico m) {
        if (m.getId() == null || m.getId().isBlank())
            throw new IllegalArgumentException("El ID es obligatorio.");
        if (m.getNombre() == null || m.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (m.getPassword() == null || m.getPassword().isBlank())
            throw new IllegalArgumentException("El password es obligatoria.");
    }
}