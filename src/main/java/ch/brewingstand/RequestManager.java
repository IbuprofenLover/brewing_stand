package ch.brewingstand;

import ch.brewingstand.endpoints.Coffee;
import io.javalin.Javalin;

public abstract class RequestManager {

    public static void delegate(Javalin app) {
        coffee_delegate(app);
        review_delegate(app);
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
