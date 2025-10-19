package com.example.sistemarecetas.logica;

import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Lógica de dashboard para mostrar métricas de recetas y prescripciones.
 * Adaptada a base de datos MySQL usando RecetasLogica.
 */
public class DashboardLogica {

    private final RecetaLogica recetasLogica;

    // Constructor sin parámetros para usar DB
    public DashboardLogica() {
        this.recetasLogica = new RecetaLogica(); // RecetasLogica ya adaptada a DB
    }

    /** Carga todas las recetas desde la base de datos */
    public List<Receta> cargarRecetas() throws SQLException {
        return recetasLogica.findAll();
    }

    /** Total de recetas registradas */
    public int totalRecetas() throws SQLException {
        return cargarRecetas().size();
    }

    /** Conteo de recetas por estado (En proceso, Lista, Entregado) */
    public Map<String, Long> recetaPorEstado() throws SQLException {
        List<Receta> recetas = cargarRecetas();
        Map<String, Long> conteoPorEstado = new HashMap<>();

        // Inicializamos todos los posibles estados
        conteoPorEstado.put("En proceso", 0L);
        conteoPorEstado.put("Lista", 0L);
        conteoPorEstado.put("Entregado", 0L);

        for (Receta receta : recetas) {
            String estado = receta.getEstado();
            if (estado != null && conteoPorEstado.containsKey(estado)) {
                conteoPorEstado.put(estado, conteoPorEstado.get(estado) + 1);
            }
        }
        return conteoPorEstado;
    }

    /**
     * Conteo de prescripciones por mes para un medicamento específico dentro de un rango de fechas.
     * @param fechaInicio Fecha de inicio (inclusive)
     * @param fechaFinal Fecha final (inclusive)
     * @param medicamento Nombre del medicamento
     * @return Mapa con clave "YYYY-MM" y valor cantidad de prescripciones
     */
    public Map<String, Long> prescripcionesPorMes(LocalDate fechaInicio, LocalDate fechaFinal, String medicamento) throws SQLException {
        Map<String, Long> prescripcionesPorMes = new TreeMap<>();
        List<Receta> listaRecetas = cargarRecetas();

        for (Receta receta : listaRecetas) {
            LocalDate fecha = receta.getFechaConfeccion();
            if (fecha != null &&
                    !fecha.isBefore(fechaInicio) &&
                    !fecha.isAfter(fechaFinal)) {

                for (Prescripcion p : receta.getMedicamentos()) {
                    if (p.getMedicamento() != null && p.getMedicamento().getNombre().equalsIgnoreCase(medicamento)) {
                        YearMonth mesReceta = YearMonth.from(fecha);
                        String claveMes = mesReceta.getYear() + "-" + String.format("%02d", mesReceta.getMonthValue());

                        prescripcionesPorMes.put(claveMes, prescripcionesPorMes.getOrDefault(claveMes, 0L) + 1);
                    }
                }
            }
        }

        return prescripcionesPorMes;
    }
}
