package ch.brewingstand.endpoints.ReviewClasses;

/**
 * DTO used as the request body for creating a review (POST /reviews).
 *
 * Does not contain an id, because the server generates it.
 *
 * @param coffeeName Name of the coffee being reviewed.
 * @param rating     Rating value (1-5).
 * @param comment    Free text comment.
 */
public record ReviewCreateRequest(String coffeeName, int rating, String comment) {}
