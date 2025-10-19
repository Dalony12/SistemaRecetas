package com.example.sistemarecetas.datos;

import com.example.sistemarecetas.Model.Paciente;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class PacienteDatos {

    private void validarPaciente(Paciente p) throws IllegalArgumentException {
        if (p.getNombre() == null || p.getNombre().trim().isEmpty())
            throw new IllegalArgumentException("El nombre del paciente es obligatorio.");
        if (p.getIdentificacion() == null || p.getIdentificacion().trim().isEmpty())
            throw new IllegalArgumentException("La identificación es obligatoria.");
        if (p.getFechaNacimiento() == null)
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");

        int edad = Period.between(p.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < 0) throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
    }

    public List<Paciente> findAll() throws SQLException {
        String sql = "SELECT * FROM pacientes ORDER BY id_paciente";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Paciente> list = new ArrayList<>();
            while (rs.next()) {
                Paciente p = new Paciente(
                        rs.getInt("id_paciente"),
                        rs.getString("identificacion"),
                        rs.getString("nombre"),
                        rs.getInt("telefono"),
                        rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null
                );
                list.add(p);
            }
            return list;
        }
    }

    public Paciente findById(int id) throws SQLException {
        String sql = "SELECT * FROM pacientes WHERE id_paciente = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Paciente(
                            rs.getInt("id_paciente"),
                            rs.getString("identificacion"),
                            rs.getString("nombre"),
                            rs.getInt("telefono"),
                            rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null
                    );
                }
            }
            return null;
        }
    }

    public Paciente insert(Paciente p) throws SQLException {
        validarPaciente(p);

        String sql = "INSERT INTO pacientes (identificacion, nombre, telefono, fecha_nacimiento) VALUES (?, ?, ?, ?)";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getIdentificacion());
            ps.setString(2, p.getNombre());
            ps.setInt(3, p.getTelefono());
            ps.setDate(4, Date.valueOf(p.getFechaNacimiento()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) return null;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setId(keys.getInt(1));
                    return p;
                }
            }
            return null;
        }
    }

    public Paciente update(Paciente p) throws SQLException {
        validarPaciente(p);

        String sql = "UPDATE pacientes SET identificacion = ?, nombre = ?, telefono = ?, fecha_nacimiento = ? WHERE id_paciente = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, p.getIdentificacion());
            ps.setString(2, p.getNombre());
            ps.setInt(3, p.getTelefono());
            ps.setDate(4, Date.valueOf(p.getFechaNacimiento()));
            ps.setInt(5, p.getId());

            return ps.executeUpdate() > 0 ? p : null;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pacientes WHERE id_paciente = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteByIdentificacion(String identificacion) throws SQLException {
        String sql = "DELETE FROM pacientes WHERE identificacion = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, identificacion);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Paciente> search(String identificacion, String nombre) throws SQLException {
        String sql = "SELECT * FROM pacientes WHERE identificacion LIKE ? OR nombre LIKE ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + (identificacion != null ? identificacion : "") + "%");
            ps.setString(2, "%" + (nombre != null ? nombre : "") + "%");

            try (ResultSet rs = ps.executeQuery()) {
                List<Paciente> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Paciente(
                            rs.getInt("id_paciente"),
                            rs.getString("identificacion"),
                            rs.getString("nombre"),
                            rs.getInt("telefono"),
                            rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null
                    ));
                }
                return list;
            }
        }
    }

    public Paciente findByIdentificacion(String identificacion) throws SQLException {
        String sql = "SELECT * FROM pacientes WHERE identificacion = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Paciente(
                            rs.getInt("id_paciente"),
                            rs.getString("identificacion"),
                            rs.getString("nombre"),
                            rs.getInt("telefono"),
                            rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null
                    );
                }
            }
        }
        return null;
    }
}