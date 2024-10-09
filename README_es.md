# Prices API

Este proyecto es una API de precios desarrollada en Spring Boot 3, diseñada para gestionar y proporcionar información sobre precios de productos de manera eficiente.

## Tabla de Contenidos

- [Características](#características)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Arquitectura](#arquitectura)
- [Revisión del Código](#revisión-del-código)
- [Instalación](#instalación)
- [Ejecución](#ejecución)
- [Endpoints](#endpoints)
- [Ejemplo de Solicitud](#ejemplo-de-solicitud)
- [Futuras Mejoras](#futuras-mejoras)
- [Licencia](#licencia)
- [Otra Información](#otra-información)

## Características

- API RESTful para consultar precios de productos.
- Permite consultar precios según identificador de cadena, identificador de producto y fecha.
- Siguiendo principios SOLID y Clean Code.
- Base de datos en memoria H2 con creación y poblado automatizados. Cuenta con campos de auditoría.
- Observabilidad: logging con trazabilidad mediante un identificador de solicitud (request ID).
- Control de errores y validación de parámetros de entrada en la solicitud.
- Inspección de código automatizada a través de SonarQube (contenedor Docker) y pruebas.
- Documentación de la API con OpenAPI.

## Tecnologías Utilizadas

- **Spring Boot 3**
- **Java 17**
- **Maven**
- **Spring Boot Web** para implementar MVC.
- **JPA** para la gestión de bases de datos.
- **H2** para persistencia en memoria.
- **Docker** para la contenedorización de SonarQube (opcional).
- **SonarQube** para el análisis estático del repositorio y conexión con el proyecto mediante sonarsource-scanner.
- **OpenAPI** para documentación.
- **Otras:** DevTools, Lombok, Jakarta Validation (validación de parámetros), Starter-Test, Rest-Assured, Logback y SLF4J.

## Arquitectura

Se trata de una arquitectura Modelo Vista Controlador. Para la separación de capas se emplea el principio de inversión de control mediante el uso intensivo de la inyección de dependencias. La estructura del proyecto está alineada con esta arquitectura.

Las consultas a la base de datos se realizan a través del patrón Repository y Specification, permitiendo así componer consultas de forma semántica. Esta elección favorece la limpieza, claridad, modularidad y posibilidad de reutilización futura. Como alternativas, podrían emplearse consultas personalizadas de JPA (más eficientes pero menos claras) o mediante Criteria de JPA.

Dentro de los modelos, se utiliza Lombok para simplificar el código y JPA para establecer las relaciones con la base de datos.

## Revisión del Código

### Peticiones

A continuación, se muestra una descripción general de cómo se comunican y procesan las peticiones a través de la aplicación:

1. La petición `GET` llega al controlador `PriceController`, donde se validan los campos a través del objeto `priceRequest`.

```java
public ResponseEntity<PriceResponse> getProductPriceOfBrandOnDated(@Valid @ModelAttribute PriceRequest priceRequest) { ... }
```

2- A continuación, se utiliza el servicio priceService para obtener los resultados que se devolverán en el objeto priceResponse. Si no hay resultados, se devolverá un código 404.

```java
 PriceResponse response = this.priceService.getProductPriceOfBrandOnDate(
                        PriceDTO.from(priceRequest).build() // Pojo
                )
                .map(p -> PriceResponse.from(p).build()) // mapping to PriceBuilder
                .orElse(null);
```

Dentro de priceService, se utiliza el patrón Specification para componer la consulta, paginando y ordenando la respuesta para tomar solo el primer elemento con mayor prioridad. PriceSpecifications contiene los métodos que aplican los filtros necesarios.

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


### Otras Partes

- **HandlerExceptionController**: Se encarga de capturar las excepciones que se puedan producir con el fin de registrarlas en el log y ocultarlas para que no sean devueltas en “bruto” como respuesta. La respuesta de error se modela en `AppError`.

- **RequestIdFilter**: Este filtro genera y añade un identificador de solicitud, permitiendo que cada solicitud sea identificada a lo largo de su ciclo de vida. Es especialmente útil en situaciones de concurrencia de solicitudes.

- **Logging**: La configuración se establece en `logback-spring.xml`, utilizando los niveles de log por defecto, y la salida está formateada para ser enviada tanto a la salida estándar como a un archivo.

- **Modelos**: El dominio se modela con JPA, pero la siembra y el esquema de la base de datos se manejan a través de scripts SQL (`schema.sql`, `data.sql`). Se añaden campos de auditoría SQL (ver modelo `Audit`).Por simplificación el formato de tiempo usado es ISO-8601, no se está utilizando un estándar de tiempo como UTC. Como formato de referencia para la moneda, se emplea ISO 4217. Es posible acceder a la base de datos durante la ejecución a través de la ui-web `http://localhost:8080/h2-console/`.

- **DTOs**: Siguiendo el principio de responsabilidad única, se separan los distintos DTOs según su función.

- **Configuraciones**: Se establecen una configuración por defecto y otra para pruebas (`application.yml` y `application-test.yml`).

- **Testing**: Las pruebas se realizan añadiendo un perfil de prueba y con las tecnologías indicadas. Se cubren con pruebas unitarias las partes críticas de la aplicación, que incluyen `PriceController`, `PriceSpecifications` y `PriceServiceImpl`. En la prueba de integración (PricesApplicationTests) se cubren los casos indicados en el enunciado, además de un caso adicional fuera del rango de fechas.


## Instalación


1. Instala Java 17 y Maven (mvn) si no los tienes instalados en tu sistema.
2. Clona el repositorio:
   ```bash
   git clone https://github.com/tu_usuario/tu_repositorio.git
   cd tu_repositorio
   ```
3. Revisa que la configuración sea correcta para ejecutarse en tu equipo: application.yml, application-test.yml, sonar-project.properties (opcional). Por ejemplo, verifica los puertos utilizados. 
4. Ejecuta el siguiente comando desde la raíz del proyecto para descargar todas las dependencias especificadas en el archivo pom.xml:
    ```bash
    mvn clean install
    ```

## Ejecución

Puedes ejecutar la aplicación mediante tres opciones:

- Ejecutar con Maven:
    ```bash
        mvn spring-boot:run
    ```

- Ejecutar el JAR (si lo has generado con mvn package, estará en target/*.jar):
 ```bash
     java -jar target/prices-0.0.1-SNAPSHOT.jar
 ```

- Cargar la aplicación en un IDE y ejecutar PricesApplication.

 Para lanzar las solicitudes contra la aplicación, carga el archivo de documentación `/prices/src/main/resources/openapi.json` en una herramienta como Postman y lanza las peticiones que quieras probar, o bien envía tus solicitudes desde una terminal, por ejemplo: 

```bash
    curl "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2020-06-14T10:00:00"
    curl "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2020-06-14T16:00:00"
    curl "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2020-06-14T21:00:00"
    curl "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2020-06-15T10:00:00"
    curl "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2020-06-16T21:00:00"
    curl "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2021-08-16T21:00:00"
```


### tests

 si solo quieres lanzar los test entonces puedes usar tu IDE o ejecutar:
 ```bash
     mvn test -Dspring.profiles.active=test
 ```

### sonarQube
 para usar sonarQube debes ejecutar el archivo docker-compose.yaml
 ```bash
     docker-compose up -d
 ```
despues accede a tu navegador web e ingresa en la ruta `http://localhost:9000/`. La contraseña y usuario por defecto es admin, admin. Seguido te pedirá cambiar la contraseña. 

Una vez dentro debes ir a la cuenta de usuario y generar un token de acceso. Dentro de tu cuenta en security/generate token, genera de tipo user. Copialo porque lo usaras posteriorme. 

despues pudes usar sonarqube desde la web para vincularlarlo con un repositorio y/o ejecutar el proyecto para realizar un análisis:

 ```bash
     mvn clean verify -X sonar:sonar -Dsonar.projectKey=prices_key -Dsonar.host.url=http://localhost:9000 -Dsonar.token=YOUR_TOKEN_HERE
 ```

## Endpoints
La documentación de la API se ha realizado con OpenAPI. El archivo se ubica en `/src/main/resources/openapi.json`. A continuación, se presenta una breve descripción de dicha documentación.
### Obtener Precio de Producto

- **URL**: `/api/prices`
- **Método**: `GET`
- **Descripción**: Obtiene el precio de un producto para una marca en una fecha y hora específica.

### Parámetros de Consulta

- **productId**:
   - **Tipo**: `integer`
   - **Requerido**: `true`
   - **Descripción**: ID del producto (ej. `35455`).

- **brandId**:
   - **Tipo**: `integer`
   - **Requerido**: `true`
   - **Descripción**: ID de la marca (ej. `1` para ZARA).

- **priceDate**:
   - **Tipo**: `string`
   - **Requerido**: `true`
   - **Descripción**: Fecha y hora para aplicar el precio en formato ISO-8601 (ej. `2020-06-14T16:00:00`).

### Respuestas

- **200 OK**: Precio encontrado.
   - **Ejemplo de Respuesta**:
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

- **404 Not Found**: No se encontró precio para la solicitud dada.

### Ejemplo de Solicitud

Aquí hay ejemplos de solicitudes que puedes hacer a la API:

1. **Consulta de Precio (Caso 1)**:
   ```bash
   curl -X GET "http://localhost:8080/api/prices?productId=35455&brandId=1&priceDate=2020-06-14T16:00:00"

## Futuras Mejoras

- Versionado de la API utilizando un interceptor y pasándolo como valor en la solicitud:
    ```
        @RequestHeader(value = "X-API-VERSION", required = false, defaultValue = "1") String apiVersion
    ```
- No ocultar las excepciones en las respuestas en entornos de desarrollo o en aquellos donde se requiera.
- Implementación de CI/CD.
- Definición de entornos y posibles configuraciones adicionales.
- Incorporación de UTC para la sincronización y coordinación de tiempo.
- Mejora de casos de prueba y cobertura de código.


## Licencia

Este proyecto está protegido por derechos de autor. Todos los derechos reservados. No se permite la copia, distribución o modificación de este proyecto, en su totalidad o en parte, sin el permiso expreso del autor.

© [2024] [Pablo Alonso López]. Todos los derechos reservados.

## Otra Información

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.4/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.3.4/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#web)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#using.devtools)
* [h2](http://www.h2database.com/html/quickstart.html)