package com.example.sistemarecetas.controller.adminApplication;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class AdminController {

    @FXML private Tab tabMedicos;
    @FXML private Tab tabFarmaceuticos;
    @FXML private Tab tabPacientes;
    @FXML private Tab tabMedicamentos;

    @FXML
    public void abrirMenuPrincipal(Event event) { }

    @FXML
    public void abrirMedicos(Event event) {
        if (tabMedicos.isSelected()) {MedicosController.getInstance().mostrarListaConAnimacion();}
    }

    @FXML
    public void abrirFarmaceuticos(Event event) {
        if (tabFarmaceuticos.isSelected()) {FarmaceuticosController.getInstance().mostrarListaConAnimacion();}
    }

    @FXML
    public void abrirPacientes(Event event) {
        if (tabPacientes.isSelected()) {PacientesController.getInstance().mostrarListaConAnimacion();}
    }

    @FXML
    public void abrirMedicamentos(Event event) {
        if (tabMedicamentos.isSelected()) {MedicamentosController.getInstance().mostrarListaConAnimacion();}
    }

    @FXML
    public void abrirDashboard(Event event) { }

    @FXML
    public void abrirHistorial(Event event) { }
}
