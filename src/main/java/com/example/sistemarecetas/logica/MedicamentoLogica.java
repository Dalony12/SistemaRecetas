package com.example.sistemarecetas.logica;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.datos.MedicamentoDatos;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MedicamentoLogica {

    private final MedicamentoDatos store = new MedicamentoDatos();

    public List<Medicamento> findAll() throws SQLException {
        return store.findAll();
    }

    public Medicamento create(Medicamento nuevo) throws SQLException {
        if (nuevo == null) throw new IllegalArgumentException("Medicamento nulo.");
        return store.insert(nuevo);
    }

    public Medicamento update(Medicamento m) throws SQLException {
        if (m == null || m.getId() <= 0)
            throw new IllegalArgumentException("El medicamento a actualizar requiere un ID válido.");
        return store.update(m);
    }

    public boolean deleteByCodigo(String codigo) throws SQLException {
        return store.deleteByCodigo(codigo);
    }

    public Medicamento findByCodigo(String codigo) throws SQLException {
        return store.findByCodigo(codigo);
    }

    public List<Medicamento> search(String codigo, String nombre) throws SQLException {
        return store.search(codigo, nombre);
    }

    public List<Medicamento> searchByCodigo(String codigo) throws SQLException {
        List<Medicamento> todos = store.search(codigo, "");
        return todos.stream()
                .filter(m -> m.getCodigo().toLowerCase().contains(codigo.toLowerCase()))
                .toList();
    }

    public List<Medicamento> searchByNombre(String nombre) throws SQLException {
        List<Medicamento> todos = store.search("", nombre);
        return todos.stream()
                .filter(m -> m.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

}