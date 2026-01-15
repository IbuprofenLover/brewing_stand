package ch.brewingstand.endpoints;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Coffee {
    private static int currid = 0;
    private final int id;
    private final String name;
    private final String origin;
    private final int intensity;
    private final int bitterness;

    private static final ConcurrentHashMap<Integer, Coffee> coffees = new ConcurrentHashMap<Integer, Coffee>();

    public Coffee(String name, String origin, int intensity, int bitterness) {
        this.name = name;
        this.intensity = Math.clamp(intensity, 1, 10);
        this.origin = origin;
        this.bitterness = bitterness;
        id = currid++;
        coffees.put(id, this);
    }

    public static void getCoffeeById(Context ctx) {
        int idToRetrieve = ctx.pathParamAsClass("id", Integer.class).get();
        Coffee coffee = coffees.get(idToRetrieve);
        if (coffee == null) {
            ctx.result("coffee with id "+idToRetrieve +" do not exists.");
            return;
        }
        ctx.json(coffee);
    }

    /**
     * The function that handle the GET requests, for coffees, using query parameters. it can respond from 0 to all
     * the coffees
     *
     * @param ctx the context of the request
     */
    public static void getManyCoffees(Context ctx) {
        String nm = ctx.queryParam("name");
        String orgn = ctx.queryParam("origin");
        String bitter = ctx.queryParam("bitterness");
        String intense = ctx.queryParam("intensity");
        List<Coffee> returnArray = new ArrayList<Coffee>();
        if(bitter != null && isInvalidNumeric(bitter, 1, 10)) {
            ctx.result("Error : Bitterness in [1:10]");
            return;
        }
        if(intense != null && isInvalidNumeric(intense, 1, 10)) {
            ctx.result("Error : Intensity in [1:10]");
            return;
        }

        for(Coffee coffee : coffees.values()) {
            if(nm != null && !coffee.getName().equals(nm)) continue;
            if(orgn != null && !coffee.origin.equals(orgn)) continue;
            if(intense != null && Integer.parseInt(intense) != coffee.intensity) continue;
            if(bitter != null && Integer.parseInt(bitter) != coffee.bitterness) continue;

            returnArray.add(coffee);
        }

        ctx.json(returnArray);
    }
    /**
     * The function that handle the POST requests for a coffee, given its attributes. The context should include a 4
     * query parameters : a name, an origin, a bitterness and an intensity. If a coffee already exists with that name,
     * the coffee won't be able to be created
     *
     * @param ctx the context of the request
     */
    public static void postCoffee(Context ctx) {

        String nm = ctx.queryParam("name");
        String orgn = ctx.queryParam("origin");
        String bitter = ctx.queryParam("bitterness");
        String intense = ctx.queryParam("intensity");


        if(nm == null || orgn == null || bitter == null || intense == null) {
            ctx.status(400);
            ctx.result("Invalid request body");
            return;
        }
        if(isInvalidNumeric(bitter, 1, 10) || isInvalidNumeric(intense, 1, 10)){
            ctx.result("Error : Bitterness and  Intensity in should be between 1 and 10");
            return;
        }

        for(Coffee coffee : coffees.values()) {
            if(coffee.getName().equals(nm)){
                ctx.status(409);
                ctx.result("Coffee with name "+nm+" already exists.");
                return;
            }
        }
        Coffee c = new Coffee(nm, orgn, Integer.parseInt(intense), Integer.parseInt(bitter));
        ctx.status(201);
        ctx.json(c);
    }

    /**
     * The function that handle the DELETE requests on a specific coffee. The context should include
     * a path parameter named id, which will correspond to the id of the coffee we want to delete.
     *
     * @param ctx the context of the request
     */
    public static void deleteCoffee(Context ctx) {
        Coffee coffee = coffees.get(Integer.parseInt(ctx.pathParam("id")));
        if(coffee == null) {
            ctx.status(404);
            return;
        }
        coffees.remove(coffee.id);
        ctx.status(204);
    }

    public static void putCoffee(Context ctx) {

        ctx.result("PUT for Coffee not yet implemented");
    }

    /***
     * For JSON parsing
     */
    public String getName() {return name;}
    public int getId() {return id;}
    public String getOrigin() {return origin;}
    public int getIntensity() {return intensity;}
    public int getBitterness() {return bitterness;}

    /**
     * Takes a string and check if it represents an integer value, and if this value is between the min
     * and max.
     * @param str the string we want to verify
     * @param min the minimal value allowed
     * @param max the maximal value allowed
     * @return true if str represents an integer and is between min and max (both included)
     */
    private static boolean isInvalidNumeric(String str, int min, int max) {
        if (str == null) return true;
        try {
            int i = Integer.parseInt(str);
            return i < min || i > max;
        }  catch (NumberFormatException e) {
            return true;
        }
    }

}
