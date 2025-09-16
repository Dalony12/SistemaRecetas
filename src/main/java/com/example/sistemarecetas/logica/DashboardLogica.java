package com.example.sistemarecetas.logica;

import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.logica.recetas.RecetasLogica;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DashboardLogica {
    private final RecetasLogica recetasLogica;

    /** Constructor actualizado con todas las rutas necesarias */
    public DashboardLogica(String rutaRecetas, String rutaPacientes, String rutaMedicamentos) {
        this.recetasLogica = new RecetasLogica(rutaRecetas, rutaPacientes, rutaMedicamentos);
    }

    /** Constructor alternativo si ya tienes una instancia de RecetasLogica */
    public DashboardLogica(RecetasLogica logica) {
        this.recetasLogica = logica;
    }

    public List<Receta> cargarRecetas() {
        return recetasLogica.findAll();
    }

    public int totalRecetas() {
        return cargarRecetas().size();
    }

    public Map<String, Long> recetaPorEstado() {
        List<Receta> recetas = recetasLogica.findAll();
        Map<String, Long> conteoPorEstado = new HashMap<>();

        conteoPorEstado.put("Confeccionada",0L);
        conteoPorEstado.put("En proceso", 0L);
        conteoPorEstado.put("Lista", 0L);
        conteoPorEstado.put("Entregada", 0L);

        for (Receta receta : recetas) {
            String estado = receta.getEstado();
            conteoPorEstado.computeIfPresent(estado, (key, value) -> value + 1);
        }
        return conteoPorEstado;
    }

    public Map<String, Long> prescripcionesPorMes(LocalDate fechaInicio, LocalDate fechaFinal, String medicamento) {
        Map<String, Long> prescripcionesPorMes = new TreeMap<>();
        List<Receta> listaRecetas = recetasLogica.findAll();

        for (Receta receta : listaRecetas) {
            if (receta.getFechaConfeccion().isAfter(fechaInicio.minusDays(1)) && receta.getFechaConfeccion().isBefore(fechaFinal.plusDays(1))) {
                for (Prescripcion p : receta.getMedicamentos()) {
                    if (p.getMedicamento().getNombre().equals(medicamento)) {
                        YearMonth mesReceta = YearMonth.from(receta.getFechaConfeccion());
                        String annioMes = mesReceta.getMonth().toString();

                        prescripcionesPorMes.put(annioMes, prescripcionesPorMes.getOrDefault(annioMes, 0L) + 1);
                    }
                }
            }
        }
        return prescripcionesPorMes;
    }
}
