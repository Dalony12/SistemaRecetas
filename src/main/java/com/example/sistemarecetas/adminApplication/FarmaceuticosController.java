package com.example.sistemarecetas.adminApplication;

import Gestores.GestorFarmaceuticos;
import Gestores.GestorMedicos;
import Model.Farmaceutico;
import Model.Medico;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class FarmaceuticosController {

    private static FarmaceuticosController instance; // Singleton
    public FarmaceuticosController() { instance = this; }
    public static FarmaceuticosController getInstance() { return instance; }

    // Panel y tabla
    @FXML private VBox vBoxPortadaFarmaceuta;
    @FXML private TableView<Farmaceutico> tableFarmaceuticos;
    @FXML private TableColumn<Farmaceutico, String> colIDFarma;
    @FXML private TableColumn<Farmaceutico, String> colNombreFarma;

    // Botones de acción (pueden ser RadioButton o ToggleButton)
    @FXML private RadioButton btnGuardarFarmaceutico;
    @FXML private RadioButton btnBorrarFarmaceutico;
    @FXML private RadioButton btnModificarFarmaceutico;
    @FXML private RadioButton btnBuscarFarmaceutico;

    // Campos del formulario
    @FXML private TextField txtIDFarmaceuta;
    @FXML private TextField txtNombreFarmaceuta;

    // Lista observable + gestor
    private ObservableList<Farmaceutico> listaObservable;
    private GestorFarmaceuticos gestorFamaceutico = GestorFarmaceuticos.getInstancia();

    @FXML
    public void initialize() {
        // Inicializar lista y tabla
        listaObservable = FXCollections.observableArrayList(gestorFamaceutico.getFarmaceuticos());
        tableFarmaceuticos.setItems(listaObservable);

        colIDFarma.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombreFarma.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Listeners para que los RadioButton se excluyan mutuamente
        btnGuardarFarmaceutico.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnBorrarFarmaceutico.setSelected(false); btnModificarFarmaceutico.setSelected(false); btnBuscarFarmaceutico.setSelected(false); }
        });
        btnBorrarFarmaceutico.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnGuardarFarmaceutico.setSelected(false); btnModificarFarmaceutico.setSelected(false); btnBuscarFarmaceutico.setSelected(false); }
        });
        btnModificarFarmaceutico.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnGuardarFarmaceutico.setSelected(false); btnBorrarFarmaceutico.setSelected(false); btnBuscarFarmaceutico.setSelected(false); }
        });

        btnBuscarFarmaceutico.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnGuardarFarmaceutico.setSelected(false); btnBorrarFarmaceutico.setSelected(false); btnModificarFarmaceutico.setSelected(false); }
        });

        txtIDFarmaceuta.textProperty().addListener((obs, oldVal, newVal) -> {
            if (btnBuscarFarmaceutico.isSelected()) {
                buscarMedico();
            }
        });

        txtNombreFarmaceuta.textProperty().addListener((obs, oldVal, newVal) -> {
            if (btnBuscarFarmaceutico.isSelected()) {
                buscarMedico();
            }
        });


        // Listener para autocompletar campos cuando escriben el ID
        txtIDFarmaceuta.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                txtNombreFarmaceuta.clear();
                return;
            }
            Farmaceutico encontrado = gestorFamaceutico.buscarPorid(newVal);
            if (encontrado != null) {
                txtNombreFarmaceuta.setText(encontrado.getNombre());
            } else {
                txtNombreFarmaceuta.clear();
            }
        });
    }

    // ---------------- Lógica CRUD ----------------

    @FXML
    private void GuardarModificarEliminarMedico() {
        try {
            String nombre = txtNombreFarmaceuta.getText().trim();
            String identificacion = txtIDFarmaceuta.getText().trim();

            if (nombre.isEmpty() || identificacion.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            if (btnGuardarFarmaceutico.isSelected()) {
                // Crear nuevo médico
                Farmaceutico nuevo = new Farmaceutico(identificacion, nombre, identificacion);

                for (Farmaceutico m : gestorFamaceutico.getFarmaceuticos()) {
                    if (m.getId().equals(identificacion)) {
                        mostrarAlerta("Médico ya existe", "Ya existe un Farmaceutico con esa identificación: " + nuevo.getId());
                        limpiarCampos();
                        return;
                    }
                }
                gestorFamaceutico.agregarFarmaceuta(nuevo);
                listaObservable.add(nuevo);

            } else if (btnModificarFarmaceutico.isSelected()) {
                Farmaceutico existente = gestorFamaceutico.buscarPorid(identificacion);
                if (existente == null) {
                    mostrarAlerta("No encontrado", "No existe un médico con ese ID");
                    limpiarCampos();
                    return;
                }
                existente.setNombre(nombre);

                tableFarmaceuticos.refresh();

            } else if (btnBorrarFarmaceutico.isSelected()) {
                Farmaceutico aEliminar = gestorFamaceutico.buscarPorid(identificacion);
                if (aEliminar == null) {
                    mostrarAlerta("No encontrado", "No existe un Farmacetico con ese ID: " + identificacion);
                    return;
                }
                gestorFamaceutico.eliminarFarmaceutico(aEliminar);
                listaObservable.remove(aEliminar);
            }

            limpiarCampos();
            refrescarTabla();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarMedico() {
        String criterioID = txtIDFarmaceuta.getText().trim().toLowerCase();
        String criterioNombre = txtNombreFarmaceuta.getText().trim().toLowerCase();

        ObservableList<Farmaceutico> filtrados = FXCollections.observableArrayList();

        for (Farmaceutico f : gestorFamaceutico.getFarmaceuticos()) {
            boolean coincideID = criterioID.isEmpty() || f.getId().toLowerCase().contains(criterioID);
            boolean coincideNombre = criterioNombre.isEmpty() || f.getNombre().toLowerCase().contains(criterioNombre);

            if (coincideID && coincideNombre) {
                filtrados.add(f);
            }
        }

        listaObservable.setAll(filtrados);
    }


    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void limpiarCampos() {
        txtIDFarmaceuta.clear();
        txtNombreFarmaceuta.clear();

    }

    // ---------------- Tabla ----------------
    public void refrescarTabla() {
        listaObservable.setAll(gestorFamaceutico.getFarmaceuticos());
    }

    // ---------------- Animación ----------------
    public void mostrarListaConAnimacion() {
        if (vBoxPortadaFarmaceuta.isVisible()) {
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                TranslateTransition transition = new TranslateTransition(Duration.seconds(1.5), vBoxPortadaFarmaceuta);
                transition.setToX(-vBoxPortadaFarmaceuta.getWidth() - 20);
                transition.setOnFinished(e -> vBoxPortadaFarmaceuta.setVisible(false));
                transition.play();
            });
            pause.play();
        }
    }
}

