package com.example.sistemarecetas.datos;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DB {
    private static HikariDataSource ds;

    static {
        try(InputStream in = DB.class.getClassLoader().getResourceAsStream("db.properties")){
            Properties p = new Properties();
            p.load(in);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(p.getProperty("db.url"));
            config.setUsername(p.getProperty("db.user"));
            config.setPassword(p.getProperty("db.password"));
            config.setMaximumPoolSize(Integer.parseInt(p.getProperty("db.pool.size", "10")));
            config.setPoolName("MyAppPool");

            //Configuración opcional pero recomnedada por el profe
            config.setMinimumIdle(2); //Tiempo de espera
            config.setConnectionTimeout(10000); //Anti Hackeo, lo saco despues de 10 min y le pide la contraseña para corroborar
            config.setIdleTimeout(60000);
            config.setMaximumPoolSize(180000); // le suma a cada conexion más minutos, al de arriba

            ds = new HikariDataSource(config);

        } catch (Exception e){
            throw new RuntimeException("No se pudo iniciar el pool de conexiones", e);
        }
    }

    private DB() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}