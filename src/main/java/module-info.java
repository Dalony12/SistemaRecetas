module com.example.sistemarecetas {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sistemarecetas to javafx.fxml;
    exports com.example.sistemarecetas;
}