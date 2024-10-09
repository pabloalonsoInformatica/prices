# Prices API

This project is a price API developed in Spring Boot 3, designed to efficiently manage and provide product pricing information.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Architecture](#architecture)
- [Code Review](#code-review)
- [Installation](#installation)
- [Execution](#execution)
- [Endpoints](#endpoints)
- [Request Example](#request-example)
- [Future Improvements](#future-improvements)
- [License](#license)
- [Additional Information](#additional-information)


## Features

- RESTful API for querying product prices.
- Allows price queries based on chain identifier, product identifier, and date.
- Adheres to SOLID principles and Clean Code practices.
- In-memory H2 database with automated creation and population, including audit fields.
- Observability: logging with traceability through a request identifier (request ID).
- Error handling and validation of request input parameters.
- Automated code inspection via SonarQube (Docker container) and testing.
- API documentation with OpenAPI.


## Technologies Used

- **Spring Boot 3**
- **Java 17**
- **Maven**
- **Spring Boot Web** for implementing MVC.
- **JPA** for database management.
- **H2** for in-memory persistence.
- **Docker** for containerizing SonarQube (optional).
- **SonarQube** for static analysis of the repository and project connection via sonarsource-scanner.
- **OpenAPI** for documentation.
- **Others:** DevTools, Lombok, Jakarta Validation (parameter validation), Starter-Test, Rest-Assured, Logback, and SLF4J.


## Architecture

This is a Model-View-Controller (MVC) architecture. For layer separation, the principle of inversion of control is applied through extensive use of dependency injection. The project structure is aligned with this architecture.

Database queries are performed via the Repository and Specification patterns, allowing queries to be composed semantically. This choice promotes code cleanliness, clarity, modularity, and potential for future reuse. As alternatives, JPA custom queries (more efficient but less clear) or JPA Criteria could be used.

Within the models, Lombok is used to simplify code, and JPA is utilized to establish relationships with the database.



## Code Review

### Requests

Below is an overview of how requests are communicated and processed throughout the application:

1. The `GET` request arrives at the `PriceController`, where fields are validated through the `priceRequest` object.


```java
public ResponseEntity<PriceResponse> getProductPriceOfBrandOnDated(@Valid @ModelAttribute PriceRequest priceRequest) { ... }
```

2. Next, the `priceService` is used to obtain the results, which will be returned in the `priceResponse` object. If there are no results, a 404 code will be returned.

```java
 PriceResponse response = this.priceService.getProductPriceOfBrandOnDate(
                        PriceDTO.from(priceRequest).build() // Pojo
                )
                .map(p -> PriceResponse.from(p).build()) // mapping to PriceBuilder
                .orElse(null);
```

Within `priceService`, the Specification pattern is used to compose the query, paginating and ordering the response to take only the first element with the highest priority. `PriceSpecifications` contains the methods that apply the necessary filters.

```java
return priceRepository.findAll(
                Specification
                        .where(PriceSpecifications.brandIs(priceDTO.getBrandId()))
                        .and(PriceSpecifications.productIs(priceDTO.getProductId()))
                        .and(PriceSpecifications.onDate(priceDTO.getPriceDate())),
                PageRequest.of(
                        0,
                        1,
                        Sort.by(Sort.Direction.DESC, "priority")
                )
        ).stream().findFirst();
```


### Other Components

- **HandlerExceptionController**: Responsible for capturing exceptions that may occur, logging them, and concealing them from being returned in raw form. The error response is modeled in `AppError`.

- **RequestIdFilter**: This filter generates and adds a request identifier, allowing each request to be tracked throughout its lifecycle. It is especially useful in concurrent request scenarios.

- **Logging**: Configuration is set in `logback-spring.xml`, using default log levels, and output is formatted for both standard output and a log file.

- **Models**: The domain is modeled with JPA, but database seeding and schema are managed through SQL scripts (`schema.sql`, `data.sql`). SQL audit fields are added (see `Audit` model). For simplification, the time format used is ISO-8601, and a universal time standard like UTC is not applied. ISO 4217 is used as the currency reference format. It is possible to access the database during runtime through the web UI at `http://localhost:8080/h2-console/`.

- **DTOs**: Following the single responsibility principle, various DTOs are separated according to their function.

- **Configurations**: Default and test configurations are set (`application.yml` and `application-test.yml`).

- **Testing**: Tests are conducted by adding a test profile and using the indicated technologies. Critical parts of the application, including `PriceController`, `PriceSpecifications`, and `PriceServiceImpl`, are covered with unit tests. In the integration test (`PricesApplicationTests`), specified cases are covered as well as an additional case outside the date range.


## Installation

1. Install Java 17 and Maven (mvn) if they are not already installed on your system.
2. Clone the repository:

   ```bash
   git clone https://github.com/tu_usuario/tu_repositorio.git
   cd tu_repositorio
   ```
3. Ensure the configuration is correct for running on your machine: `application.yml`, `application-test.yml`, `sonar-project.properties` (optional). For instance, check the ports being used.
4. Run the following command from the project root to download all dependencies specified in the `pom.xml` file:
    ```bash
    mvn clean install
    ```

## Execution

You can run the application through three options:

- Run with Maven:
    ```bash
        mvn spring-boot:run
    ```

- Run the JAR (if generated with `mvn package`, it will be in `target/*.jar`):
 ```bash
     java -jar target/prices-0.0.1-SNAPSHOT.jar
 ```

- Load the application in an IDE and run `PricesApplication`.

To send requests to the application, load the documentation file `/prices/src/main/resources/openapi.json` into a tool like Postman and send the requests you want to test, or alternatively, send requests from a terminal, for example:

```bash
    curl "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2020-06-14T10:00:00"
    curl "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2020-06-14T16:00:00"
    curl "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2020-06-14T21:00:00"
    curl "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2020-06-15T10:00:00"
    curl "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2020-06-16T21:00:00"
    curl "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2021-08-16T21:00:00"
```


### tests

If you only want to run the tests, you can use your IDE or execute:
 ```bash
     mvn test -Dspring.profiles.active=test
 ```

### sonarQube
To use SonarQube, you need to run the `docker-compose.yaml` file.
 ```bash
     docker-compose up -d
 ```
Then, open your web browser and go to `http://localhost:9000/`. The default username and password are `admin`, `admin`. You will then be prompted to change the password.

Once inside, go to your user account and generate an access token. In your account under `Security/Generate Token`, generate a token of type `user`. Copy it, as you will need it later.

Afterward, you can use SonarQube from the web interface to link it to a repository and/or execute the project to perform an analysis:

 ```bash
     mvn clean verify -X sonar:sonar -Dsonar.projectKey=prices_key -Dsonar.host.url=http://localhost:9000 -Dsonar.token=YOUR_TOKEN_HERE
 ```

## Endpoints
The API documentation is created with OpenAPI. The file is located at `/src/main/resources/openapi.json`. Below is a brief description of this documentation.

### Get Product Price

- **URL**: `/api/prices`
- **Method**: `GET`
- **Description**: Retrieves the price of a product for a specific brand at a given date and time.

### Query Parameters

- **productId**:
    - **Type**: `integer`
    - **Required**: `true`
    - **Description**: Product ID (e.g., `35455`).

- **brandId**:
    - **Type**: `integer`
    - **Required**: `true`
    - **Description**: Brand ID (e.g., `1` for ZARA).

- **priceDate**:
    - **Type**: `string`
    - **Required**: `true`
    - **Description**: Date and time for applying the price in ISO-8601 format (e.g., `2020-06-14T16:00:00`).

### Responses

- **200 OK**: Price found.
    - **Example Response**:
      ```json
      {
          "priceList": 2,
          "productId": 35455,
          "brandId": 1,
          "startDate": "2020-06-14T15:00:00",
          "endDate": "2020-06-14T18:30:00",
          "value": 25.45
      }
      ```

- **404 Not Found**: No price found for the given request.

### Request Example

Here are examples of requests you can make to the API:

1. **Price Query (Case 1)**:
   ```bash
   curl -X GET "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2020-06-14T16:00:00"

## Future Improvements

- API versioning using an interceptor and passing it as a value in the request:
    ```
        @RequestHeader(value = "X-API-VERSION", required = false, defaultValue = "1") String apiVersion
    ```
- Do not conceal exceptions in responses in development environments or those requiring it.
- Implementation of CI/CD.
- Definition of environments and potential additional configurations.
- Incorporation of UTC for time synchronization and coordination.
- Enhancement of test cases and code coverage.

## License

This project is protected by copyright. All rights reserved. Copying, distribution, or modification of this project, in whole or in part, is not permitted without the explicit permission of the author.

© [2024] [Pablo Alonso López]. All rights reserved.

## Additional Information


* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.4/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.3.4/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#web)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#using.devtools)
* [h2](http://www.h2database.com/html/quickstart.html)