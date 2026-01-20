package ch.brewingstand.endpoints.ReviewClasses;

/**
 * DTO used as the request body for updating a review (PUT /reviews/{id}).
 *
 * Only rating and comment are updatable.
 *
 * @param rating  Updated rating value (1-5).
 * @param comment Updated free text comment.
 */
public record ReviewUpdateRequest(int rating, String comment) {}
