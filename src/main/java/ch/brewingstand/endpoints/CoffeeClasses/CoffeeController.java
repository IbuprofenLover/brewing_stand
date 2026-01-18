package ch.brewingstand.endpoints.CoffeeClasses;
import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CoffeeController {
    private static final ConcurrentHashMap<String, Coffee> coffees = new ConcurrentHashMap<String, Coffee>();
    public CoffeeController() {}
    /**
     * The function that handle the GET requests, for coffees, using path parameters. it can respond one coffee.
     *
     * @param ctx the context of the request
     */
    public static void getCoffeeById(Context ctx) {
        String idToRetrieve = ctx.pathParam("id");
        Coffee coffee = coffees.get(idToRetrieve);
        if (coffee == null) {
            ctx.status(404);
            ctx.result("coffee with name "+idToRetrieve +" do not exists.");
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
            if(orgn != null && !coffee.origin().equals(orgn)) continue;
            if(get_aroma != null && !coffee.aroma().equals(get_aroma)) continue;
            if(intense != null && Integer.parseInt(intense) != coffee.intensity()) continue;
            if(get_type != null && !coffee.type().equals(get_type)) continue;

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

        Coffee c = ctx.bodyValidator(Coffee.class)
                .check(obj -> obj.name() != null, "Missing name")
                .check(obj -> obj.origin() != null, "Missing origin")
                .check(obj -> obj.aroma() != null, "Missing aroma")
                .check(obj -> obj.type() != null, "Missing type")
                .check(obj -> obj.intensity() != 0, "Missing intensity")
                .get();
        for(Coffee coffee : coffees.values()) {
            if(c.name().equalsIgnoreCase(coffee.name())) {
                throw new ConflictResponse();
            }
        }

        c = new Coffee(c.name(), c.origin(), c.intensity(), c.aroma(), c.type());

        coffees.put(c.name(), c);
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
        Coffee coffee = coffees.get(ctx.pathParam("id"));
        if(coffee == null) {
            ctx.status(404);
            ctx.result("Not found");
            return;
        }
        coffees.remove(coffee.name());
        ctx.status(204);
    }

    /**
     * The function that handle the PUT function : the context should include a path parameter named id, which correspond
     * to the coffee we want to modify. The context should also contain the query parameters for each attribute of the
     * coffee object.
     * The request shall include at least one of these query parameters, but it can contain all of them
     * @param ctx the context of the request
     */
    public static void putCoffee(Context ctx) {
        String coffeeToUpdate = ctx.pathParam("id");
        Coffee coffee = coffees.get(coffeeToUpdate);
        if(coffee == null) {
            ctx.status(404);
            ctx.result("Error : coffee with name "+ coffeeToUpdate +" do not exists.");
            return;
        }
        String nm = ctx.queryParam("name");
        String orgn = ctx.queryParam("origin");
        String intense = ctx.queryParam("intensity");
        String post_aroma = ctx.queryParam("aroma");
        String post_type = ctx.queryParam("type");

        if(orgn == null && intense == null && post_aroma == null && post_type == null) {
            ctx.status(400);
            ctx.result("Error : At least one attribute to modify required from [origin, intensity, aroma, type]");
            return;
        }
        if(intense != null && isInvalidNumeric(intense, 1, 10)) {
            ctx.status(400);
            ctx.result("Error : Intensity should be between 1 and 10");
            return;
        }
        String newOrigin = (orgn == null)?coffee.origin() : orgn;
        String newAroma = (post_aroma == null)?coffee.aroma() : post_aroma;
        String newType = (post_type == null)?coffee.type() : post_type;
        int newIntensity = (intense == null)?coffee.intensity() : Integer.parseInt(intense);
        coffee = new Coffee(coffee.name(), newOrigin, newIntensity, newAroma, newType);
        coffees.put(coffee.name(), coffee);
        ctx.json(coffee);
        ctx.status(200);
    }

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

    /**
     * Checks whether a coffee exists in the current in-memory storage, using a case-insensitive match on the coffee name.
     *
     * @param coffeeName the coffee name to check
     * @return true if a coffee with the given name exists, false otherwise
     */
    public static boolean coffeeExistsByName(String coffeeName) {
        if (coffeeName == null) return false;

        // coffees is keyed by name, but we still ensure case-insensitive behavior.
        for (Coffee c : coffees.values()) {
            if (c.name() != null && c.name().equalsIgnoreCase(coffeeName)) {
                return true;
            }
        }
        return false;
    }


}
