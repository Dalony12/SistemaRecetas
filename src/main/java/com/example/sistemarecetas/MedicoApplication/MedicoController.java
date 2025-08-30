package com.example.sistemarecetas.MedicoApplication;


import com.example.sistemarecetas.adminApplication.FarmaceuticosController;
import com.example.sistemarecetas.adminApplication.MedicamentosController;
import com.example.sistemarecetas.adminApplication.MedicosController;
import com.example.sistemarecetas.adminApplication.PacientesController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class MedicoController {

    @FXML private TabPane tabPane;
    @FXML private Tab tabMenuPrincipal;
    @FXML private Tab tabDashboard;
    @FXML private Tab tabHistorial;
    @FXML private Tab tabTuCuenta;

    @FXML
    public void abrirMenuPrincipal(Event event) { }

    @FXML
    public void abrirDashboard(Event event) { }

    @FXML
    public void abrirHistorial(Event event) { }

    @FXML
    public void abrirTuCuenta(Event event) { }

    @FXML
    public void abrirPrescripcion(Event event) { }
}
