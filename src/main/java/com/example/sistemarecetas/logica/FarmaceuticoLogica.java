package com.example.sistemarecetas.logica;

import com.example.sistemarecetas.Model.Farmaceutico;
import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.datos.FarmaceuticoDatos;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FarmaceuticoLogica {

    private final FarmaceuticoDatos store = new FarmaceuticoDatos();

    public List<Farmaceutico> findAll() throws SQLException {
        return store.findAll();
    }

    public Farmaceutico findById(String id) throws SQLException {
        return store.findByIdentificacion(id);
    }

    public Farmaceutico create(Farmaceutico nuevo) throws SQLException {
        validarNuevo(nuevo);
        return store.insert(nuevo);
    }

    public Farmaceutico update(Farmaceutico f) throws SQLException {
        if (f == null || f.getId() <= 0)
            throw new IllegalArgumentException("El farmacéutico a actualizar requiere un ID válido.");
        validarCampos(f);
        return store.update(f);
    }


    private void validarNuevo(Farmaceutico f) {
        if (f == null) throw new IllegalArgumentException("Farmacéutico nulo.");
        validarCampos(f);
    }

    private void validarCampos(Farmaceutico f) {
        if (f.getNombre() == null || f.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (f.getIdentificacion() == null || f.getIdentificacion().isBlank())
            throw new IllegalArgumentException("La identificación es obligatoria.");
        if (f.getPassword() == null || f.getPassword().isBlank())
            throw new IllegalArgumentException("La contraseña es obligatoria.");
    }

    /** Elimina un farmacéutico según su identificación única */
    public boolean deleteByIdentificacion(String identificacion) throws SQLException {
        if (identificacion == null || identificacion.isBlank()) return false;
        return store.deleteByIdentificacion(identificacion); // store es FarmaceuticoDatos
    }

    public Optional<Farmaceutico> findByIdentificacion(String identificacion) throws SQLException {
        if (identificacion == null || identificacion.isBlank()) return Optional.empty();
        return Optional.ofNullable(store.findByIdentificacion(identificacion));
    }

    public List<Farmaceutico> search(String codigo, String nombre) throws SQLException {
        return store.search(codigo, nombre);
    }

    public List<Farmaceutico> searchByCodigo(String codigo) throws SQLException {
        List<Farmaceutico> todos = store.search(codigo, "");
        return todos.stream()
                .filter(m -> m.getIdentificacion().toLowerCase().contains(codigo.toLowerCase()))
                .toList();
    }


}
