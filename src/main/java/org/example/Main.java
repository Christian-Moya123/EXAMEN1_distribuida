package org.example;


import com.google.gson.Gson;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.enterprise.inject.spi.CDI;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.example.model.Book;
import org.example.service.IBookService;

public class Main {


    private static ContainerLifecycle cicloDeVida = null;
    static IBookService servicio;
    static Gson gson = new Gson();

    public static void main(String[] args) {
        cicloDeVida = WebBeansContext.currentInstance().getService(ContainerLifecycle.class);
        cicloDeVida.startApplication(null);

        servicio = CDI.current().select(IBookService.class).get();

        WebServer server = WebServer.builder()
                .port(8080)
                .routing(builder -> builder
                        .get("/book/{id}", Main::obtenerLibro)
                        .get("/book", Main::todosLibros)
                        .post("/book", Main::crearLibro)
                        .put("/book/{id}", Main::actualizarLibro)
                        .delete("/book/{id}", Main::eliminarLibro)
                )
                .build();


        server.start();

        shutdown();
    }

    public static void shutdown() {
        cicloDeVida.stopApplication(null);
    }

    static void todosLibros(ServerRequest req, ServerResponse res) {

        res.send(gson.toJson(servicio.findAll()));

    }

    static void obtenerLibro(ServerRequest req, ServerResponse res) {

            Long id = Long.valueOf(req.path().pathParameters().get("id"));
            Book libro = servicio.find(id);
            res.send(gson.toJson(libro));

    }

    static void crearLibro(ServerRequest req, ServerResponse res) {

            String libroJson = req.content().as(String.class);
            Book libro = gson.fromJson(libroJson, Book.class);
            servicio.create(libro);
            res.send(gson.toJson(libro));

    }

    static void eliminarLibro(ServerRequest req, ServerResponse res) {

            Long id = Long.valueOf(req.path().pathParameters().get("id"));
            servicio.delete(id);
            res.send("Eliminado");

    }

    static void actualizarLibro(ServerRequest req, ServerResponse res) {

        String libroJson = req.content().as(String.class);
        Book libro = gson.fromJson(libroJson, Book.class);
        libro.setId(Long.valueOf(req.path().pathParameters().get("id")));
        servicio.update(libro);
        res.send(gson.toJson(libro));

    }
}