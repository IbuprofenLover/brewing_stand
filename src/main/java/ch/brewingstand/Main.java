package ch.brewingstand;

import io.javalin.Javalin;

public class Main {
    public static final int PORT = 8080;

    public static void main(String[] args) {
        Javalin app = Javalin.create();
        RequestManager.delegate(app);

        app.start(PORT);
    }
}