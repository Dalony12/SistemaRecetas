package com.example.sistemarecetas.controller.generalControllers;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.logica.DashboardLogica;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Controlador del Dashboard conectado a base de datos.
 * Muestra:
 *  - Gráfico de línea: cantidad de medicamentos prescritos por mes.
 *  - Gráfico de pastel: cantidad de recetas por estado.
 *  - Carga datos automáticamente al abrir el tab.
 */
public class DashboardController {

    // ===== Singleton =====
    private static DashboardController instance;

    public DashboardController() {
        instance = this;
    }

    public static DashboardController getInstance() {
        return instance;
    }

    // ===== FXML =====
    @FXML private Tab tabDashboard; // Tab que contiene el Dashboard
    @FXML private LineChart<String, Number> graficoLineal;
    @FXML private PieChart graficoPastel;
    @FXML private ComboBox<String> cmbMedicamento;
    @FXML private DatePicker fechaInicioDashboard;
    @FXML private DatePicker fechaFinDashboard;
    @FXML private Button btnEnviar;

    // ===== Lógica =====
    private DashboardLogica service;

    // ===== Inicialización =====
    @FXML
    public void initialize() {
        this.service = new DashboardLogica();

        // Cargar inmediatamente (asegura que aparezcan datos al abrir la app)
        try {
            cargarDashboard();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Si el Tab está enlazado en el FXML, también recarga al seleccionarlo
        if (tabDashboard != null) {
            tabDashboard.setOnSelectionChanged(event -> {
                if (tabDashboard.isSelected()) {
                    cargarDashboard();
                }
            });
        }
    }

    /**
     * Carga inicial: llena medicamentos y muestra gráfico pastel.
     */
    public void cargarDashboard() {
        try {
            List<Receta> listaRecetas = service.cargarRecetas();

            // Obtener nombres únicos de medicamentos desde las prescripciones
            Set<String> nombresMedicamentos = new HashSet<>();
            for (Receta receta : listaRecetas) {
                for (Prescripcion p : receta.getMedicamentos()) {
                    Medicamento m = p.getMedicamento();
                    if (m != null) nombresMedicamentos.add(m.getNombre());
                }
            }

            // Cargar nombres al ComboBox
            ObservableList<String> medicamentos = FXCollections.observableArrayList(nombresMedicamentos);
            cmbMedicamento.setItems(medicamentos);

            // Seleccionar uno por defecto si hay disponibles
            if (!medicamentos.isEmpty()) {
                cmbMedicamento.getSelectionModel().selectFirst();
            }

            // Cargar gráfico de pastel (recetas por estado)
            cargarGraficoPie();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los datos del Dashboard desde la base de datos.");
        }
    }

    // ===== Gráfico de línea =====
    @FXML
    private void cargarGraficoLineal() {
        try {
            LocalDate fechaInicio = fechaInicioDashboard.getValue();
            LocalDate fechaFin = fechaFinDashboard.getValue();
            String medicamento = cmbMedicamento.getValue();

            if (fechaInicio == null || fechaFin == null || medicamento == null || medicamento.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe seleccionar fechas y un medicamento antes de continuar.");
                return;
            }

            Map<String, Long> datos = service.prescripcionesPorMes(fechaInicio, fechaFin, medicamento);

            graficoLineal.getData().clear();
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName("Prescripciones de " + medicamento);

            for (Map.Entry<String, Long> entry : datos.entrySet()) {
                serie.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            if (serie.getData().isEmpty()) {
                mostrarAlerta("Sin datos", "No se encontraron prescripciones para ese medicamento en el rango indicado.");
            }

            graficoLineal.getData().add(serie);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el gráfico de prescripciones por mes.");
        }
    }

    // ===== Gráfico de pastel =====
    private void cargarGraficoPie() {
        try {
            // Forzar carga de recetas desde la lógica (BD) y construir el mapa localmente
            List<Receta> recetas = service.cargarRecetas();
            System.out.println("[Dashboard] recetas cargadas (total): " + (recetas != null ? recetas.size() : 0));

            Map<String, Long> conteoPorEstado = new LinkedHashMap<>();
            if (recetas != null) {
                for (Receta r : recetas) {
                    String estado = r.getEstado();
                    if (estado == null || estado.isBlank()) {
                        estado = "Desconocido";
                    } else {
                        estado = estado.trim();
                    }
                    conteoPorEstado.put(estado, conteoPorEstado.getOrDefault(estado, 0L) + 1);
                }
            }

            System.out.println("[Dashboard] conteoPorEstado calculado localmente: " + conteoPorEstado);

            // Llenar PieChart
            graficoPastel.getData().clear();
            for (Map.Entry<String, Long> entrada : conteoPorEstado.entrySet()) {
                String estado = entrada.getKey();
                long cantidad = entrada.getValue() == null ? 0L : entrada.getValue();
                System.out.println("[Dashboard] estado='" + estado + "' cantidad=" + cantidad);
                if (cantidad > 0) {
                    graficoPastel.getData().add(new PieChart.Data(estado + " (" + cantidad + ")", cantidad));
                }
            }

            // Si no hay sectores con >0, mostrar un sector "Sin datos" para que el control no quede vacío
            if (graficoPastel.getData().isEmpty()) {
                System.out.println("[Dashboard] No hay recetas con estado válido — mostrando 'Sin datos'.");
                graficoPastel.getData().add(new PieChart.Data("Sin datos", 1));
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el gráfico de recetas por estado: " + e.getMessage());
        }
    }

    // ===== Utilidad =====
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
