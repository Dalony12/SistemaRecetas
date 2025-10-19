package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.controller.generalControllers.DashboardController;
import com.example.sistemarecetas.controller.generalControllers.HistorialController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class AdminController {

    @FXML private Tab tabMedicos;
    @FXML private Tab tabFarmaceuticos;
    @FXML private Tab tabPacientes;
    @FXML private Tab tabMedicamentos;
    @FXML private Tab tabHistorial;
    @FXML private Tab tabDashboard;

    @FXML
    public void abrirMenuPrincipal(Event event) { }

    @FXML
    public void abrirMedicos(Event event) {
        if (tabMedicos.isSelected()) {
            MedicosController.getInstance().cargarMedicos(); // refresca los datos desde XML
            MedicosController.getInstance().mostrarListaConAnimacion();
        }
    }

    @FXML
    public void abrirFarmaceuticos(Event event) {
        if (tabFarmaceuticos.isSelected()) {
            FarmaceuticosController.getInstance().cargarFarmaceuticos(); // refresca datos desde XML
            FarmaceuticosController.getInstance().mostrarListaConAnimacion();
        }
    }

    @FXML
    public void abrirPacientes(Event event) {
        if (tabPacientes.isSelected()) {
            PacientesController.getInstance().cargarPacientes(); // refresca datos desde XML
            PacientesController.getInstance().mostrarListaConAnimacion();
        }
    }

    @FXML
    public void abrirMedicamentos(Event event) {
        if (tabMedicamentos.isSelected()) {
            MedicamentosController.getInstance().cargarMedicamentos(); // refresca datos desde XML
            MedicamentosController.getInstance().mostrarListaConAnimacion();
        }
    }

    @FXML
    public void abrirDashboard(Event event) {
        if (tabDashboard.isSelected()) {
            DashboardController.getInstance().cargarDashboard(); // refresca gráficos y combobox
        }
    }

    @FXML
    public void abrirHistorial(Event event) {
        if (tabHistorial.isSelected()) {
            HistorialController.getInstance().cargarHistorial(); // refresca la tabla de recetas
        }
    }
}