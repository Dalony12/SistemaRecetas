package com.example.sistemarecetas.datos;

import com.example.sistemarecetas.Model.Medico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoDatos {

    public List<Medico> findAll() throws SQLException {
        String sql = "SELECT * FROM medicos ORDER BY id_medico";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Medico> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Medico(
                        rs.getInt("id_medico"),
                        rs.getString("identificacion"),
                        rs.getString("nombre"),
                        rs.getString("especialidad")
                ));
            }
            return list;
        }
    }

    public Medico insert(Medico m) throws SQLException {
        String sql = "INSERT INTO medicos (identificacion, nombre, especialidad) VALUES (?, ?, ?)";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getIdentificacion());
            ps.setString(2, m.getNombre());
            ps.setString(3, m.getEspecialidad());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) return null;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    m.setId(keys.getInt(1));
                    return m;
                }
            }
            return null;
        }
    }

    public Medico update(Medico m) throws SQLException {
        String sql = "UPDATE medicos SET nombre = ?, especialidad = ? WHERE identificacion = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, m.getNombre());
            ps.setString(2, m.getEspecialidad());
            ps.setString(3, m.getIdentificacion());

            return ps.executeUpdate() > 0 ? m : null;
        }
    }

    public boolean deleteByIdentificacion(String identificacion) throws SQLException {
        String sql = "DELETE FROM medicos WHERE identificacion = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, identificacion);
            return ps.executeUpdate() > 0;
        }
    }

    public Medico findByIdentificacion(String identificacion) throws SQLException {
        String sql = "SELECT * FROM medicos WHERE identificacion = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Medico(
                            rs.getInt("id_medico"),
                            rs.getString("identificacion"),
                            rs.getString("nombre"),
                            rs.getString("especialidad")
                    );
                }
            }
        }
        return null;
    }

    public List<Medico> search(String identificacion, String nombre) throws SQLException {
        String sql = "SELECT * FROM medicos WHERE identificacion LIKE ? OR nombre LIKE ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + (identificacion != null ? identificacion : "") + "%");
            ps.setString(2, "%" + (nombre != null ? nombre : "") + "%");

            try (ResultSet rs = ps.executeQuery()) {
                List<Medico> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Medico(
                            rs.getInt("id_medico"),
                            rs.getString("identificacion"),
                            rs.getString("nombre"),
                            rs.getString("especialidad")
                    ));
                }
                return list;
            }
        }
    }
}