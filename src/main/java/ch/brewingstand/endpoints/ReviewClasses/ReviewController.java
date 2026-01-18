package ch.brewingstand.endpoints.ReviewClasses;
import ch.brewingstand.endpoints.CoffeeClasses.CoffeeController;

import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Controller handling CRUD operations for Review resources.
 *
 * Thread-safety: storage is backed by a ConcurrentHashMap and IDs/versions are generated using AtomicLong,
 * which makes the data structure resilient to concurrent accesses.
 *
 * Caching: GET endpoints emit an ETag and support conditional requests with If-None-Match,
 * returning 304 when the data has not changed.</p>
 */
public class ReviewController {

    // In-memory concurrent storage of reviews, keyed by review id.
    private static final ConcurrentHashMap<String, Review> reviews = new ConcurrentHashMap<>();
    
    // Monotonic counter to generate unique string IDs.
    private static final AtomicLong idCounter = new AtomicLong(0);

    // Global "version" of the reviews dataset. Incremented on any change to invalidate caches (ETag changes).
    private static final AtomicLong dataVersion = new AtomicLong(0);

    // Utility class pattern: no instances.
    private ReviewController() {}

    /**
     * Handles GET /reviews/{id}.
     *
     * Status codes:
     * - 200 if found
     * - 404 if not found
     * - 304 if client cache is up-to-date (If-None-Match matches ETag)
     *
     * @param ctx Javalin request/response context
     */
    public static void getReviewById(Context ctx) {
        String id = ctx.pathParam("id");
        Review review = reviews.get(id);

        if (review == null) {
            ctx.status(404).result("Review not found");
            return;
        }

        // Build an ETag that changes whenever the dataset changes (or the requested scope changes).
        String etag = buildEtag("review:" + id);

        // If the client already has the latest representation, return 304.
        if (isNotModified(ctx, etag)) {
            ctx.status(304);
            return;
        }

        // Emit caching headers + response.
        setCacheHeaders(ctx, etag);
        ctx.status(200).json(review);
    }

    /**
     * Handles GET /reviews?coffeeName=...
     *
     * Optional query parameter:
     * - coffeeName: filters reviews by coffee name
     *
     * Status codes:
     * - 200 always (returns a JSON array, possibly empty)
     * - 304 if client cache is up-to-date (If-None-Match matches ETag)
     *
     * @param ctx Javalin request/response context
     */
    public static void getManyReviews(Context ctx) {
        String coffeeName = ctx.queryParam("coffeeName");

        // Scope the ETag to the filter, to avoid mixing cache entries.
        String etag = buildEtag("reviews?coffeeName=" + (coffeeName == null ? "" : coffeeName));

        if (isNotModified(ctx, etag)) {
            ctx.status(304);
            return;
        }

        // In-memory filtering.
        List<Review> result = new ArrayList<>();
        for (Review r : reviews.values()) {
            if (coffeeName != null && !r.coffeeName().equals(coffeeName)) continue;
            result.add(r);
        }

        setCacheHeaders(ctx, etag);
        ctx.status(200).json(result);
    }

