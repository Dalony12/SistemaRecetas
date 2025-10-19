package com.example.sistemarecetas.datos;

import com.example.sistemarecetas.Model.Farmaceutico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FarmaceuticoDatos {

    private void validarFarmaceutico(Farmaceutico f) throws IllegalArgumentException {
        if (f.getNombre() == null || f.getNombre().trim().isEmpty())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (f.getIdentificacion() == null || f.getIdentificacion().trim().isEmpty())
            throw new IllegalArgumentException("La identificación es obligatoria.");
        if (f.getPassword() == null || f.getPassword().trim().isEmpty())
            throw new IllegalArgumentException("La contraseña es obligatoria.");
    }

    public List<Farmaceutico> findAll() throws SQLException {
        String sql = "SELECT * FROM farmaceuticos ORDER BY id_farmaceutico";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Farmaceutico> list = new ArrayList<>();
            while (rs.next()) {
                Farmaceutico f = new Farmaceutico(
                        rs.getInt("id_farmaceutico"),
                        rs.getString("identificacion"),
                        rs.getString("nombre"),
                        rs.getString("password")
                );
                list.add(f);
            }
            return list;
        }
    }

    public Farmaceutico findById(int id) throws SQLException {
        String sql = "SELECT * FROM farmaceuticos WHERE id_farmaceutico = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Farmaceutico(
                            rs.getInt("id_farmaceutico"),
                            rs.getString("identificacion"),
                            rs.getString("nombre"),
                            rs.getString("password")
                    );
                }
            }
            return null;
        }
    }

    public Farmaceutico insert(Farmaceutico f) throws SQLException {
        validarFarmaceutico(f);

        String sql = "INSERT INTO farmaceuticos (identificacion, nombre, password) VALUES (?, ?, ?)";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, f.getIdentificacion());
            ps.setString(2, f.getNombre());
            ps.setString(3, f.getPassword());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) return null;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    f.setId(keys.getInt(1));
                    return f;
                }
            }
            return null;
        }
    }

    public Farmaceutico update(Farmaceutico f) throws SQLException {
        validarFarmaceutico(f);

        String sql = "UPDATE farmaceuticos SET identificacion = ?, nombre = ?, password = ? WHERE id_farmaceutico = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, f.getIdentificacion());
            ps.setString(2, f.getNombre());
            ps.setString(3, f.getPassword());
            ps.setInt(4, f.getId());

            return ps.executeUpdate() > 0 ? f : null;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM farmaceuticos WHERE id_farmaceutico = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Farmaceutico findByIdentificacion(String identificacion) throws SQLException {
        String sql = "SELECT * FROM farmaceuticos WHERE identificacion = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Farmaceutico(
                            rs.getInt("id_farmaceutico"),
                            rs.getString("identificacion"),
                            rs.getString("nombre"),
                            rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    public boolean deleteByIdentificacion(String identificacion) throws SQLException {
        String sql = "DELETE FROM farmaceuticos WHERE identificacion = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement pst = cn.prepareStatement(sql)) {

            pst.setString(1, identificacion);

            int affectedRows = pst.executeUpdate();
            return affectedRows > 0; // true si se eliminó al menos un registro
        }
    }
}