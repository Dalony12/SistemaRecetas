package com.example.sistemarecetas.controller.FarmaceuticoApplication;

import com.example.sistemarecetas.controller.generalControllers.HistorialController;
import com.example.sistemarecetas.controller.generalControllers.DashboardController;
import com.example.sistemarecetas.controller.generalControllers.TuCuentaController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class FarmaController {

    @FXML private Tab tabMenuPrincipal;
    @FXML private Tab tabDashboard;
    @FXML private Tab tabHistorial;
    @FXML private Tab tabTuCuenta;
    @FXML private Tab tabDespacho;

    @FXML
    public void abrirMenuPrincipal(Event event) {
        if (!tabMenuPrincipal.isSelected()) return;
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
        HistorialController.getInstance().cargarHistorial();
    }

    @FXML
    public void abrirTuCuenta(Event event) {
        if (!tabTuCuenta.isSelected()) return;
        TuCuentaController.getInstance().cargarDatosCuenta();
    }

    @FXML
    public void abrirDespacho(Event event) {
        if (!tabDespacho.isSelected()) return;
        DespachoController.getInstance().refrescarTabla();
    }
}
