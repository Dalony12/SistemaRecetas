module com.example.sistemarecetas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;


    opens com.example.sistemarecetas to javafx.fxml;
    exports com.example.sistemarecetas;
    exports com.example.sistemarecetas.adminApplication;
    opens com.example.sistemarecetas.adminApplication to javafx.fxml;
}