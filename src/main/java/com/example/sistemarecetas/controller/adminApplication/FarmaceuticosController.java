package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Farmaceutico;
import com.example.sistemarecetas.logica.farmaceutas.FarmaceutasLogica;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class FarmaceuticosController {

    private static FarmaceuticosController instance; // Singleton
    public FarmaceuticosController() { instance = this; }
    public static FarmaceuticosController getInstance() { return instance; }

    // Panel y tabla
    @FXML private VBox vBoxPortadaFarmaceuta;
    @FXML private TableView<Farmaceutico> tableFarmaceuticos;
    @FXML private TableColumn<Farmaceutico, String> colIDFarma;
    @FXML private TableColumn<Farmaceutico, String> colNombreFarma;

    // Botones de acción
    @FXML private RadioButton btnGuardarFarmaceutico;
    @FXML private RadioButton btnBorrarFarmaceutico;
    @FXML private RadioButton btnModificarFarmaceutico;
    @FXML private RadioButton btnBuscarFarmaceutico;

    // Campos del formulario
    @FXML private TextField txtIDFarmaceuta;
    @FXML private TextField txtNombreFarmaceuta;

    private ObservableList<Farmaceutico> listaObservable;
    private FarmaceutasLogica farmaeutaLogica;

    @FXML
    public void initialize() {
        try {
            // Ruta dinámica al archivo XML
            String rutaXML = Paths.get(System.getProperty("user.dir"), "datos", "farmaceutas.xml").toString();
            File archivo = new File(rutaXML);

            // Crear archivo si no existe
            if (!archivo.exists()) {
                archivo.getParentFile().mkdirs();
                String contenidoInicial = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <farmaceuticos>
                    </farmaceuticos>
                    """;
                try (FileWriter writer = new FileWriter(archivo)) {
                    writer.write(contenidoInicial);
                    System.out.println("Archivo farmaceutas.xml creado en: " + rutaXML);
                }
            }

            // Inicializar lógica y tabla
            farmaeutaLogica = new FarmaceutasLogica(rutaXML);
            listaObservable = FXCollections.observableArrayList(farmaeutaLogica.findAll());
            tableFarmaceuticos.setItems(listaObservable);

            colIDFarma.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNombreFarma.setCellValueFactory(new PropertyValueFactory<>("nombre"));

            // Eventos de botones
            btnGuardarFarmaceutico.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) toggleMode("guardar");
            });
            btnBorrarFarmaceutico.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) toggleMode("borrar");
            });
            btnModificarFarmaceutico.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) toggleMode("modificar");
            });
            btnBuscarFarmaceutico.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) toggleMode("buscar");
            });

            // Búsqueda reactiva
            txtIDFarmaceuta.textProperty().addListener((obs, oldVal, newVal) -> {
                if (btnBuscarFarmaceutico.isSelected()) {
                    buscarFarmaceuta();
                }
            });

            txtNombreFarmaceuta.textProperty().addListener((obs, oldVal, newVal) -> {
                if (btnBuscarFarmaceutico.isSelected()) {
                    buscarFarmaceuta();
                }
            });

            // Autocompletar nombre al escribir ID existente
            txtIDFarmaceuta.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.isEmpty()) {
                    limpiarCampos();
                    return;
                }
                Optional<Farmaceutico> encontrado = farmaeutaLogica.findByCodigo(newVal);
                if (encontrado.isPresent()) {
                    Farmaceutico m = encontrado.get();
                    txtNombreFarmaceuta.setText(m.getNombre());
                } else {
                    txtNombreFarmaceuta.clear();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al inicializar", e.getMessage());
        }
    }

    @FXML
    private void GuardarModificarEliminarFarmaceuta() {
        try {
            String id = txtIDFarmaceuta.getText().trim();
            String nombre = txtNombreFarmaceuta.getText().trim();

            if (id.isEmpty() || nombre.isEmpty()) {
                mostrarAlerta("Campos incompletos", "Debe llenar todos los campos del formulario");
                return;
            }

            Farmaceutico m = new Farmaceutico(id, nombre, id);

            if (btnGuardarFarmaceutico.isSelected()) {
                farmaeutaLogica.create(m);
                listaObservable.add(m);

            } else if (btnModificarFarmaceutico.isSelected()) {
                farmaeutaLogica.update(m);
                refrescarTabla();

            } else if (btnBorrarFarmaceutico.isSelected()) {
                boolean eliminado = farmaeutaLogica.deleteByCodigo(id);
                if (eliminado) {
                    listaObservable.removeIf(x -> x.getId().equalsIgnoreCase(id));
                } else {
                    mostrarAlerta("No encontrado", "No existe un farmaceutico con ese ID: " + id);
                }
            }

            limpiarCampos();
            refrescarTabla();

        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void buscarFarmaceuta() {
        String id = txtIDFarmaceuta.getText().trim();
        String nombre = txtNombreFarmaceuta.getText().trim();

        if (id.isEmpty() && nombre.isEmpty()) {
            refrescarTabla();
            return;
        }

        List<Farmaceutico> resultado = farmaeutaLogica.search(id, nombre);
        listaObservable.setAll(resultado);
    }

    private void toggleMode(String mode) {
        btnGuardarFarmaceutico.setSelected(mode.equals("guardar"));
        btnBorrarFarmaceutico.setSelected(mode.equals("borrar"));
        btnModificarFarmaceutico.setSelected(mode.equals("modificar"));
        btnBuscarFarmaceutico.setSelected(mode.equals("buscar"));

        boolean editable = !mode.equals("borrar");
        txtNombreFarmaceuta.setEditable(editable);
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

    public void refrescarTabla() {
        listaObservable.setAll(farmaeutaLogica.findAll());
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


