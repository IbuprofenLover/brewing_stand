package ch.brewingstand.endpoints;

import io.javalin.http.Context;

public class Coffee {
    private static int currid = 0;
    private int id;
    private final String name;
    private final int intensity;
    private final String origin;
    private final int bitterness;

    public Coffee(String name, int intensity, String origin, int bitterness) {
        this.name = name;
        this.intensity = Math.clamp(intensity, 1, 10);
        this.origin = origin;
        this.bitterness = bitterness;
        id = currid++;
    }

    public static void get_coffee(Context ctx) {
        int idToRetrieve = ctx.pathParamAsClass("coffee_id", Integer.class).get();
            ctx.result("Tried to get coffee with id " + idToRetrieve);
    }

}
