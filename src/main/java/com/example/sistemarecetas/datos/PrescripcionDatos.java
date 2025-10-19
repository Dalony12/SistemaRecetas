package com.example.sistemarecetas.datos;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.Model.Prescripcion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescripcionDatos {

    // Convierte un ResultSet en un objeto Prescripcion (incluye cargar Medicamento)
    private Prescripcion mapRowToPrescripcion(ResultSet rs) throws SQLException {
        // Medicamento embedded
        Medicamento med = new Medicamento();
        med.setId(rs.getInt("id_medicamento"));
        med.setCodigo(rs.getString("codigo"));
        med.setNombre(rs.getString("nombre"));
        med.setPresentacion(rs.getString("presentacion"));
        med.setDescripcion(rs.getString("descripcion"));

        Prescripcion p = new Prescripcion(
                med,
                rs.getInt("cantidad"),
                rs.getString("indicaciones"),
                rs.getInt("duracion_dias")
        );
        p.setId(rs.getInt("id_prescripcion"));
        return p;
    }

    // --- FIND ALL (todas las prescripciones)
    public List<Prescripcion> findAll() throws SQLException {
        String sql = "SELECT p.id_prescripcion, p.id_receta, p.id_medicamento, p.cantidad, p.indicaciones, p.duracion_dias, " +
                "m.codigo, m.nombre, m.presentacion, m.descripcion " +
                "FROM prescripciones p " +
                "LEFT JOIN medicamentos m ON p.id_medicamento = m.id_medicamento " +
                "ORDER BY p.id_prescripcion";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Prescripcion> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRowToPrescripcion(rs));
            }
            return list;
        }
    }

    // --- FIND BY ID
    public Prescripcion findById(int id) throws SQLException {
        String sql = "SELECT p.id_prescripcion, p.id_receta, p.id_medicamento, p.cantidad, p.indicaciones, p.duracion_dias, " +
                "m.codigo, m.nombre, m.presentacion, m.descripcion " +
                "FROM prescripciones p " +
                "LEFT JOIN medicamentos m ON p.id_medicamento = m.id_medicamento " +
                "WHERE p.id_prescripcion = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPrescripcion(rs);
                }
            }
            return null;
        }
    }

    // --- FIND BY RECETA ID (todas las prescripciones de una receta)
    public List<Prescripcion> findByRecetaId(int idReceta) throws SQLException {
        String sql = "SELECT p.id_prescripcion, p.id_receta, p.id_medicamento, p.cantidad, p.indicaciones, p.duracion_dias, " +
                "m.codigo, m.nombre, m.presentacion, m.descripcion " +
                "FROM prescripciones p " +
                "LEFT JOIN medicamentos m ON p.id_medicamento = m.id_medicamento " +
                "WHERE p.id_receta = ? ORDER BY p.id_prescripcion";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idReceta);
            try (ResultSet rs = ps.executeQuery()) {
                List<Prescripcion> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapRowToPrescripcion(rs));
                }
                return list;
            }
        }
    }

    // --- INSERT (abre su propia conexión)
    public Prescripcion insert(Prescripcion p, int idReceta) throws SQLException {
        try (Connection cn = DB.getConnection()) {
            return insert(p, idReceta, cn);
        }
    }

    // --- INSERT usando conexión externa (útil para transacciones)
    public Prescripcion insert(Prescripcion p, int idReceta, Connection cn) throws SQLException {
        String sql = "INSERT INTO prescripciones (id_receta, id_medicamento, cantidad, indicaciones, duracion_dias) VALUES (?, ?, ?, ?, ?)";
        boolean autoCommitPrev = cn.getAutoCommit();
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idReceta);
            ps.setInt(2, p.getMedicamento().getId());
            ps.setInt(3, p.getCantidad());
            ps.setString(4, p.getIndicaciones());
            ps.setInt(5, p.getDuracionDias());

            int affected = ps.executeUpdate();
            if (affected == 0) return null;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setId(keys.getInt(1));
                    return p;
                }
            }
            return null;
        } finally {
            // no cambiamos autoCommit aquí (lo maneja quien abrió la connection)
            cn.setAutoCommit(autoCommitPrev);
        }
    }

    // En PrescripcionDatos.java
    public Prescripcion update(Prescripcion p, int idReceta) throws SQLException {
        String sql = "UPDATE prescripciones SET id_medicamento = ?, cantidad = ?, indicaciones = ?, duracion_dias = ?, id_receta = ? WHERE id_prescripcion = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, p.getMedicamento().getId());
            ps.setInt(2, p.getCantidad());
            ps.setString(3, p.getIndicaciones());
            ps.setInt(4, p.getDuracionDias());
            ps.setInt(5, idReceta);
            ps.setInt(6, p.getId());

            if (ps.executeUpdate() > 0) {
                return p;
            } else {
                return null;
            }
        }
    }

    // --- UPDATE (también puede recibir Connection si se requiere transacción)
    public Prescripcion update(Prescripcion p) throws SQLException {
        String sql = "UPDATE prescripciones SET id_medicamento = ?, cantidad = ?, indicaciones = ?, duracion_dias = ? WHERE id_prescripcion = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, p.getMedicamento().getId());
            ps.setInt(2, p.getCantidad());
            ps.setString(3, p.getIndicaciones());
            ps.setInt(4, p.getDuracionDias());
            ps.setInt(5, p.getId());

            return ps.executeUpdate() > 0 ? p : null;
        }
    }

    // --- DELETE by id
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM prescripciones WHERE id_prescripcion = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // En PrescripcionDatos.java
    public boolean deleteByRecetaId(int idReceta) throws SQLException {
        String sql = "DELETE FROM prescripciones WHERE id_receta = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idReceta);
            return ps.executeUpdate() > 0;
        }
    }

    // --- DELETE by receta id (útil al actualizar una receta: borrar y reinsertar)
    public int deleteByRecetaId(int idReceta, Connection cn) throws SQLException {
        String sql = "DELETE FROM prescripciones WHERE id_receta = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idReceta);
            return ps.executeUpdate(); // retorna cantidad de filas borradas
        }
    }

    // --- count por receta (opcional, útil para comprobaciones)
    public int countByRecetaId(int idReceta) throws SQLException {
        String sql = "SELECT COUNT(*) FROM prescripciones WHERE id_receta = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idReceta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        }
    }
}