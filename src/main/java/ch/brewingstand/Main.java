package ch.brewingstand;

import io.javalin.Javalin;

import java.time.LocalDateTime;

public class Main {
    public static final int PORT = 8080;

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            // This will allow us to parse LocalDateTime
            config.validation.register(LocalDateTime.class, LocalDateTime::parse);
        });
        RequestManager.delegate(app);

        app.start(PORT);
    }
}