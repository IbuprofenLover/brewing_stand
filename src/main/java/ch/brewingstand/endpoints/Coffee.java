package ch.brewingstand.endpoints;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;

public class Coffee {
    private static int currid = 0;
    private final int id;
    private final String name;
    private final String origin;
    private final int intensity;
    private final int bitterness;

    private static HashMap<Integer, Coffee> coffees = new HashMap<Integer, Coffee>();

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

    public static void getManyCoffees(Context ctx) {
        String nm = ctx.queryParam("name");
        String orgn = ctx.queryParam("origin");
        String bitter = ctx.queryParam("bitterness");
        String intense = ctx.queryParam("intensity");
        ArrayList<Coffee> returnArray = new ArrayList<Coffee>();
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
    public String getOrigin() {return origin;}
    public int getIntensity() {return intensity;}
    public int getBitterness() {return bitterness;}

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
