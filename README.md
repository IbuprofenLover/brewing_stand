# Brewing Stand
## lite weight CRUD API for coffee ranking
##### Author : 
- Tiago Ferreira [@tfHEIG](https://github.com/tfHEIG)
- Profico Mirco ,[@mircoprofico](https://github.com/mircoprofico) 
- Gellet Pierre [@IbuprofenLover](https://github.com/IbuprofenLover)
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
You are now able to create a review using

