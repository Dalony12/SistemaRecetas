package com.example.sistemarecetas.controller.MedicoApplication;

import com.example.sistemarecetas.controller.generalControllers.HistorialController;
import com.example.sistemarecetas.controller.generalControllers.DashboardController;
import com.example.sistemarecetas.controller.generalControllers.TuCuentaController;
import com.example.sistemarecetas.controller.MedicoApplication.PrescripcionController; // Controller de prescripciones
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class MedicoController {

    @FXML private Tab tabMenuPrincipal;
    @FXML private Tab tabDashboard;
    @FXML private Tab tabHistorial;
    @FXML private Tab tabTuCuenta;
    @FXML private Tab tabPrescripcion;

    @FXML
    public void abrirMenuPrincipal(Event event) {
        if (!tabMenuPrincipal.isSelected()) return;
        // Lógica futura para menú principal si es necesario
    }

    @FXML
    public void abrirDashboard(Event event) {
        if (!tabDashboard.isSelected()) return;
        // Refrescar dashboard al seleccionarlo
        DashboardController.getInstance().cargarDashboard();
    }

    @FXML
    public void abrirHistorial(Event event) {
        if (!tabHistorial.isSelected()) return;
        // Refrescar historial al seleccionarlo
        HistorialController.getInstance().cargarRecetasAsync();
    }

    @FXML
    public void abrirTuCuenta(Event event) {
        if (!tabTuCuenta.isSelected()) return;
        // Refrescar datos de la cuenta al seleccionarlo
        TuCuentaController.getInstance().cargarDatosCuenta();
    }

    @FXML
    public void abrirPrescripcion(Event event) {
        if (!tabPrescripcion.isSelected()) return;
        // Refrescar lista de prescripciones al seleccionarlo
        PrescripcionController.getInstance().refrescarDatos();
    }
}
