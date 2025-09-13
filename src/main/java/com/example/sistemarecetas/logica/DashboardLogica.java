package com.example.sistemarecetas.logica;

import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.logica.recetas.RecetasLogica;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardLogica {
    private final RecetasLogica recetasLogica;

    public DashboardLogica(String rutaXmlClientes) {
        this.recetasLogica = new RecetasLogica(rutaXmlClientes);
    }

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

        conteoPorEstado.put("En proceso", 0L);
        conteoPorEstado.put("Lista", 0L);
        conteoPorEstado.put("Entregado", 0L);

        for (Receta receta : recetas) {
            String estado = receta.getEstado();
            conteoPorEstado.computeIfPresent(estado, (key, value) -> value + 1);
        }
        return conteoPorEstado;
    }
}
