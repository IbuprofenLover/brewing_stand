# Coffee API

The Coffee API allows to manage coffees and reviews. It uses the HTTP protocol and the JSON format.

The API is based on the CRUD pattern. It has the following operations:

- Create a new coffee.
- Get many coffees that you can filter by intensity.
- Get one coffee by its ID.
- Update a coffee.
- Delete a coffee.
- Create a review.
- Get many reviews that you can filter by coffee name.
- Get one review by its ID.
- Update a review.
- Delete a review.

## Endpoints

### Create a new coffee

- `POST /coffees`

Create a new coffee.

#### Request

The request body must contain a JSON object with the following properties:

- `name` - The name of the coffee.
- `intensity` - The intensity of the coffee.
- `aromas` - An array of aromas of the coffee.
- `origin` - The origin of the coffee.
- `type` - The type of the coffee (Arabica, Robusta, Blend).

#### Response

The response body contains a JSON object with the following properties:

- `id` - The unique identifier of the coffee.
- `name` - The name of the coffee.
- `intensity` - The intensity of the coffee.
- `aromas` - The aromas of the coffee.
- `origin` - The origin of the coffee.
- `type` - The type of the coffee.

#### Status codes

- `201` (Created) - The coffee has been successfully created.
- `400` (Bad Request) - The request body is invalid.
- `409` (Conflict) - The coffee already exists.

---

### Get many coffees

- `GET /coffees`

Get many coffees.

#### Request

The request can contain the following query parameters:

- `intensity` - Filter coffees by intensity.

#### Response

The response body contains a JSON array with the following properties:

- `name` - The name of the coffee (used as unique identifier).
- `intensity` - The intensity of the coffee.
- `aromas` - The aromas of the coffee.
- `origin` - The origin of the coffee.
- `type` - The type of the coffee.

#### Status codes

- `200` (OK) - The coffees have been successfully retrieved.

---

### Get one coffee

- `GET /coffees/{name}`

Get one coffee by its name.

#### Request

The request path must contain the name of the coffee.

#### Response

The response body contains a JSON object with the following properties:

- `name` - The name of the coffee.
- `intensity` - The intensity of the coffee.
- `aromas` - The aromas of the coffee.
- `origin` - The origin of the coffee.
- `type` - The type of the coffee.

#### Status codes

- `200` (OK) - The coffee has been successfully retrieved.
- `404` (Not Found) - The coffee does not exist.

---

### Update a coffee

- `PUT /coffees/{name}`

Update a coffee by its name.

#### Request

The request path must contain the ID of the coffee.

The request body must contain a JSON object with the following properties:

- `name` - The name of the coffee.
- `intensity` - The intensity of the coffee.
- `aromas` - The aromas of the coffee.
- `origin` - The origin of the coffee.
- `type` - The type of the coffee.

#### Response

The response body contains a JSON object with the following properties:

- `name` - The name of the coffee.
- `intensity` - The intensity of the coffee.
- `aromas` - The aromas of the coffee.
- `origin` - The origin of the coffee.
- `type` - The type of the coffee.

#### Status codes

- `200` (OK) - The coffee has been successfully updated.
- `400` (Bad Request) - The request body is invalid.
- `404` (Not Found) - The coffee does not exist.

---

### Delete a coffee

- `DELETE /coffees/{name}`

Delete a coffee by its ID.

#### Request

The request path must contain the ID of the coffee.

#### Response

The response body is empty.

#### Status codes

- `204` (No Content) - The coffee has been successfully deleted.
- `404` (Not Found) - The coffee does not exist.

---

### Create a review

- `POST /reviews`

Create a new review.

#### Request

The request body must contain a JSON object with the following properties:

- `coffeeName` - The name of the coffee.
- `rating` - The rating of the coffee (1-5).
- `comment` - The comment about the coffee.

#### Response

The response body contains a JSON object with the following properties:

- `id` - The unique identifier of the review.
- `coffeeName` - The name of the coffee.
- `rating` - The rating of the coffee.
- `comment` - The comment about the coffee.

#### Status codes

- `201` (Created) - The review has been successfully created.
- `400` (Bad Request) - The request body is invalid. / The coffee does not exists
- `409` (Conflict) - The review already exists.

---

### Get many reviews

- `GET /reviews`

Get many reviews.

#### Request

The request can contain the following query parameters:

- `coffeeName` - Filter reviews by coffee name.

#### Response

The response body contains a JSON array with the following properties:

- `id` - The unique identifier of the review.
- `coffeeName` - The name of the coffee.
- `rating` - The rating of the coffee.
- `comment` - The comment about the coffee.

#### Status codes

- `200` (OK) - The reviews have been successfully retrieved.

---

### Get one review

- `GET /reviews/{id}`

Get one review by its ID.

#### Request

The request path must contain the ID of the review.

#### Response

The response body contains a JSON object with the following properties:

- `id` - The unique identifier of the review.
- `coffeeName` - The name of the coffee.
- `rating` - The rating of the coffee.
- `comment` - The comment about the coffee.

#### Status codes

- `200` (OK) - The review has been successfully retrieved.
- `404` (Not Found) - The review does not exist.

---

### Update a review

- `PUT /reviews/{id}`

Update a review by its ID.

#### Request

The request path must contain the ID of the review.

The request body must contain a JSON object with the following properties:

- `rating` - The rating of the coffee.
- `comment` - The comment about the coffee.

#### Response

The response body contains a JSON object with the following properties:

- `id` - The unique identifier of the review.
- `coffeeName` - The name of the coffee.
- `rating` - The rating of the coffee.
- `comment` - The comment about the coffee.

#### Status codes

- `200` (OK) - The review has been successfully updated.
- `400` (Bad Request) - The request body is invalid.
- `404` (Not Found) - The review does not exist.

---

### Delete a review

- `DELETE /reviews/{id}`

Delete a review by its ID.

#### Request

The request path must contain the ID of the review.

#### Response

The response body is empty.

#### Status codes

- `204` (No Content) - The review has been successfully deleted.
- `404` (Not Found) - The review does not exist.
