package ch.brewingstand.endpoints;

import io.javalin.http.Context;

import java.util.HashMap;

public class Coffee {
    private static int currid = 0;
    private final int id;
    private final String name;
    private final int intensity;
    private final String origin;
    private final int bitterness;

    private static HashMap<Integer, Coffee> coffees = new HashMap<Integer, Coffee>();

    public Coffee(String name, int intensity, String origin, int bitterness) {
        this.name = name;
        this.intensity = Math.clamp(intensity, 1, 10);
        this.origin = origin;
        this.bitterness = bitterness;
        id = currid++;
        coffees.put(id, this);
    }

    public static void getCoffeeById(Context ctx) {
        int idToRetrieve = ctx.pathParamAsClass("id", Integer.class).get();
        ctx.result("coffee with id " + idToRetrieve);
    }

    public static void getManyCoffees(Context ctx) {

        ctx.result("All coffees");
    }

}
