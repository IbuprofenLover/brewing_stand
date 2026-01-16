package ch.brewingstand.endpoints;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Coffee {
    private static int currid = 0;
    private final int id;
    private String name;
    private int intensity;
    private String aroma;
    private String origin;
    private String type;
    private static final ConcurrentHashMap<Integer, Coffee> coffees = new ConcurrentHashMap<Integer, Coffee>();

    public Coffee(String name, String origin, int intensity, String aroma, String type) {
        this.name = name;
        this.intensity = Math.clamp(intensity, 1, 10);
        this.origin = origin;
        this.aroma = aroma;
        this.type = type;
        id = currid++;
        coffees.put(id, this);
    }
    /**
     * The function that handle the GET requests, for coffees, using path parameters. it can respond one coffeex
     *
     * @param ctx the context of the request
     */
    public static void getCoffeeById(Context ctx) {
        int idToRetrieve = ctx.pathParamAsClass("id", Integer.class).get();
        Coffee coffee = coffees.get(idToRetrieve);
        if (coffee == null) {
            ctx.status(404);
            ctx.result("coffee with id "+idToRetrieve +" do not exists.");
            return;
        }
        ctx.json(coffee);
    }

    /**
     * The function that handle the GET requests, for coffees, using query parameters. it can respond from none to all
     * the coffees
     *
     * @param ctx the context of the request
     */
    public static void getManyCoffees(Context ctx) {
        String nm = ctx.queryParam("name");
        String orgn = ctx.queryParam("origin");
        String intense = ctx.queryParam("intensity");
        String get_aroma = ctx.queryParam("aroma");
        String get_type = ctx.queryParam("type");
        List<Coffee> returnArray = new ArrayList<Coffee>();
        if(intense != null && isInvalidNumeric(intense, 1, 10)) {
            ctx.result("Error : Intensity in [1:10]");
            return;
        }

        for(Coffee coffee : coffees.values()) {
            if(nm != null && !coffee.getName().equals(nm)) continue;
            if(orgn != null && !coffee.origin.equals(orgn)) continue;
            if(get_aroma != null && !coffee.aroma.equals(get_aroma)) continue;
            if(intense != null && Integer.parseInt(intense) != coffee.intensity) continue;
            if(get_type != null && !coffee.type.equals(get_type)) continue;

            returnArray.add(coffee);
        }
        ctx.status(200);
        ctx.json(returnArray);
    }
    /**
     * The function that handle the POST requests for a coffee, given its attributes. The context should include at least
     * 3 query parameters : a name, an origin and an intensity. In addition, it can also contain an aroma and a type.
     * If a coffee already exists with that name, the coffee won't be able to be created.
     *
     * @param ctx the context of the request
     */
    public static void postCoffee(Context ctx) {
        String nm = ctx.queryParam("name");
        String orgn = ctx.queryParam("origin");
        String intense = ctx.queryParam("intensity");
        String post_aroma = ctx.queryParam("aroma");
        String post_type = ctx.queryParam("type");

        if(nm == null || orgn == null || intense == null) {

            ctx.status(400);
            ctx.result("Invalid request body : a coffee must at least contain a name, an origin and an intensity");
            return;
        }
        if(isInvalidNumeric(intense, 1, 10)){
            ctx.status(400);
            ctx.result("Error : Intensity should be between 1 and 10");
            return;
        }

        post_aroma = post_aroma==null?"":post_aroma;
        post_type = post_type==null?"":post_type;

        for(Coffee coffee : coffees.values()) {
            if(coffee.getName().equals(nm)){
                ctx.status(409);
                ctx.result("Coffee with name "+nm+" already exists.");
                return;
            }
        }
        Coffee c = new Coffee(nm, orgn, Integer.parseInt(intense),  post_aroma, post_type);
        coffees.put(c.id, c);
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
            ctx.result("Not found");
            return;
        }
        coffees.remove(coffee.id);
        ctx.status(204);
    }

    public static void putCoffee(Context ctx) {
        int idToUpdate = ctx.pathParamAsClass("id", Integer.class).get();
        Coffee coffee = coffees.get(idToUpdate);
        if(coffee == null) {
            ctx.status(404);
            ctx.result("coffee with id "+idToUpdate +" do not exists.");
            return;
        }
        String nm = ctx.queryParam("name");
        String orgn = ctx.queryParam("origin");
        String intense = ctx.queryParam("intensity");
        String post_aroma = ctx.queryParam("aroma");
        String post_type = ctx.queryParam("type");

        if(nm == null && orgn == null && intense == null && post_aroma == null && post_type == null) {
            ctx.status(400);
            ctx.result("You should provide at least one attribute to change in [name, origin, intensity, aroma, type]");
            return;
        }
        if(intense != null && isInvalidNumeric(intense, 1, 10)) {
            ctx.status(400);
            ctx.result("Error : Intensity should be between 1 and 10");
            return;
        }
        coffee.origin = (orgn == null)?coffee.origin : orgn;
        coffee.aroma = (post_aroma == null)?coffee.aroma : post_aroma;
        coffee.type = (post_type == null)?coffee.type : post_type;
        coffee.intensity = (intense == null)?coffee.intensity : Integer.parseInt(intense);
        coffee.name = (nm==null)?coffee.name : nm;


        ctx.json(coffee);
        ctx.status(200);
    }

    /***
     * For JSON parsing
     */
    public String getName() {return name;}
    public int getId() {return id;}
    public String getOrigin() {return origin;}
    public int getIntensity() {return intensity;}
    public String getAroma() {return aroma;}
    public String getType() {return type;}

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
