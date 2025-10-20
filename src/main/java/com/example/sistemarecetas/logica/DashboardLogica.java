package com.example.sistemarecetas.logica;

import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Lógica de dashboard para mostrar métricas de recetas y prescripciones
 * directamente desde la base de datos.
 */
public class DashboardLogica {

    private final RecetaLogica recetaLogica;
    private final PrescripcionLogica prescripcionLogica;

    public DashboardLogica() {
        this.recetaLogica = new RecetaLogica();
        this.prescripcionLogica = new PrescripcionLogica();
    }

    /** Obtiene todas las recetas desde la base de datos */
    public List<Receta> cargarRecetas() throws SQLException {
        return recetaLogica.findAll();
    }

    /** Total de recetas registradas */
    public int totalRecetas() throws SQLException {
        return cargarRecetas().size();
    }

    /**
     * Devuelve un mapa con la cantidad de recetas por estado.
     * Clave: estado, Valor: cantidad
     */
    public Map<String, Long> recetaPorEstado() throws SQLException {
        List<Receta> recetas = cargarRecetas();
        Map<String, Long> conteoPorEstado = new HashMap<>();

        // Inicializar estados conocidos
        conteoPorEstado.put("Confeccionada", 0L);
        conteoPorEstado.put("En proceso", 0L);
        conteoPorEstado.put("Lista", 0L);
        conteoPorEstado.put("Entregada", 0L);

        for (Receta r : recetas) {
            String estado = r.getEstado();
            if (estado != null && conteoPorEstado.containsKey(estado)) {
                conteoPorEstado.put(estado, conteoPorEstado.get(estado) + 1);
            }
        }
        return conteoPorEstado;
    }

    /**
     * Devuelve la cantidad de prescripciones por mes para un medicamento específico
     * dentro de un rango de fechas.
     *
     * @param fechaInicio Fecha de inicio (inclusive)
     * @param fechaFin Fecha de fin (inclusive)
     * @param medicamento Nombre del medicamento
     * @return Mapa con clave "YYYY-MM" y valor cantidad de prescripciones
     */
    public Map<String, Long> prescripcionesPorMes(LocalDate fechaInicio, LocalDate fechaFin, String medicamento) throws SQLException {
        Map<String, Long> prescripcionesPorMes = new TreeMap<>();
        List<Receta> recetas = cargarRecetas();

        for (Receta receta : recetas) {
            LocalDate fecha = receta.getFechaConfeccion();
            if (fecha == null || fecha.isBefore(fechaInicio) || fecha.isAfter(fechaFin)) continue;

            List<Prescripcion> prescripciones = prescripcionLogica.findByRecetaId(receta.getId());
            for (Prescripcion p : prescripciones) {
                if (p.getMedicamento() != null && medicamento.equalsIgnoreCase(p.getMedicamento().getNombre())) {
                    YearMonth mes = YearMonth.from(fecha);
                    String clave = mes.getYear() + "-" + String.format("%02d", mes.getMonthValue());
                    prescripcionesPorMes.put(clave, prescripcionesPorMes.getOrDefault(clave, 0L) + 1);
                }
            }
        }

        return prescripcionesPorMes;
    }
}