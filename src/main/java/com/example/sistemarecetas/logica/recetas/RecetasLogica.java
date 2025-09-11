package com.example.sistemarecetas.logica.recetas;

import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.datos.recetas.RecetaConector;
import com.example.sistemarecetas.datos.recetas.RecetaDatos;
import com.example.sistemarecetas.datos.recetas.RecetaEntity;

import java.util.*;
import java.util.stream.Collectors;

public class RecetasLogica {

    private final RecetaDatos store;

    /** rutaArchivoEj: "datos/recetas.xml" */
    public RecetasLogica(String rutaArchivo) {
        this.store = new RecetaDatos(rutaArchivo);
    }

    // --------- Lectura ---------

    public List<Receta> findAll() {
        RecetaConector data = store.load();
        return data.getRecetas().stream()
                .map(RecetaMapper::toModel)
                .collect(Collectors.toList());
    }

    public Optional<Receta> findByPacienteId(String idPaciente) {
        RecetaConector data = store.load();
        return data.getRecetas().stream()
                .filter(x -> x.getPaciente().getId().equalsIgnoreCase(idPaciente))
                .findFirst()
                .map(RecetaMapper::toModel);
    }

    public List<Receta> searchByNombrePaciente(String nombre) {
        String criterio = (nombre == null) ? "" : nombre.trim().toLowerCase();
        RecetaConector data = store.load();

        return data.getRecetas().stream()
                .filter(x -> x.getPaciente().getNombre().toLowerCase().contains(criterio))
                .map(RecetaMapper::toModel)
                .collect(Collectors.toList());
    }

    // --------- Escritura ---------

    public Receta create(Receta nueva) {
        validarNueva(nueva);
        RecetaConector data = store.load();

        RecetaEntity entity = RecetaMapper.toXml(nueva);
        data.getRecetas().add(entity);
        store.save(data);
        return RecetaMapper.toModel(entity);
    }

    public Receta update(Receta receta) {
        if (receta == null || receta.getPaciente() == null || receta.getPaciente().getId() == null)
            throw new IllegalArgumentException("La receta requiere un paciente con ID válido.");

        RecetaConector data = store.load();

        for (int i = 0; i < data.getRecetas().size(); i++) {
            RecetaEntity actual = data.getRecetas().get(i);
            if (actual.getPaciente().getId().equalsIgnoreCase(receta.getPaciente().getId())) {
                data.getRecetas().set(i, RecetaMapper.toXml(receta));
                store.save(data);
                return receta;
            }
        }
        throw new NoSuchElementException("No existe receta para el paciente con ID: " + receta.getPaciente().getId());
    }

    public boolean deleteByPacienteId(String idPaciente) {
        if (idPaciente == null || idPaciente.isBlank()) return false;
        RecetaConector data = store.load();
        boolean removed = data.getRecetas().removeIf(x -> idPaciente.equalsIgnoreCase(x.getPaciente().getId()));
        if (removed) store.save(data);
        return removed;
    }

    // --------- Helpers ---------

    private void validarNueva(Receta r) {
        if (r == null) throw new IllegalArgumentException("Receta nula.");
        if (r.getMedicamentos() == null || r.getMedicamentos().isEmpty())
            throw new IllegalArgumentException("La receta debe tener al menos una prescripción.");
        validarCamposReceta(r);
        validarPrescripciones(r.getMedicamentos());
    }

    private void validarCamposReceta(Receta r) {
        if (r.getFechaConfeccion() == null)
            throw new IllegalArgumentException("La fecha de confección es obligatoria.");
        if (r.getFechaRetiro() == null)
            throw new IllegalArgumentException("La fecha de retiro es obligatoria.");
        if (r.getFechaRetiro().isBefore(r.getFechaConfeccion()))
            throw new IllegalArgumentException("La fecha de retiro no puede ser anterior a la de confección.");
        if (r.getEstado() == null || r.getEstado().isBlank())
            throw new IllegalArgumentException("El estado de la receta es obligatorio.");
        if (r.getConfeccionado() < 0)
            throw new IllegalArgumentException("El campo 'confeccionado' debe ser 0 o 1.");
    }

    private void validarPrescripciones(List<Prescripcion> lista) {
        for (int i = 0; i < lista.size(); i++) {
            Prescripcion p = lista.get(i);
            if (p.getMedicamento() == null)
                throw new IllegalArgumentException("Prescripción #" + (i + 1) + " no tiene medicamento.");
            if (p.getCantidad() <= 0)
                throw new IllegalArgumentException("Prescripción #" + (i + 1) + " tiene cantidad inválida.");
            if (p.getDuracionDias() <= 0)
                throw new IllegalArgumentException("Prescripción #" + (i + 1) + " tiene duración inválida.");
            if (p.getIndicaciones() == null || p.getIndicaciones().isBlank())
                throw new IllegalArgumentException("Prescripción #" + (i + 1) + " requiere indicaciones.");
        }
    }
}