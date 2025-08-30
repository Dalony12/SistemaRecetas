package com.example.sistemarecetas.adminApplication;


import Gestores.GestorRecetas;
import Model.Medicamento;
import Model.Paciente;
import Model.Prescripcion;
import Model.Receta;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;


public class HistorialController {

    @FXML private TextField txtNombreHistorialRecetas;

    //Tabla de los datos
    @FXML private TableView<Receta> tableRecetas;
    @FXML private TableColumn<Receta, String> colPersona;
    @FXML private TableColumn<Receta, String> colMedicamentos;
    @FXML private TableColumn<Receta, String> colConfeccion;
    @FXML private TableColumn<Receta, String> colRetiro;
    @FXML private TableColumn<Receta, String> colCantidad;
    @FXML private TableColumn<Receta, String> colInidcaiones;
    @FXML private TableColumn<Receta, String> colCantidadDías;

    // Lista observable + gestor
    private ObservableList<Receta> listaObservable;
    private GestorRecetas gestorRecetas = GestorRecetas.getInstancia();

    @FXML
    public void initialize() {
        // =======================
        // DATOS DE PRUEBA
        // =======================
        // Pacientes
        Paciente p1 = new Paciente("med-111", "Juan Pérez", 71978798, LocalDate.of(1995, 5, 21));

        // Medicamentos
        Medicamento m1 = new Medicamento("met","Ibuprofeno", "duro", "dolor");
        Medicamento m2 = new Medicamento("amo","Amoxicilina", "duro", "dolor");

        // Prescripciones
        Prescripcion pr1 = new Prescripcion(m1, 2, "Cada 8 horas", 5);
        Prescripcion pr2 = new Prescripcion(m2, 1, "Cada 12 horas", 7);

        // Recetas
        Receta r1 = new Receta(p1);

        // 5. Agregar la prescripción a la receta
        r1.getMedicamentos().add(pr1);
        r1.getMedicamentos().add(pr2);

        // 6. Opcional: asignar fecha de retiro
        r1.setFechaRetiro(LocalDate.now().plusDays(5));

        // 7. Guardar la receta en el gestor
        gestorRecetas.agregarReceta(r1);

        // Inicializar lista y tabla
        listaObservable = FXCollections.observableArrayList(gestorRecetas.getRecetas());
        tableRecetas.setItems(listaObservable);

        colPersona.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaciente().getNombre()));
        colConfeccion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFechaConfeccion().toString()));
        colRetiro.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaRetiro() != null ? data.getValue().getFechaRetiro().toString() : "Pendiente"
        ));
        colMedicamentos.setCellValueFactory(data -> {
            List<Prescripcion> lista = data.getValue().getMedicamentos();
            String nombres = "";
            for (Prescripcion p : lista) {
                nombres += p.getMedicamento().getNombre() + ", ";
            }
            if (!nombres.isEmpty()) {
                nombres = nombres.substring(0, nombres.length() - 2);
            }
            return new SimpleStringProperty(nombres);
        });
        colCantidad.setCellValueFactory(data -> {
            List<Prescripcion> lista = data.getValue().getMedicamentos();
            String cantidades = "";
            for (Prescripcion p : lista) {
                cantidades += p.getCantidad() + ", ";
            }
            if (!cantidades.isEmpty()) {
                cantidades = cantidades.substring(0, cantidades.length() - 2);
            }
            return new SimpleStringProperty(cantidades);
        });
        colInidcaiones.setCellValueFactory(data -> {
            List<Prescripcion> lista = data.getValue().getMedicamentos();
            String indicaciones = "";
            for (Prescripcion p : lista) {
                indicaciones += p.getIndicaciones() + ", ";
            }
            if (!indicaciones.isEmpty()) {
                indicaciones = indicaciones.substring(0, indicaciones.length() - 2);
            }
            return new SimpleStringProperty(indicaciones);
        });
        colCantidadDías.setCellValueFactory(data -> {
            List<Prescripcion> lista = data.getValue().getMedicamentos();
            String dias = "";
            for (Prescripcion p : lista) {
                dias += p.getDuracionDias() + ", ";
            }
            if (!dias.isEmpty()) {
                dias = dias.substring(0, dias.length() - 2);
            }
            return new SimpleStringProperty(dias);
        });

        txtNombreHistorialRecetas.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarRecetas();
        });
    }

    private void filtrarRecetas() {
        String criterioNombre = txtNombreHistorialRecetas.getText().trim().toLowerCase();

        ObservableList<Receta> filtradas = FXCollections.observableArrayList();

        for (Receta r : gestorRecetas.getRecetas()) {
            String idPaciente = r.getPaciente().getId().toLowerCase();
            String nombrePaciente = r.getPaciente().getNombre().toLowerCase();

            boolean coincideNombre = criterioNombre.isEmpty() || nombrePaciente.contains(criterioNombre);

            if (coincideNombre) {
                filtradas.add(r);
            }
        }

        listaObservable.setAll(filtradas);
    }

}
