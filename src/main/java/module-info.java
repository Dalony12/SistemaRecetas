module com.example.sistemarecetas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires jakarta.xml.bind;

    opens com.example.sistemarecetas.Model to javafx.base, jakarta.xml.bind, org.glassfish.jaxb.runtime;
    opens com.example.sistemarecetas.datos.medicamentos to jakarta.xml.bind, org.glassfish.jaxb.runtime;
    opens com.example.sistemarecetas.datos.farmaceutas to jakarta.xml.bind, org.glassfish.jaxb.runtime;
    opens com.example.sistemarecetas.datos.pacientes to jakarta.xml.bind, org.glassfish.jaxb.runtime;
    opens com.example.sistemarecetas.datos.medicos to jakarta.xml.bind, org.glassfish.jaxb.runtime;
    opens com.example.sistemarecetas.datos.recetas to jakarta.xml.bind, org.glassfish.jaxb.runtime;
    opens com.example.sistemarecetas.datos.prescripciones to jakarta.xml.bind, org.glassfish.jaxb.runtime;

    opens com.example.sistemarecetas.controller.adminApplication to javafx.fxml;
    opens com.example.sistemarecetas.controller.FarmaceuticoApplication to javafx.fxml;
    opens com.example.sistemarecetas.controller.generalControllers to javafx.fxml;
    opens com.example.sistemarecetas.controller.MedicoApplication to javafx.fxml;
    opens com.example.sistemarecetas.controller to javafx.fxml;

    exports com.example.sistemarecetas.controller.adminApplication;
    exports com.example.sistemarecetas.controller.FarmaceuticoApplication;
    exports com.example.sistemarecetas.controller.generalControllers;
    exports com.example.sistemarecetas.controller.MedicoApplication;
    exports com.example.sistemarecetas.controller;
    exports com.example.sistemarecetas.datos;
    exports com.example.sistemarecetas;
}