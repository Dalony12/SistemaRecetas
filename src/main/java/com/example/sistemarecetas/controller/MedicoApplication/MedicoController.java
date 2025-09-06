package com.example.sistemarecetas.controller.MedicoApplication;


import com.example.sistemarecetas.Gestores.GestorPacientes;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MedicoController {

    @FXML private TabPane tabPane;
    @FXML private Tab tabMenuPrincipal;
    @FXML private Tab tabDashboard;
    @FXML private Tab tabHistorial;
    @FXML private Tab tabTuCuenta;
    @FXML private Tab tabPrescripcion;
    private GestorPacientes gestorPacientes;

    @FXML
    public void abrirMenuPrincipal(Event event) { }

    @FXML
    public void abrirDashboard(Event event) { }

    @FXML
    public void abrirHistorial(Event event) { }

    @FXML
    public void abrirTuCuenta(Event event) { }

    @FXML
    public void abrirPrescripcion(Event event) {
    }
}
