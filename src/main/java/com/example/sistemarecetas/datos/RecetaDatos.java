package com.example.sistemarecetas.datos;

import com.example.sistemarecetas.Model.Paciente;
import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RecetaDatos {

    private final PrescripcionDatos prescripcionDatos = new PrescripcionDatos();
    private final PacienteDatos pacienteDatos = new PacienteDatos();

    private void validarReceta(Receta r) {
        if (r.getPaciente() == null)
            throw new IllegalArgumentException("La receta debe tener un paciente asociado.");
        if (r.getMedicamentos() == null || r.getMedicamentos().isEmpty())
            throw new IllegalArgumentException("La receta debe contener al menos una prescripción.");
    }

    // --- FIND ALL (carga paciente y prescripciones)
    public List<Receta> findAll() throws SQLException {
        String sql = "SELECT id_receta, codigo, id_paciente, fecha_confeccion, fecha_retiro, confeccionado, estado " +
                "FROM recetas ORDER BY id_receta";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Receta> list = new ArrayList<>();
            while (rs.next()) {
                int idReceta = rs.getInt("id_receta");
                Paciente paciente = pacienteDatos.findById(rs.getInt("id_paciente"));
                LocalDate fechaConfeccion = rs.getDate("fecha_confeccion") != null ? rs.getDate("fecha_confeccion").toLocalDate() : null;
                LocalDate fechaRetiro = rs.getDate("fecha_retiro") != null ? rs.getDate("fecha_retiro").toLocalDate() : null;
                int confeccionado = rs.getInt("confeccionado");
                String estado = rs.getString("estado");

                List<Prescripcion> prescripciones = prescripcionDatos.findByRecetaId(idReceta);

                Receta r = new Receta(idReceta, paciente, prescripciones, fechaConfeccion, fechaRetiro, confeccionado, estado);
                list.add(r);
            }
            return list;
        }
    }

    // --- FIND BY ID
    public Receta findById(int id) throws SQLException {
        String sql = "SELECT id_receta, codigo, id_paciente, fecha_confeccion, fecha_retiro, confeccionado, estado " +
                "FROM recetas WHERE id_receta = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idReceta = rs.getInt("id_receta");
                    Paciente paciente = pacienteDatos.findById(rs.getInt("id_paciente"));
                    LocalDate fechaConfeccion = rs.getDate("fecha_confeccion") != null ? rs.getDate("fecha_confeccion").toLocalDate() : null;
                    LocalDate fechaRetiro = rs.getDate("fecha_retiro") != null ? rs.getDate("fecha_retiro").toLocalDate() : null;
                    int confeccionado = rs.getInt("confeccionado");
                    String estado = rs.getString("estado");

                    List<Prescripcion> prescripciones = prescripcionDatos.findByRecetaId(idReceta);

                    return new Receta(idReceta, paciente, prescripciones, fechaConfeccion, fechaRetiro, confeccionado, estado);
                }
            }
            return null;
        }
    }

    // --- INSERT (inserta receta y sus prescripciones en una transacción)
    public Receta insert(Receta r) throws SQLException {
        validarReceta(r);

        String sql = "INSERT INTO recetas (codigo, id_paciente, fecha_confeccion, fecha_retiro, confeccionado, estado) VALUES (?, ?, ?, ?, ?, ?)";
        Connection cn = null;
        boolean previousAutoCommit = true;
        try {
            cn = DB.getConnection();
            previousAutoCommit = cn.getAutoCommit();
            cn.setAutoCommit(false); // inicio transacción

            try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, r.getEstado() != null ? r.getEstado() : r.getEstado()); // codigo could be null or computed elsewhere
                ps.setInt(2, r.getPaciente().getId());
                ps.setDate(3, r.getFechaConfeccion() != null ? Date.valueOf(r.getFechaConfeccion()) : null);
                ps.setDate(4, r.getFechaRetiro() != null ? Date.valueOf(r.getFechaRetiro()) : null);
                ps.setInt(5, r.getConfeccionado());
                ps.setString(6, r.getEstado());

                // Nota: si 'codigo' no lo manejas en Receta, ajustar ps.setString(1, ... ) con r.getCodigo() si existe.
                // Aquí asumo que r.getEstado() ocupa índice 1 por falta de campo getCodigo en modelo.
                // Si querés manejar 'codigo' en Receta, agrega getter y ajustamos esta línea.
                // Para evitar errores, podemos usar NULL si no hay código:
                // ps.setString(1, r.getCodigo()); // si implementás getCodigo()

                int affected = ps.executeUpdate();
                if (affected == 0) {
                    cn.rollback();
                    return null;
                }

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int generatedId = keys.getInt(1);
                        r.setId(generatedId);

                        // insertar cada prescripción usando la misma conexión
                        for (Prescripcion pres : r.getMedicamentos()) {
                            Prescripcion inserted = prescripcionDatos.insert(pres, generatedId, cn);
                            if (inserted == null) {
                                // falla -> rollback
                                cn.rollback();
                                return null;
                            }
                        }

                        cn.commit();
                        return r;
                    } else {
                        cn.rollback();
                        return null;
                    }
                }
            }
        } catch (SQLException ex) {
            if (cn != null) cn.rollback();
            throw ex;
        } finally {
            if (cn != null) {
                cn.setAutoCommit(previousAutoCommit);
                cn.close();
            }
        }
    }

    // --- UPDATE (actualiza receta y reemplaza prescripciones: borra las viejas y vuelve a insertar)
    public Receta update(Receta r) throws SQLException {
        validarReceta(r);

        String sql = "UPDATE recetas SET codigo = ?, id_paciente = ?, fecha_confeccion = ?, fecha_retiro = ?, confeccionado = ?, estado = ? WHERE id_receta = ?";
        Connection cn = null;
        boolean previousAutoCommit = true;
        try {
            cn = DB.getConnection();
            previousAutoCommit = cn.getAutoCommit();
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, null); // ajustar si manejás código en Receta
                ps.setInt(2, r.getPaciente().getId());
                ps.setDate(3, r.getFechaConfeccion() != null ? Date.valueOf(r.getFechaConfeccion()) : null);
                ps.setDate(4, r.getFechaRetiro() != null ? Date.valueOf(r.getFechaRetiro()) : null);
                ps.setInt(5, r.getConfeccionado());
                ps.setString(6, r.getEstado());
                ps.setInt(7, r.getId());

                int affected = ps.executeUpdate();
                if (affected == 0) {
                    cn.rollback();
                    return null;
                }

                // Borrar prescripciones viejas
                prescripcionDatos.deleteByRecetaId(r.getId(), cn);

                // Insertar las nuevas
                for (Prescripcion pres : r.getMedicamentos()) {
                    Prescripcion inserted = prescripcionDatos.insert(pres, r.getId(), cn);
                    if (inserted == null) {
                        cn.rollback();
                        return null;
                    }
                }

                cn.commit();
                return r;
            }
        } catch (SQLException ex) {
            if (cn != null) cn.rollback();
            throw ex;
        } finally {
            if (cn != null) {
                cn.setAutoCommit(previousAutoCommit);
                cn.close();
            }
        }
    }

    // --- DELETE (el borrado en cascada debería eliminar prescripciones si definiste FK ON DELETE CASCADE)
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM recetas WHERE id_receta = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Opcional: encontrar recetas por paciente
    public List<Receta> findByPacienteId(int idPaciente) throws SQLException {
        String sql = "SELECT id_receta FROM recetas WHERE id_paciente = ?";
        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                List<Receta> list = new ArrayList<>();
                while (rs.next()) {
                    Receta r = findById(rs.getInt("id_receta"));
                    if (r != null) list.add(r);
                }
                return list;
            }
        }
    }
}