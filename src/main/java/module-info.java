module com.example.sistemarecetas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.sistemarecetas to javafx.fxml;
    exports com.example.sistemarecetas;
}