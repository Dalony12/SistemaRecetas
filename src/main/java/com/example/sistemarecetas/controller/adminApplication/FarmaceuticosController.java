package com.example.sistemarecetas.controller.adminApplication;

import com.example.sistemarecetas.Model.Farmaceutico;
import com.example.sistemarecetas.logica.farmaceutas.FarmaceutasLogica;
import javafx.animation.PauseTransition;
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

    private static FarmaceuticosController instance;
    public FarmaceuticosController() { instance = this; }
    public static FarmaceuticosController getInstance() { return instance; }

    @FXML private VBox vBoxPortadaFarmaceuta;
    @FXML private TableView<Farmaceutico> tableFarmaceuticos;
    @FXML private TableColumn<Farmaceutico, String> colIDFarma;
    @FXML private TableColumn<Farmaceutico, String> colNombreFarma;

    @FXML private RadioButton btnGuardarFarmaceutico;
    @FXML private RadioButton btnBorrarFarmaceutico;
    @FXML private RadioButton btnModificarFarmaceutico;
    @FXML private RadioButton btnBuscarFarmaceutico;

    @FXML private TextField txtIDFarmaceuta;
    @FXML private TextField txtNombreFarmaceuta;

    private ObservableList<Farmaceutico> listaObservable;
    private FarmaceutasLogica farmaceutaLogica;

    private String currentMode = "guardar"; // modo activo

    @FXML
    public void initialize() {
        try {
            String rutaXML = Paths.get(System.getProperty("user.dir"), "datos", "farmaceutas.xml").toString();
            File archivo = new File(rutaXML);

            if (!archivo.exists()) {
                archivo.getParentFile().mkdirs();
                String contenidoInicial = """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <farmaceuticos>
                        </farmaceuticos>
                        """;
                try (FileWriter writer = new FileWriter(archivo)) {
                    writer.write(contenidoInicial);
                }
            }

            farmaceutaLogica = new FarmaceutasLogica(rutaXML);
            listaObservable = FXCollections.observableArrayList(farmaceutaLogica.findAll());
            tableFarmaceuticos.setItems(listaObservable);

            colIDFarma.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNombreFarma.setCellValueFactory(new PropertyValueFactory<>("nombre"));

            // Inicializar modo por defecto
            toggleMode("guardar");

            // Eventos de botones
            btnGuardarFarmaceutico.setOnAction(e -> toggleMode("guardar"));
            btnBorrarFarmaceutico.setOnAction(e -> toggleMode("borrar"));
            btnModificarFarmaceutico.setOnAction(e -> toggleMode("modificar"));
            btnBuscarFarmaceutico.setOnAction(e -> toggleMode("buscar"));

            // Reacción de campos
            txtIDFarmaceuta.textProperty().addListener((obs, oldVal, newVal) -> {
                handleIDChange(newVal);
            });

            txtNombreFarmaceuta.textProperty().addListener((obs, oldVal, newVal) -> {
                if (currentMode.equals("guardar") && !newVal.isEmpty() && txtIDFarmaceuta.getText().isEmpty()) {
                    // Construir ID automáticamente
                    txtIDFarmaceuta.setText("far-" + (farmaceutaLogica.generarNextID()));
                }
                if (currentMode.equals("buscar")) buscarFarmaceuta();
            });

        } catch (Exception e) {
            mostrarAlerta("Error al inicializar", e.getMessage());
        }
    }

    public void cargarFarmaceuticos() {
        if (farmaceutaLogica != null) {
            listaObservable.setAll(farmaceutaLogica.findAll());
        }
    }

    private void handleIDChange(String newVal) {
        if (!newVal.startsWith("far-")) {
            txtIDFarmaceuta.setText("far-" + newVal.replace("far-", ""));
        }

        if (currentMode.equals("modificar") || currentMode.equals("buscar") || currentMode.equals("borrar")) {
            Optional<Farmaceutico> encontrado = farmaceutaLogica.findByCodigo(txtIDFarmaceuta.getText());
            if (encontrado.isPresent()) {
                txtNombreFarmaceuta.setText(encontrado.get().getNombre());
                txtNombreFarmaceuta.setEditable(currentMode.equals("modificar"));
            } else {
                if (!currentMode.equals("buscar")) txtNombreFarmaceuta.clear();
            }
        }

        if (currentMode.equals("buscar")) buscarFarmaceuta();
    }

    private void toggleMode(String mode) {
        currentMode = mode;

        btnGuardarFarmaceutico.setSelected(mode.equals("guardar"));
        btnBorrarFarmaceutico.setSelected(mode.equals("borrar"));
        btnModificarFarmaceutico.setSelected(mode.equals("modificar"));
        btnBuscarFarmaceutico.setSelected(mode.equals("buscar"));

        txtIDFarmaceuta.setEditable(true);
        txtNombreFarmaceuta.clear();
        txtIDFarmaceuta.setText("far-");

        switch (mode) {
            case "guardar":
                txtNombreFarmaceuta.setEditable(true);
                break;
            case "borrar":
            case "buscar":
                txtNombreFarmaceuta.setEditable(false);
                break;
            case "modificar":
                txtNombreFarmaceuta.setEditable(false); // se desbloquea al encontrar
                break;
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

            switch (currentMode) {
                case "guardar":
                    farmaceutaLogica.create(m);
                    listaObservable.add(m);
                    break;
                case "modificar":
                    farmaceutaLogica.update(m);
                    refrescarTabla();
                    break;
                case "borrar":
                    boolean eliminado = farmaceutaLogica.deleteByCodigo(id);
                    if (eliminado) listaObservable.removeIf(x -> x.getId().equalsIgnoreCase(id));
                    else mostrarAlerta("No encontrado", "No existe un farmaceutico con ese ID: " + id);
                    break;
            }

            limpiarCampos();

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

        List<Farmaceutico> resultado = farmaceutaLogica.search(id, nombre);
        listaObservable.setAll(resultado);
    }

    @FXML
    private void limpiarCampos() {
        txtIDFarmaceuta.setText("far-");
        txtNombreFarmaceuta.clear();
    }

    public void refrescarTabla() {
        listaObservable.setAll(farmaceutaLogica.findAll());
    }

    public void mostrarListaConAnimacion() {
        if (vBoxPortadaFarmaceuta.isVisible()) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                TranslateTransition transition = new TranslateTransition(Duration.seconds(1.5), vBoxPortadaFarmaceuta);
                transition.setToX(-vBoxPortadaFarmaceuta.getWidth() - 20);
                transition.setOnFinished(e -> vBoxPortadaFarmaceuta.setVisible(false));
                transition.play();
            });
            pause.play();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
