package com.example.sistemarecetas.logica.medicamentos;

import com.example.sistemarecetas.datos.medicamentos.MedicamentoConector;
import com.example.sistemarecetas.datos.medicamentos.MedicamentoDatos;
import com.example.sistemarecetas.datos.medicamentos.MedicamentoEntity;
import com.example.sistemarecetas.Model.Medicamento;

import java.util.*;
import java.util.stream.Collectors;

/** CRUD para Medicamento usando un archivo XML como almacenamiento. */
public class MedicamentoLogica {

    private final MedicamentoDatos store;

    /** rutaArchivoEj: "datos/medicamentos.xml" */
    public MedicamentoLogica(String rutaArchivo) {
        this.store = new MedicamentoDatos(rutaArchivo);
    }

    // --------- Lectura ---------

    public List<Medicamento> findAll() {
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .map(MedicamentoMapper::toModel)
                .collect(Collectors.toList());
    }

    public Optional<Medicamento> findByCodigo(String codigo) {
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .filter(x -> codigo.equalsIgnoreCase(x.getCodigo()))
                .findFirst()
                .map(MedicamentoMapper::toModel);
    }

    public List<Medicamento> search(String texto) {
        String q = (texto == null) ? "" : texto.trim().toLowerCase();
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .filter(x ->
                        (x.getNombre() != null && x.getNombre().toLowerCase().contains(q)) ||
                                (x.getDescripcion() != null && x.getDescripcion().toLowerCase().contains(q)) ||
                                (x.getPresentacion() != null && x.getPresentacion().toLowerCase().contains(q)) ||
                                (x.getCodigo() != null && x.getCodigo().toLowerCase().contains(q))
                )
                .map(MedicamentoMapper::toModel)
                .collect(Collectors.toList());
    }

    // --------- Escritura ---------

    public Medicamento create(Medicamento nuevo) {
        validarNuevo(nuevo);
        MedicamentoConector data = store.load();

        // Unicidad por código
        if (nuevo.getCodigo() != null && !nuevo.getCodigo().isBlank()) {
            boolean exists = data.getMedicamentos().stream()
                    .anyMatch(x -> nuevo.getCodigo().equalsIgnoreCase(x.getCodigo()));
            if (exists) throw new IllegalArgumentException("Ya existe un medicamento con ese código.");
        }

        MedicamentoEntity x = MedicamentoMapper.toXml(nuevo);
        data.getMedicamentos().add(x);
        store.save(data);
        return MedicamentoMapper.toModel(x);
    }

    public Medicamento update(Medicamento medicamento) {
        if (medicamento == null || medicamento.getCodigo() == null || medicamento.getCodigo().isBlank())
            throw new IllegalArgumentException("El medicamento a actualizar requiere un código válido.");
        validarCampos(medicamento);

        MedicamentoConector data = store.load();

        for (int i = 0; i < data.getMedicamentos().size(); i++) {
            MedicamentoEntity actual = data.getMedicamentos().get(i);
            if (actual.getCodigo().equalsIgnoreCase(medicamento.getCodigo())) {
                data.getMedicamentos().set(i, MedicamentoMapper.toXml(medicamento));
                store.save(data);
                return medicamento;
            }
        }
        throw new NoSuchElementException("No existe medicamento con código: " + medicamento.getCodigo());
    }

    public boolean deleteByCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return false;
        MedicamentoConector data = store.load();
        boolean removed = data.getMedicamentos().removeIf(x -> codigo.equalsIgnoreCase(x.getCodigo()));
        if (removed) store.save(data);
        return removed;
    }

    // --------- Helpers ---------

    private void validarNuevo(Medicamento m) {
        if (m == null) throw new IllegalArgumentException("Medicamento nulo.");
        validarCampos(m);
    }

    private void validarCampos(Medicamento m) {
        if (m.getCodigo() == null || m.getCodigo().isBlank())
            throw new IllegalArgumentException("El código es obligatorio.");
        if (m.getNombre() == null || m.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (m.getPresentacion() == null || m.getPresentacion().isBlank())
            throw new IllegalArgumentException("La presentación es obligatoria.");
    }
}