    /**
     * Handles POST /reviews.
     *
     * Expected JSON body:
     * - coffeeName (required)
     * - rating (required, 1-5)
     * - comment (required)
     *
     * Conflict rule:
     * - returns 409 if an identical review already exists (same coffeeName, rating, comment)
     *
     * Status codes:
     * - 201 if created
     * - 400 if validation fails
     * - 409 if conflict
     *
     * @param ctx Javalin request/response context
     */
    public static void postReview(Context ctx) {
        
        // Validate request body fields.
        ReviewCreateRequest req = ctx.bodyValidator(ReviewCreateRequest.class)
                .check(obj -> obj.coffeeName() != null && !obj.coffeeName().isBlank(), "Missing coffeeName")
                .check(obj -> obj.comment() != null && !obj.comment().isBlank(), "Missing comment")
                .check(obj -> obj.rating() >= 1 && obj.rating() <= 5, "Rating must be between 1 and 5")
                .get();

        // A review can only be created for an existing coffee.
        if (!CoffeeController.coffeeExistsByName(req.coffeeName())) {
            ctx.status(400).result("Error: coffee does not exist");
            return;
        }

        // Detect duplicates according to the conflict policy.
        for (Review r : reviews.values()) {
            if (r.coffeeName().equalsIgnoreCase(req.coffeeName())
                    && r.rating() == req.rating()
                    && r.comment().equals(req.comment())) {
                throw new ConflictResponse("Review already exists");
            }
        }

        // Generate an id on the server side.
        String id = String.valueOf(idCounter.incrementAndGet());
        Review created = new Review(id, req.coffeeName(), req.rating(), req.comment());

        // Persist and invalidate caches.
        reviews.put(id, created);
        dataVersion.incrementAndGet();

        ctx.status(201).json(created);
    }

    /**
     * Handles PUT /reviews/{id}.
     *
     * Expected JSON body:
     * - rating (required, 1-5)
     * - comment (required)
     *
     * Immutable field:
     * - coffeeName is not updated (stays the same as the existing review)
     *
     * Status codes:
     * - 200 if updated
     * - 400 if validation fails
     * - 404 if not found
     *
     * @param ctx Javalin request/response context
     */
    public static void putReview(Context ctx) {
        String id = ctx.pathParam("id");
        Review existing = reviews.get(id);

        if (existing == null) {
            ctx.status(404).result("Review not found");
            return;
        }

        ReviewUpdateRequest req = ctx.bodyValidator(ReviewUpdateRequest.class)
                .check(obj -> obj.comment() != null && !obj.comment().isBlank(), "Missing comment")
                .check(obj -> obj.rating() >= 1 && obj.rating() <= 5, "Rating must be between 1 and 5")
                .get();

        // Only rating/comment are updated. coffeeName remains unchanged.
        Review updated = new Review(existing.id(), existing.coffeeName(), req.rating(), req.comment());

        // Persist and invalidate caches.
        reviews.put(id, updated);
        dataVersion.incrementAndGet();

        ctx.status(200).json(updated);
    }

    /**
     * Handles DELETE /reviews/{id}.
     *
     * Status codes:
     * - 204 if deleted
     * - 404 if not found
     *
     * @param ctx Javalin request/response context
     */
    public static void deleteReview(Context ctx) {
        String id = ctx.pathParam("id");
        Review removed = reviews.remove(id);

        // remove returns null if the key does not exist.
        if (removed == null) {
            ctx.status(404).result("Review not found");
            return;
        }

        // Any mutation invalidates cached GET responses.
        dataVersion.incrementAndGet();
        ctx.status(204);
    }

    /**
     * Builds an ETag string for the given scope.
     * The ETag changes whenever the global data version changes or the scope changes.
     *
     * @param scope String representing endpoint + filter context
     * @return quoted ETag value
     */
    private static String buildEtag(String scope) {
        // ETag simple: "v<version>-<scopeHash>"
        long v = dataVersion.get();
        int h = scope.hashCode();
        return "\"v" + v + "-" + h + "\"";
    }

    /**
     * Checks whether the client-provided If-None-Match header matches the current ETag.
     *
     * @param ctx Javalin request/response context
     * @param etag current ETag for this representation
     * @return true if the server should reply 304 Not Modified
     */
    private static boolean isNotModified(Context ctx, String etag) {
        String inm = ctx.header("If-None-Match");
        return inm != null && inm.equals(etag);
    }

    
    /**
     * Sets caching headers for conditional GET support.
     *
     * @param ctx Javalin request/response context
     * @param etag ETag for this representation
     */
    private static void setCacheHeaders(Context ctx, String etag) {
        ctx.header("ETag", etag);

        // Cacheable but must revalidate, so clients can use ETag validation.
        ctx.header("Cache-Control", "private, max-age=0, must-revalidate");
    }
}