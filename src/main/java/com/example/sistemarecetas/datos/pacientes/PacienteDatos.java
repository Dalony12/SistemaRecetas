package com.example.sistemarecetas.datos.pacientes;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Gestión del XML de pacientes con manejo robusto de errores y respaldo si el XML está corrupto.
 */
public class PacienteDatos {

    private final Path xmlPath;
    private final JAXBContext ctx;
    private PacienteConector cache;

    public PacienteDatos(String filePath) {
        try {
            this.xmlPath = Path.of(Objects.requireNonNull(filePath));
            // declarar las clases que JAXB va a usar
            this.ctx = JAXBContext.newInstance(PacienteConector.class, PacienteEntity.class);
        } catch (Exception e) {
            // preservar la causa original para depuración
            throw new RuntimeException("Error inicializando JAXBContext para PacienteDatos", e);
        }
    }

    /**
     * Carga (unmarshal) el XML. Usa cache si está disponible.
     * Si el XML existe pero está corrupto, crea una copia de respaldo y genera un XML vacío.
     */
    public synchronized PacienteConector load() {
        try {
            if (cache != null) return cache;

            // Si no existe el archivo, crear estructura vacía y guardarla
            if (!Files.exists(xmlPath)) {
                cache = new PacienteConector();
                save(cache);
                return cache;
            }

            Unmarshaller u = ctx.createUnmarshaller();
            try {
                cache = (PacienteConector) u.unmarshal(xmlPath.toFile());
            } catch (Exception unmarshalEx) {
                // Si falla el unmarshal, respaldar el archivo corrupto y regenerar uno vacío
                System.err.println("WARNING: fallo al unmarshalling '" + xmlPath + "': " + unmarshalEx.getMessage());
                unmarshalEx.printStackTrace();

                // crear respaldo
                try {
                    Path backup = xmlPath.resolveSibling(xmlPath.getFileName().toString()
                            + ".corrupt." + LocalDateTime.now().toString().replace(':', '-'));
                    Files.move(xmlPath, backup);
                    System.err.println("Se movió archivo corrupto a: " + backup);
                } catch (Exception moveEx) {
                    System.err.println("No se pudo crear respaldo del archivo corrupto: " + moveEx.getMessage());
                    moveEx.printStackTrace();
                }

                // regenerar archivo vacío
                cache = new PacienteConector();
                save(cache);
                return cache;
            }

            if (cache.getPacientes() == null) {
                cache.setPacientes(new java.util.ArrayList<>());
            }
            return cache;

        } catch (RuntimeException re) {
            // rethrow con causa preservada
            throw re;
        } catch (Exception e) {
            throw new RuntimeException("Error cargando datos de pacientes desde " + xmlPath, e);
        }
    }

    /**
     * Guarda (marshal) el PacienteConector en disco.
     */
    public synchronized void save(PacienteConector data) {
        try {
            // asegurar directorios padre
            Path parent = xmlPath.getParent();
            if (parent != null) Files.createDirectories(parent);

            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            // escribir en archivo
            File output = xmlPath.toFile();
            try (StringWriter sw = new StringWriter()) {
                m.marshal(data, sw);      // opcional: generar string (útil para debug)
                m.marshal(data, output);  // escritura final
            }

            // actualizar cache
            this.cache = data;
        } catch (Exception e) {
            // imprimir traza completa para depuración
            e.printStackTrace();
            throw new RuntimeException("Error guardando datos de pacientes en " + xmlPath, e);
        }
    }

    public Path getXmlPath() { return xmlPath; }

    /** Invalida el cache para forzar recarga desde disco */
    public synchronized void invalidateCache() {
        this.cache = null;
    }
}
