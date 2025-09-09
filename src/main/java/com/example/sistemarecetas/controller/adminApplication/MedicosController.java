package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Gestores.GestorMedicos;
import com.example.sistemarecetas.Model.Medico;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MedicosController {

    private static MedicosController instance; // Singleton
    public MedicosController() { instance = this; }
    public static MedicosController getInstance() { return instance; }

    // Panel y tabla
    @FXML private VBox vBoxPortadaMedicos;
    @FXML private TableView<Medico> tableMedicos;
    @FXML private TableColumn<Medico, String> colID;
    @FXML private TableColumn<Medico, String> colNombre;
    @FXML private TableColumn<Medico, String> colEspecialidad;

    // Botones de acción (pueden ser RadioButton o ToggleButton)
    @FXML private RadioButton btnGuardarMedico;
    @FXML private RadioButton btnBorrarMedico;
    @FXML private RadioButton btnModificarMedico;
    @FXML private RadioButton btnBuscarMedico;

    // Campos del formulario
    @FXML private TextField txtIDMedico;
    @FXML private TextField txtNombreMedico;
    @FXML private TextField txtEspecialidadMedico;

    // Lista observable + gestor
    private ObservableList<Medico> listaObservable;
    private GestorMedicos gestorMedico = GestorMedicos.getInstancia();

    @FXML
    public void initialize() {
        // Inicializar lista y tabla
        listaObservable = FXCollections.observableArrayList(gestorMedico.getMedicos());
        tableMedicos.setItems(listaObservable);

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));

        // Listeners para que los RadioButton se excluyan mutuamente
        btnGuardarMedico.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnBorrarMedico.setSelected(false); btnModificarMedico.setSelected(false); btnBuscarMedico.setSelected(false); txtNombreMedico.setEditable(true); txtEspecialidadMedico.setEditable(true);}
        });
        btnBorrarMedico.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnGuardarMedico.setSelected(false); btnModificarMedico.setSelected(false); btnBuscarMedico.setSelected(false); txtNombreMedico.setEditable(false); txtEspecialidadMedico.setEditable(false); }
        });
        btnModificarMedico.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { btnGuardarMedico.setSelected(false); btnBorrarMedico.setSelected(false); btnBuscarMedico.setSelected(false); txtNombreMedico.setEditable(true); txtEspecialidadMedico.setEditable(true);}
        });

        btnBuscarMedico.selectedProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal) { btnGuardarMedico.setSelected(false); btnBorrarMedico.setSelected(false); btnModificarMedico.setSelected(false); txtNombreMedico.setEditable(true); txtEspecialidadMedico.setEditable(true);}
        });

        txtIDMedico.textProperty().addListener((obs, oldVal, newVal) -> {
            if (btnBuscarMedico.isSelected()) {
                buscarMedico();
            }
        });

        txtNombreMedico.textProperty().addListener((obs, oldVal, newVal) -> {
            if (btnBuscarMedico.isSelected()) {
                buscarMedico();
            }
        });

        txtEspecialidadMedico.textProperty().addListener((obs, oldVal, newVal) -> {
            if (btnBuscarMedico.isSelected()) {
                buscarMedico();
            }
        });


        // Listener para autocompletar campos cuando escriben el ID
        txtIDMedico.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                txtNombreMedico.clear();
                txtEspecialidadMedico.clear();
                return;
            }
            Medico encontrado = gestorMedico.buscarPorId(newVal);
            if (encontrado != null) {
                txtNombreMedico.setText(encontrado.getNombre());
                txtEspecialidadMedico.setText(encontrado.getEspecialidad());
            } else {
                txtNombreMedico.clear();
                txtEspecialidadMedico.clear();
            }
        });
    }

    // ---------------- Lógica CRUD ----------------

    @FXML
    private void GuardarModificarEliminarMedico() {
        try {
            String nombre = txtNombreMedico.getText().trim();
            String especialidad = txtEspecialidadMedico.getText().trim();
            String identificacion = txtIDMedico.getText().trim();

            if (nombre.isEmpty() || especialidad.isEmpty() || identificacion.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            if (btnGuardarMedico.isSelected()) {
                // Crear nuevo médico
                Medico nuevo = new Medico(identificacion,nombre, identificacion, especialidad);

                for (Medico m : gestorMedico.getMedicos()) {
                    if (m.getId().equals(identificacion)) {
                        mostrarAlerta("Médico ya existe", "Ya existe un médico con esa identificación: " + nuevo.getId());
                        limpiarCampos();
                        return;
                    }
                }
                gestorMedico.agregarMedico(nuevo);
                listaObservable.add(nuevo);

            } else if (btnModificarMedico.isSelected()) {
                Medico existente = gestorMedico.buscarPorId(identificacion);
                if (existente == null) {
                    mostrarAlerta("No encontrado", "No existe un médico con ese ID");
                    limpiarCampos();
                    return;
                }
                existente.setNombre(nombre);
                existente.setEspecialidad(especialidad);

                tableMedicos.refresh();

            } else if (btnBorrarMedico.isSelected()) {
                Medico aEliminar = gestorMedico.buscarPorId(identificacion);
                if (aEliminar == null) {
                    mostrarAlerta("No encontrado", "No existe un médico con ese ID: " + identificacion);
                    return;
                }
                gestorMedico.eliminarMedico(aEliminar);
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
        String criterioID = txtIDMedico.getText().trim().toLowerCase();
        String criterioNombre = txtNombreMedico.getText().trim().toLowerCase();
        String criterioEspecialidad = txtEspecialidadMedico.getText().trim().toLowerCase();

        ObservableList<Medico> filtrados = FXCollections.observableArrayList();

        for (Medico m : gestorMedico.getMedicos()) {
            boolean coincideID = criterioID.isEmpty() || m.getId().toLowerCase().contains(criterioID);
            boolean coincideNombre = criterioNombre.isEmpty() || m.getNombre().toLowerCase().contains(criterioNombre);
            boolean coincideEspecialidad = criterioEspecialidad.isEmpty() || m.getEspecialidad().toLowerCase().contains(criterioEspecialidad);

            if (coincideID && coincideNombre && coincideEspecialidad) {
                filtrados.add(m);
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
        txtIDMedico.clear();
        txtNombreMedico.clear();
        txtEspecialidadMedico.clear();
    }

    // ---------------- Tabla ----------------
    public void refrescarTabla() {
        listaObservable.setAll(gestorMedico.getMedicos());
    }

    // ---------------- Animación ----------------
    public void mostrarListaConAnimacion() {
        if (vBoxPortadaMedicos.isVisible()) {
            // Pausa de 1 segundo antes de iniciar la animación
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                TranslateTransition transition = new TranslateTransition(Duration.seconds(1.5), vBoxPortadaMedicos);
                transition.setToX(-vBoxPortadaMedicos.getWidth() - 20);
                transition.setOnFinished(e -> vBoxPortadaMedicos.setVisible(false));
                transition.play();
            });
            pause.play();
        }
    }
}

