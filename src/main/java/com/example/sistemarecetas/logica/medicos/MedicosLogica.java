package com.example.sistemarecetas.logica.medicos;


import com.example.sistemarecetas.Model.Medico;
import com.example.sistemarecetas.datos.medicos.MedicoConector;
import com.example.sistemarecetas.datos.medicos.MedicoDatos;
import com.example.sistemarecetas.datos.medicos.MedicoEntity;
import com.example.sistemarecetas.logica.medicos.MedicosMapper;

import java.util.*;
import java.util.stream.Collectors;

/** CRUD para medicos usando un archivo XML como almacenamiento. */
public class MedicosLogica {

    private final MedicoDatos store;

    /** rutaArchivoEj: "datos/farmaceutas.xml" */
    public MedicosLogica(String rutaArchivo) {
        this.store = new MedicoDatos(rutaArchivo);
    }

    // --------- Lectura ---------

    public List<Medico> findAll() {
        MedicoConector data = store.load();
        return data.getMedicos().stream()
                .map(MedicosMapper::toModel)
                .collect(Collectors.toList());
    }

    public Optional<Medico> findByCodigo(String codigo) {
        MedicoConector data = store.load();
        return data.getMedicos().stream()
                .filter(x -> codigo.equalsIgnoreCase(x.getId()))
                .findFirst()
                .map(MedicosMapper::toModel);
    }

    public List<Medico> search(String id, String nombre) {
        String qId = (id == null) ? "" : id.trim().toLowerCase();
        String qNombre = (nombre == null) ? "" : nombre.trim().toLowerCase();

        MedicoConector data = store.load();

        return data.getMedicos().stream()
                // filtra solo si el ID no está vacío
                .filter(x -> qId.isEmpty() ||
                        (x.getId() != null && x.getId().toLowerCase().contains(qId)))
                // filtra solo si el nombre no está vacío
                .filter(x -> qNombre.isEmpty() ||
                        (x.getNombre() != null && x.getNombre().toLowerCase().contains(qNombre)))
                .map(MedicosMapper::toModel)
                .toList();
    }

    // --------- Escritura ---------

    public Medico create(Medico nuevo) {
        validarNuevo(nuevo);
        MedicoConector data = store.load();

        // Unicidad por código
        if (nuevo.getId() != null && !nuevo.getId().isBlank()) {
            boolean exists = data.getMedicos().stream()
                    .anyMatch(x -> nuevo.getId().equalsIgnoreCase(x.getId()));
            if (exists) throw new IllegalArgumentException("Ya existe un Medico con ese ID.");
        }

        MedicoEntity x = MedicosMapper.toXml(nuevo);
        data.getMedicos().add(x);
        store.save(data);
        return MedicosMapper.toModel(x);
    }

    public Medico update(Medico farmaceutico) {
        if (farmaceutico == null || farmaceutico.getId() == null || farmaceutico.getId().isBlank())
            throw new IllegalArgumentException("El Medico  a actualizar requiere un ID válido.");
        validarCampos(farmaceutico);

        MedicoConector data = store.load();

        for (int i = 0; i < data.getMedicos().size(); i++) {
            MedicoEntity actual = data.getMedicos().get(i);
            if (actual.getId().equalsIgnoreCase(farmaceutico.getId())) {
                data.getMedicos().set(i, MedicosMapper.toXml(farmaceutico));
                store.save(data);
                return farmaceutico;
            }
        }
        throw new NoSuchElementException("No existe Medico con ID: " + farmaceutico.getId());
    }

    public boolean deleteByCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return false;
        MedicoConector data = store.load();
        boolean removed = data.getMedicos().removeIf(x -> codigo.equalsIgnoreCase(x.getId()));
        if (removed) store.save(data);
        return removed;
    }

    // --------- Helpers ---------

    private void validarNuevo(Medico m) {
        if (m == null) throw new IllegalArgumentException("Medico nulo.");
        validarCampos(m);
    }

    private void validarCampos(Medico m) {
        if (m.getId() == null || m.getId().isBlank())
            throw new IllegalArgumentException("El ID es obligatorio.");
        if (m.getNombre() == null || m.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (m.getPassword() == null || m.getPassword().isBlank())
            throw new IllegalArgumentException("El password es obligatoria.");
        if (m.getEspecialidad() == null || m.getEspecialidad().isBlank())
            throw new IllegalArgumentException("La especialidad es obligatoria.");
    }
}
