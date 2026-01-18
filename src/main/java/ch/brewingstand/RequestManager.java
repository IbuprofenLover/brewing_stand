package ch.brewingstand;

import ch.brewingstand.endpoints.CoffeeClasses.CoffeeController;
import io.javalin.Javalin;

public abstract class RequestManager {

    public static void delegate(Javalin app) {
        coffee_delegate(app);
        review_delegate(app);
    }

    private static void coffee_delegate(Javalin app) {
        app.get("/coffee", CoffeeController::getManyCoffees);
        app.get("/coffee/{id}", CoffeeController::getCoffeeById);

        app.delete("/coffee/{id}", CoffeeController::deleteCoffee);

        app.post("/coffee", CoffeeController::postCoffee);

        app.put("/coffee/{id}", CoffeeController::putCoffee);
    }




    // todo redefine the gets so that they actually call functions from review
    private static void review_delegate(Javalin app) {
        app.get("/review", ctx -> ctx.result("List of reviews"));
        app.get("/review/{id}", ctx -> ctx.result("getting reviews with id " +  ctx.pathParam("id")));
    }

}
