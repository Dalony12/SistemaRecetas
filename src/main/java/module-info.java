module com.example.sistemarecetas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;

    opens com.example.sistemarecetas to javafx.fxml;
    exports com.example.sistemarecetas;

    exports com.example.sistemarecetas.adminApplication;
    opens com.example.sistemarecetas.adminApplication to javafx.fxml;

    opens com.example.sistemarecetas.FarmaceuticoApplication to javafx.fxml;
    exports com.example.sistemarecetas.MedicoApplication;
    opens com.example.sistemarecetas.MedicoApplication to javafx.fxml;

    exports com.example.sistemarecetas.generalControllers;
    opens com.example.sistemarecetas.generalControllers to javafx.fxml;

    opens Model to javafx.base;
}
