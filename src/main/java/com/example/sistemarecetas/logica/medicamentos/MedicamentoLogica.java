package com.example.sistemarecetas.logica.medicamentos;

import com.example.sistemarecetas.datos.medicamentos.MedicamentoConector;
import com.example.sistemarecetas.datos.medicamentos.MedicamentoDatos;
import com.example.sistemarecetas.datos.medicamentos.MedicamentoEntity;
import com.example.sistemarecetas.Model.Medicamento;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Lógica de negocio para Medicamentos.
 * Encapsula las operaciones CRUD y validaciones,
 * usando un archivo XML como almacenamiento persistente.
 */
public class MedicamentoLogica {

    private final MedicamentoDatos store;

    /** rutaArchivoEj: "datos/medicamentos.xml" */
    public MedicamentoLogica(String rutaArchivo) {
        this.store = new MedicamentoDatos(rutaArchivo);
    }

    // --------- Lectura ---------

    /** Retorna todos los medicamentos en memoria. */
    public List<Medicamento> findAll() {
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .map(MedicamentoMapper::toModel)
                .collect(Collectors.toList());
    }

    /** Busca un medicamento por código exacto (case-insensitive). */
    public Optional<Medicamento> findByCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return Optional.empty();
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .filter(x -> codigo.equalsIgnoreCase(x.getCodigo()))
                .findFirst()
                .map(MedicamentoMapper::toModel);
    }

    /** Busca medicamentos por descripción (contiene, case-insensitive). */
    public List<Medicamento> findByDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) return List.of();
        String q = descripcion.trim().toLowerCase();
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .filter(x -> x.getDescripcion() != null &&
                        x.getDescripcion().toLowerCase().contains(q))
                .map(MedicamentoMapper::toModel)
                .toList();
    }

    /** Búsqueda combinada por ID y nombre. */
    public List<Medicamento> search(String id, String nombre) {
        String qId = (id == null) ? "" : id.trim().toLowerCase();
        String qNombre = (nombre == null) ? "" : nombre.trim().toLowerCase();

        MedicamentoConector data = store.load();

        return data.getMedicamentos().stream()
                .filter(x -> qId.isEmpty() ||
                        (x.getCodigo() != null && x.getCodigo().toLowerCase().contains(qId)))
                .filter(x -> qNombre.isEmpty() ||
                        (x.getNombre() != null && x.getNombre().toLowerCase().contains(qNombre)))
                .map(MedicamentoMapper::toModel)
                .toList();
    }

    // --------- Escritura ---------

    /** Crea un nuevo medicamento validando unicidad y campos obligatorios. */
    public Medicamento create(Medicamento nuevo) {
        validarNuevo(nuevo);
        MedicamentoConector data = store.load();

        // Validar unicidad por código
        if (existsByCodigo(nuevo.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un medicamento con ese código.");
        }

        MedicamentoEntity x = MedicamentoMapper.toXml(nuevo);
        data.getMedicamentos().add(x);
        store.save(data);
        return MedicamentoMapper.toModel(x);
    }

    /** Actualiza un medicamento existente basado en su código. */
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

    /** Elimina un medicamento por código. */
    public boolean deleteByCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return false;
        MedicamentoConector data = store.load();
        boolean removed = data.getMedicamentos().removeIf(x -> codigo.equalsIgnoreCase(x.getCodigo()));
        if (removed) store.save(data);
        return removed;
    }

    // --------- Helpers ---------

    /** Verifica si ya existe un medicamento con ese código. */
    public boolean existsByCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return false;
        MedicamentoConector data = store.load();
        return data.getMedicamentos().stream()
                .anyMatch(x -> codigo.equalsIgnoreCase(x.getCodigo()));
    }

    /** Retorna la cantidad total de medicamentos. */
    public long count() {
        return store.load().getMedicamentos().size();
    }

    /** Indica si no hay medicamentos en el sistema. */
    public boolean isEmpty() {
        return count() == 0;
    }

    // --------- Validaciones ---------

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