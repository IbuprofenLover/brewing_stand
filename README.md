# Brewing Stand
## CRUD API for coffee ranking
##### Author : 
- Tiago Ferreira [@tfHEIG](https://github.com/tfHEIG)
- Mirco Profico ,[@mircoprofico](https://github.com/mircoprofico) 
- Pierre Gellet [@IbuprofenLover](https://github.com/IbuprofenLover)
### Quick installation
the first step is to clone the repository, either via https or ssh :
###### HTTPS clone
```shell
git clone https://github.com/IbuprofenLover/brewing_stand.git
```

###### SSH clone
```shell
git clone git@github.com:IbuprofenLover/brewing_stand.git
```

### Build the application
To build the application, simply run :
```shell
  mvn clean package
```
This generates the runnable JAR:
target/brewing_stand-1.0-SNAPSHOT.jar

### Run the application
If you've followed all the previous step, you can then start the application using :
```shell
java -jar target/brewing_stand-1.0-SNAPSHOT.jar
```

### Usage
Once the application is running, you'll be able to access it using the HTTP/HTTPS protocol. The application is composed
of two endpoints, coffee and reviews. By default, the application doesn't contain any value, meaning you'll have to
populate it (using POST requests). The simplest way to use it is by using cURL (more information about it [here](https://github.com/curl/curl)).

Each endpoint possess the 4 requests type, namely POST, PUT, GET and DELETE. A complete documentation of the API can be 
found [here](docs/API.md).

Considering you're running on your local machine, you can start by running
```shell
curl -i -X POST http://localhost:8080/coffee -H "Content-Type: application/json"   -d '{
    "name": "Espresso",
    "origin": "Italy",
    "intensity": 8,
    "aroma": "Chocolate",
    "type": "Hot"
  }'
  ```
If you correctly did all the previous steps, this should print a HTTP response looking like this
```blame
HTTP/1.1 201 Created
Date: Tue, 20 Jan 2026 19:36:42 GMT
Content-Type: application/json
Content-Length: 83

{"name":"Espresso","origin":"Italy","intensity":8,"aroma":"Chocolate","type":"Hot"}p
```
You are now able to create a review using a similar request, but with a slightly different json and endpoint :

```shell
curl -i -X POST http://localhost:8080/reviews -H "Content-Type: application/json"   -d '{
    "coffeeName": "Espresso",
    "rating": 5,
    "comment": "Strong and delicious!"
  }'
  ```
note : As mentioned inside the API declaration, a review can only be created if it reviews an existing coffee.
The response to this shall be :
```blame
HTTP/1.1 201 Created
Date: Tue, 20 Jan 2026 20:09:34 GMT
Content-Type: application/json
Content-Length: 79

{"id":"1","coffeeName":"Espresso","rating":5,"comment":"Strong and delicious!"}
```
### Caching strategy
There are two caching strategies, depending on which endpoint you do a request from :
1. For the coffee endpoint, the caching works on a validation model, where the precise time when a value is modified last
is stored. When a get request is made on a specific coffee, if the user adds to its request the header something like
```shell
  "If-Modified-Since: 2026-01-20T14:32:10.123"
```
Then the server will first check if the data requested has changed since the given time. If not, it will return an empty
body response with response code 304 (Not modified).

2. For the reviews endpoint, the code implements ETag-based caching for GET requests. Each response includes an ETag 
built from the global dataVersion and a scope-specific hash (e.g., review ID or query filter). When a client sends an 
If-None-Match header with the cached ETag, the server compares it to the current ETag; if they match, it returns 304 
Not Modified, avoiding sending the full data. Any mutation (POST, PUT, DELETE) increments dataVersion, automatically 
invalidating all cached ETags.


## Deployment on the Virtual Machine

The application can be deployed on a VM to be accessible via a domain name with HTTPS.

### Connecting to the Virtual Machine

To manage or deploy the API, you need to connect to the VM using SSH:

```bash
ssh ubuntu@<VM_IP_or_domain>
```

Replace `<VM_IP_or_domain>` with the public IP or domain name of your VM. Make sure your SSH key is configured or use the password provided for the `ubuntu` user.

Once connected, you can pull the latest API image from the GitHub Container Registry and start it with Docker Compose.

```bash
docker login ghcr.io
docker compose pull           # retrieves the latest image from GitHub Container Registry
```


### Docker Compose with Traefik

A `docker-compose.yaml` file is used to:

* Deploy the API in a Docker container.
* Connect the API to Traefik, which acts as a **reverse proxy**.
* Allow Traefik to automatically manage **HTTP/HTTPS routing** and TLS certificates via **Let's Encrypt**.
* Ensure both Traefik and the API are **running on the VM and accessible via the domain**.

In this project, the Compose file is located in the `api` folder. Start the services with:

```bash
docker compose up -d
```

Once running:

* The API is accessible at `https://heig.freeddns.org`.
* Traefik handles HTTPS certificates automatically.
* The Traefik dashboard is publicly accessible by default (it can be secured if needed).

---

**Note:** The API is accessible via `https://heig.freeddns.org` **only if Traefik is running** on the VM.
If Traefik has been stopped, restart it using:

```bash
cd ~/project/traefik
docker compose up -d
```

Traefik handles HTTPS, domain routing, and the API reverse proxy, so the API will not be reachable until Traefik is active.


### Testing the API with `curl`

Example request to create a coffee entry:

```bash
curl -i -X POST https://heig.freeddns.org/coffee \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Espresso",
    "origin": "Italy",
    "intensity": 8,
    "aroma": "Chocolate",
    "type": "Hot"
  }'
```

Expected response:

```json
{"name":"Espresso","origin":"Italy","intensity":8,"aroma":"Chocolate","type":"Hot"}
```

You can use similar requests for reviews and other CRUD operations.

---

### Automatic HTTPS

Traefik automatically generates TLS certificates using **Let's Encrypt**, ensuring secure access to the API.


