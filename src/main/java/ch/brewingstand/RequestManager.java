package ch.brewingstand;

import ch.brewingstand.endpoints.Coffee;
import io.javalin.Javalin;

public abstract class RequestManager {

    public static void delegate(Javalin app) {
        coffee_delegate(app);
        review_delegate(app);
        // todo remove this when coffee is finished
        createSimpleCoffeeList();
    }

    // todo remove this when coffee is finished
    private static void createSimpleCoffeeList(){
        String[] names = {"Espresso", "Colombian Roast", "French Roast", "Ethiopian Yirgacheffe",
                        "Sumatra Mandheling", "Kenyan AA", "House Blend",
                        "Guatemalan Huehuetenango", "Brazilian Santos", "Italian Dark Roast"};

        String[] origins = {"Italy", "Colombia", "France", "Ethiopia", "Indonesia",
                        "Kenya", "USA", "Guatemala", "Brazil", "Italy"};

        String[] aroma = {"coco and dark chocolate", "hints of wood", "fruity", "bitter"};
        String[] types = {"blend", "preparation", "aromatic coffee"};

        for (int i = 0; i < names.length; i++) {Coffee f = new Coffee(names[i], origins[i], i%10, aroma[i%4], types[i%3]);}
    }

    private static void coffee_delegate(Javalin app) {
        app.get("/coffee", Coffee::getManyCoffees);
        app.get("/coffee/{id}", Coffee::getCoffeeById);

        app.delete("/coffee/{id}", Coffee::deleteCoffee);

        app.post("/coffee", Coffee::postCoffee);

        app.put("/coffee/{id}", Coffee::putCoffee);
    }




    // todo redefine the gets so that they actually call functions from review
    private static void review_delegate(Javalin app) {
        app.get("/review", ctx -> ctx.result("List of reviews"));
        app.get("/review/{id}", ctx -> ctx.result("getting reviews with id " +  ctx.pathParam("id")));
    }

}
