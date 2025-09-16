package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.logica.DashboardLogica;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class DashboardController {
    private static final String RUTA_RECETAS = java.nio.file.Paths
            .get(System.getProperty("user.dir"), "datos", "recetas.xml")
            .toString();
    @FXML private LineChart<String, Number> graficoLineal;
    @FXML private PieChart graficoPastel;
    @FXML private ComboBox<String> cmbMedicamento;
    @FXML private DatePicker fechaInicioDashboard;
    @FXML private DatePicker fechaFinDashboard;
    @FXML Button btnEnviar;

    private DashboardLogica service;


    public void cargarDashboard() {
        this.service = new DashboardLogica(RUTA_RECETAS);

        List<Receta> lista = service.cargarRecetas();

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
    }

    @FXML
    private void cargarGraficoLineal() {
        try {
            LocalDate fechaInicio = fechaInicioDashboard.getValue();
            LocalDate fechaFin = fechaFinDashboard.getValue();
            String medicamento = cmbMedicamento.getValue();

            if (fechaInicio == null || fechaFin == null || medicamento == null || medicamento.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe de completar todo los espacios.");
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
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
