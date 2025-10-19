package com.example.sistemarecetas.datos;

import com.example.sistemarecetas.Model.Medicamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDatos {

    private void validarMedicamento(Medicamento m) {
        if (m.getNombre() == null || m.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del medicamento es obligatorio.");
        if (m.getCodigo() == null || m.getCodigo().isBlank())
            throw new IllegalArgumentException("El código del medicamento es obligatorio.");
        if (m.getPresentacion() == null) m.setPresentacion("");
        if (m.getDescripcion() == null) m.setDescripcion("");
    }

    public List<Medicamento> findAll() throws SQLException {
        List<Medicamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM medicamentos ORDER BY id_medicamento";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Medicamento(
                        rs.getInt("id_medicamento"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("presentacion"),
                        rs.getString("descripcion")
                ));
            }
        }
        return lista;
    }

    public Medicamento insert(Medicamento m) throws SQLException {
        validarMedicamento(m);
        String sql = "INSERT INTO medicamentos (codigo, nombre, presentacion, descripcion) VALUES (?, ?, ?, ?)";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getCodigo());
            ps.setString(2, m.getNombre());
            ps.setString(3, m.getPresentacion());
            ps.setString(4, m.getDescripcion());
            int rows = ps.executeUpdate();
            if (rows == 0) return null;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) m.setId(keys.getInt(1));
            }
            return m;
        }
    }

    public Medicamento update(Medicamento m) throws SQLException {
        validarMedicamento(m);
        String sql = "UPDATE medicamentos SET codigo = ?, nombre = ?, presentacion = ?, descripcion = ? WHERE id_medicamento = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, m.getCodigo());
            ps.setString(2, m.getNombre());
            ps.setString(3, m.getPresentacion());
            ps.setString(4, m.getDescripcion());
            ps.setInt(5, m.getId());

            return ps.executeUpdate() > 0 ? m : null;
        }
    }

    public boolean deleteByCodigo(String codigo) throws SQLException {
        String sql = "DELETE FROM medicamentos WHERE codigo = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            return ps.executeUpdate() > 0;
        }
    }

    public Medicamento findByCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM medicamentos WHERE codigo = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Medicamento(
                            rs.getInt("id_medicamento"),
                            rs.getString("codigo"),
                            rs.getString("nombre"),
                            rs.getString("presentacion"),
                            rs.getString("descripcion")
                    );
                }
            }
        }
        return null;
    }

    public List<Medicamento> search(String codigo, String nombre) throws SQLException {
        List<Medicamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM medicamentos WHERE codigo LIKE ? OR nombre LIKE ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + codigo + "%");
            ps.setString(2, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Medicamento(
                            rs.getInt("id_medicamento"),
                            rs.getString("codigo"),
                            rs.getString("nombre"),
                            rs.getString("presentacion"),
                            rs.getString("descripcion")
                    ));
                }
            }
        }
        return lista;
    }
}