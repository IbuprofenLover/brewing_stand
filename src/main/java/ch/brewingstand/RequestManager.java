package ch.brewingstand;

import ch.brewingstand.endpoints.CoffeeClasses.CoffeeController;
import ch.brewingstand.endpoints.ReviewClasses.ReviewController;
import io.javalin.Javalin;

public abstract class RequestManager {

    /**
     * Registers all API routes on the given Javalin app instance.
     *
     * @param app Javalin application
     */
    public static void delegate(Javalin app) {
        coffee_delegate(app);
        review_delegate(app);
    }
    
    /**
     * Registers Coffee CRUD endpoints.
     *
     * @param app Javalin application
     */
    private static void coffee_delegate(Javalin app) {
        app.get("/coffees", CoffeeController::getManyCoffees);
        app.get("/coffees/{id}", CoffeeController::getCoffeeById);

        app.post("/coffees", CoffeeController::postCoffee);
        app.put("/coffees/{id}", CoffeeController::putCoffee);
        app.delete("/coffees/{id}", CoffeeController::deleteCoffee);
    }

    /**
     * Registers Review CRUD endpoints.
     *
     * @param app Javalin application
     */
    private static void review_delegate(Javalin app) {
        app.get("/reviews", ReviewController::getManyReviews);
        app.get("/reviews/{id}", ReviewController::getReviewById);

        app.post("/reviews", ReviewController::postReview);
        app.put("/reviews/{id}", ReviewController::putReview);
        app.delete("/reviews/{id}", ReviewController::deleteReview);
    }
}
