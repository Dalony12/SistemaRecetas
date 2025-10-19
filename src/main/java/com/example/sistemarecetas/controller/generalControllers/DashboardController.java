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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DashboardController {

    private static DashboardController instance;

    @FXML private LineChart<String, Number> graficoLineal;
    @FXML private PieChart graficoPastel;
    @FXML private ComboBox<String> cmbMedicamento;
    @FXML private DatePicker fechaInicioDashboard;
    @FXML private DatePicker fechaFinDashboard;
    @FXML private Button btnEnviar;

    private DashboardLogica service;

    public DashboardController() {
        instance = this;
    }

    public static DashboardController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        // Inicializar el servicio directamente con la DB
        this.service = new DashboardLogica(); // usa constructor sin archivos
        cargarDashboard();
    }


    public void cargarDashboard() {
        try {
            List<Receta> lista = service.cargarRecetas();

            // Obtener nombres únicos de medicamentos
            Set<String> nombresUnicos = new HashSet<>();
            for (Receta r : lista) {
                List<Prescripcion> prescripciones = r.getMedicamentos();
                for (Prescripcion p : prescripciones) {
                    Medicamento m = p.getMedicamento();
                    if (m != null) {
                        nombresUnicos.add(m.getNombre());
                    }
                }
            }

            ObservableList<String> medicamentos = FXCollections.observableArrayList(nombresUnicos);
            cmbMedicamento.setItems(medicamentos);

            cargarGraficoPie();
        } catch (Exception e) {
            mostrarAlerta("Cargar Dashboard", "No se pudo cargar la información desde la base de datos.");
            e.printStackTrace();
        }
    }

    @FXML
    private void cargarGraficoLineal() {
        try {
            LocalDate fechaInicio = fechaInicioDashboard.getValue();
            LocalDate fechaFin = fechaFinDashboard.getValue();
            String medicamento = cmbMedicamento.getValue();

            if (fechaInicio == null || fechaFin == null || medicamento == null || medicamento.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe completar todos los espacios.");
                return;
            }

            Map<String, Long> datos = service.prescripcionesPorMes(fechaInicio, fechaFin, medicamento);

            graficoLineal.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Cantidad de Prescripciones de " + medicamento);

            for (Map.Entry<String, Long> entry : datos.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            graficoLineal.getData().add(series);
        } catch(Exception e) {
            mostrarAlerta("Cargar gráfico", "No se pudo cargar el gráfico");
            e.printStackTrace();
        }
    }

    private void cargarGraficoPie() {
        try {
            Map<String, Long> conteoPorEstado = service.recetaPorEstado();
            graficoPastel.getData().clear();

            for (Map.Entry<String, Long> entrada : conteoPorEstado.entrySet()) {
                String estado = entrada.getKey();
                long cantidad = entrada.getValue();

                if (cantidad > 0) {
                    PieChart.Data sector = new PieChart.Data(estado + " (" + cantidad + ")", cantidad);
                    graficoPastel.getData().add(sector);
                }
            }
        } catch (Exception e) {
            mostrarAlerta("Cargar gráfico Pie", "No se pudo cargar el gráfico de pastel");
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
