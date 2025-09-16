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

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class DashboardController {

    private static DashboardController instance; // 🔑 Singleton

    private static final String RUTA_RECETAS = Paths.get(System.getProperty("user.dir"), "datos", "recetas.xml").toString();
    private static final String RUTA_PACIENTES = Paths.get(System.getProperty("user.dir"), "datos", "pacientes.xml").toString();
    private static final String RUTA_MEDICAMENTOS = Paths.get(System.getProperty("user.dir"), "datos", "medicamentos.xml").toString();

    @FXML private LineChart<String, Number> graficoLineal;
    @FXML private PieChart graficoPastel;
    @FXML private ComboBox<String> cmbMedicamento;
    @FXML private DatePicker fechaInicioDashboard;
    @FXML private DatePicker fechaFinDashboard;
    @FXML private Button btnEnviar;

    private DashboardLogica service;

    public DashboardController() {
        instance = this; // Cada vez que JavaFX cree la instancia del FXML, se setea el singleton
    }

    public static DashboardController getInstance() {
        return instance;
    }

    public void cargarDashboard() {
        // Asegurarse de que los archivos existen
        inicializarArchivo(RUTA_RECETAS, "recetas");
        inicializarArchivo(RUTA_PACIENTES, "pacientes");
        inicializarArchivo(RUTA_MEDICAMENTOS, "medicamentos");

        // Crear la lógica con las tres rutas
        this.service = new DashboardLogica(RUTA_RECETAS, RUTA_PACIENTES, RUTA_MEDICAMENTOS);

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

    /** Inicializa un archivo XML si no existe */
    private void inicializarArchivo(String ruta, String rootElement) {
        try {
            File archivo = new File(ruta);
            if (!archivo.exists()) {
                archivo.getParentFile().mkdirs();
                try (FileWriter writer = new FileWriter(archivo)) {
                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><" + rootElement + "></" + rootElement + ">");
                }
            }
        } catch (Exception e) {
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