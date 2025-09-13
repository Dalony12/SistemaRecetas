package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Medicamento;
import com.example.sistemarecetas.Model.Prescripcion;
import com.example.sistemarecetas.Model.Receta;
import com.example.sistemarecetas.logica.DashboardLogica;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class DashboardController implements Initializable {
    private static final String RUTA_RECETAS = java.nio.file.Paths
            .get(System.getProperty("user.dir"), "datos", "recetas.xml")
            .toString();
    @FXML private LineChart<Number, LocalDate> graficoLineal;
    @FXML private PieChart graficoPastel;
    @FXML private ComboBox<String> cmbMedicamento;
    @FXML private DatePicker fechaInicioDashboard;
    @FXML private DatePicker fechaFinDashboard;

    private DashboardLogica service;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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

        cargarGraficos();
    }

    private void cargarGraficos() {
        LocalDate fechaInicio = fechaInicioDashboard.getValue();
        LocalDate fechaFin = fechaFinDashboard.getValue();
        String medicamento = cmbMedicamento.getValue();

        //service.graficoLinea(fechaInicio, fechaFin, medicamento);

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

}
