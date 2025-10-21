package com.example.sistemarecetas.controller;

import javafx.application.Platform;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Async {

    public static final ExecutorService EXECUTOR =
            new ThreadPoolExecutor(2, // Core
                    4, // Max hilos que se pueden ejecutarm depende de la capacidad de memoria) // Pool cant de espacios disponibles que se tienen para ejecutar algo // Máximo de hilos
                    60L, TimeUnit.SECONDS,   // Ociosidad - Lo que va a esperar el hilo antes de mostrar un error
                    new LinkedBlockingDeque<>(),
                    r -> {
                        Thread t = new Thread(r);
                        t.setName("exec-bg-" + t.getId());
                        t.setDaemon(true);
                        return t;
                    }
            );

    public Async() {

    }

    // Crear una tarea con resultados dentro del hilo principal de la interfaz gráfica

    public static <T> void run(Supplier<T> supplier, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        EXECUTOR.submit(() -> { // Confirmar que todo está listo
            try {
                T result = supplier.get();
                if(onSuccess != null){
                    Platform.runLater(() -> onSuccess.accept(result));
                }
            }catch(Throwable e){
                if(onError != null){
                    Platform.runLater(() -> onError.accept(e));
                }
            }

        });
    }

    // Esta es una versión del método donde no hay resultado
    // La acción se ejecutó pero no necesitamos una respuesta
    public static void runVoid(Runnable action, Runnable onSuccess, Consumer<Throwable> onError){
        EXECUTOR.submit(() ->
        {
            try{
                action.run();
                if(onSuccess != null){
                    Platform.runLater(onSuccess);
                }

            } catch(Throwable ex){
                if(onError != null){
                    Platform.runLater(() -> onError.accept(ex));
                }
            }
        });
    }

}
