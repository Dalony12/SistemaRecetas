package com.example.sistemarecetas.MedicoApplication;

import Gestores.GestorPacientes;
import Model.Paciente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

public class buscarPacienteController {
    @FXML
    private TableView<Paciente> tblPacientePresc;
    @FXML
    private TableColumn<Paciente, String> colIDPaciente;
    @FXML
    private TableColumn<Paciente, String> colNombrePaciente;
    @FXML
    private TableColumn<Paciente, Integer> colTelefonoPaciente;
    @FXML
    private TableColumn<Paciente, LocalDate> colFechaNacPaciente;
    @FXML
    private TextField txtBuscarPacientePresc;
    @FXML
    private ComboBox<String> cmbFiltrarPacientePresc;


    private GestorPacientes gestorPacientes = GestorPacientes.getInstancia();

    @FXML
    public void initialize() {
        colIDPaciente.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombrePaciente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefonoPaciente.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colFechaNacPaciente.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));

        tblPacientePresc.setItems(FXCollections.observableArrayList());
    }

    @FXML
    public void buscarPaciente(ActionEvent event) {
        String nombre = txtBuscarPacientePresc.getText();

        Paciente paciente = gestorPacientes.buscarPorNombre(nombre);
    }

    @FXML
    public void inicializarFiltrado() {
        if (cmbFiltrarPacientePresc.getItems().isEmpty()) {
            ObservableList<String> opciones = FXCollections.observableArrayList(
                    "Nombre",
                    "ID"
            );
            cmbFiltrarPacientePresc.setItems(opciones);
            cmbFiltrarPacientePresc.getSelectionModel().selectFirst();
        }
    }

}
