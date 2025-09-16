package com.example.sistemarecetas.controller.MedicoApplication;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import com.example.sistemarecetas.controller.adminApplication.DashboardController;

public class MedicoController {

    @FXML private TabPane tabPane;
    @FXML private Tab tabMenuPrincipal;
    @FXML private Tab tabDashboard;
    @FXML private Tab tabHistorial;
    @FXML private Tab tabTuCuenta;
    @FXML private Tab tabPrescripcion;
    @FXML private HistorialController includeHistorialController;
    @FXML private DashboardController includeDashboardController;


    @FXML
    public void abrirMenuPrincipal(Event event) { }

    @FXML
    public void abrirDashboard(Event event) {
        if (tabDashboard.isSelected()) {
            includeDashboardController.cargarDashboard();
        }
    }

    @FXML
    public void abrirHistorial(Event event) {
        if (tabHistorial.isSelected()) {
            includeHistorialController.cargarHistorial();
        }
    }

    @FXML
    public void abrirTuCuenta(Event event) { }

    @FXML
    public void abrirPrescripcion(Event event) {
    }
}
