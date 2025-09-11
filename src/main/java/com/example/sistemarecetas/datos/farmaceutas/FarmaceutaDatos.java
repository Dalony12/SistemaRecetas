package com.example.sistemarecetas.datos.farmaceutas;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

//Esta clase gestiona la base de datos (el archivo XML) para la entidad cliente
public class FarmaceutaDatos {
    private final Path xmlPath;
    private final JAXBContext ctx;
    private FarmaceutaConector farmaceutaConnector;
    private FarmaceutaConector cache;

    public FarmaceutaDatos(String filePath) {
        try{
            this.xmlPath = Path.of(Objects.requireNonNull(filePath));
            this.ctx = JAXBContext.newInstance(FarmaceutaConector.class, FarmaceutaEntity.class);
        }
        catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public synchronized FarmaceutaConector load() {
        try {
            if(cache != null) return cache;
            if(!Files.exists(xmlPath))
            {
                cache = new FarmaceutaConector();
                save(cache);
                return cache;
            }
            // Convierte XML a java
            Unmarshaller u = ctx.createUnmarshaller();
            // Gestion la información convertida al archivo XML
            cache = (FarmaceutaConector) u.unmarshal(xmlPath.toFile());

            if(cache.getFarmaceuticos() == null)
            {
                //Aqui creamos una primera instancia de clientes dentro del archivo
                //Esto se haría la primera vez que se corre el sistema
                //O cuando se limpia la información de BD
                cache.setFarmaceuticos(new java.util.ArrayList<>());
            }
            return cache;
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public synchronized void save(FarmaceutaConector data) {
        try{
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            File output = xmlPath.toFile();
            File parent = output.getParentFile();
            if(parent != null) parent.mkdirs();

            java.io.StringWriter sw = new java.io.StringWriter();
            m.marshal(data, sw);
            m.marshal(data, output);

            cache = data;
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public Path getXmlPath() { return xmlPath; }
}