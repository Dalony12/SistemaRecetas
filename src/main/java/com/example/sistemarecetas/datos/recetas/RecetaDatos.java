package com.example.sistemarecetas.datos.recetas;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

//Esta clase gestiona la base de datos (el archivo XML) para la entidad cliente
public class RecetaDatos {
    private final Path xmlPath;
    private final JAXBContext ctx;
    private RecetaConector recetaConnector;
    private RecetaConector cache;

    public RecetaDatos(Path filePath) {
        try{
            this.xmlPath = Path.of(Objects.requireNonNull(filePath.toString()));
            this.ctx = JAXBContext.newInstance(RecetaConector.class, RecetaEntity.class);
        }
        catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public synchronized RecetaConector load() {
        try {
            if(cache != null) return cache;
            if(!Files.exists(xmlPath))
            {
                cache = new RecetaConector();
                save(cache);
                return cache;
            }
            // Convierte XML a java
            Unmarshaller u = ctx.createUnmarshaller();
            // Gestion la información convertida al archivo XML
            cache = (RecetaConector) u.unmarshal(xmlPath.toFile());

            if(cache.getRecetas() == null)
            {
                //Aqui creamos una primera instancia de clientes dentro del archivo
                //Esto se haría la primera vez que se corre el sistema
                //O cuando se limpia la información de BD
                cache.setRecetas(new java.util.ArrayList<>());
            }
            return cache;
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public synchronized void save(RecetaConector data) {
        try{
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            File output = xmlPath.toFile();
            File parent = output.getParentFile();
            if(parent != null) parent.mkdirs();

            java.io.StringWriter sw = new java.io.StringWriter();
            m.marshal(data, sw); //Pasa los datos a escritura
            m.marshal(data, output); // Escribe los datos en el archivo

            cache = data;
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public Path getXmlPath() { return xmlPath; }
}
