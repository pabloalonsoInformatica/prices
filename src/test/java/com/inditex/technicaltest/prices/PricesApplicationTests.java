package com.inditex.technicaltest.prices;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //Spring context
@ActiveProfiles("test")
class PricesApplicationTests {

    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:" + port + "/api";
    }

    @Test
    public void testGetProductPriceForDate2020_06_14_10_WithSingleOccurrence() {
        final long productId = 35455;
        final long brandId = 1;

        given()
                .param("productId", productId)
                .param("brandId", brandId)
                .param("priceDate", "2020-06-14T10:00:00")
                .when()
                .get("/prices")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("priceList", equalTo(1))
                .body("productId", equalTo((int) productId))
                .body("brandId", equalTo((int) brandId))
                .body("startDate", equalTo("2020-06-14T00:00:00"))
                .body("endDate", equalTo("2020-12-31T23:59:59"))
                .body("value", equalTo(35.50f));
    }

    @Test
    public void testGetProductPriceForDate2020_06_14_16_ReturnsOnlyHighestPriority() {
        final long productId = 35455;
        final long brandId = 1;

        given()
                .param("productId", productId)
                .param("brandId", brandId)
                .param("priceDate", "2020-06-14T16:00:00")
                .when()
                .get("/prices")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("priceList", equalTo(2))
                .body("productId", equalTo((int) productId))
                .body("brandId", equalTo((int) brandId))
                .body("startDate", equalTo("2020-06-14T15:00:00"))
                .body("endDate", equalTo("2020-06-14T18:30:00"))
                .body("value", equalTo(25.45f));
    }

    @Test
    public void testGetProductPriceForDate2020_06_14_21_ReturnsOnlyHighestPriority() {
        final long productId = 35455;
        final long brandId = 1;

        given()
                .param("productId", productId)
                .param("brandId", brandId)
                .param("priceDate", "2020-06-14T21:00:00")
                .when()
                .get("/prices")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("priceList", equalTo(1))
                .body("productId", equalTo((int) productId))
                .body("brandId", equalTo((int) brandId))
                .body("startDate", equalTo("2020-06-14T00:00:00"))
                .body("endDate", equalTo("2020-12-31T23:59:59"))
                .body("value", equalTo(35.50f));
    }

    @Test
    public void testGetProductPriceForDate2020_06_15_10_ReturnsOnlyHighestPriority() {
        final long productId = 35455;
        final long brandId = 1;

        given()
                .param("productId", productId)
                .param("brandId", brandId)
                .param("priceDate", "2020-06-15T10:00:00")
                .when()
                .get("/prices")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("priceList", equalTo(3))
                .body("productId", equalTo((int) productId))
                .body("brandId", equalTo((int) brandId))
                .body("startDate", equalTo("2020-06-15T00:00:00"))
                .body("endDate", equalTo("2020-06-15T11:00:00"))
                .body("value", equalTo(30.50f));
    }


    @Test
    public void testGetProductPriceForDate2020_06_16_21_ReturnsOnlyHighestPriority() {
        final long productId = 35455;
        final long brandId = 1;

        given()
                .param("productId", productId)
                .param("brandId", brandId)
                .param("priceDate", "2020-06-16T21:00:00")
                .when()
                .get("/prices")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("priceList", equalTo(4))
                .body("productId", equalTo((int) productId))
                .body("brandId", equalTo((int) brandId))
                .body("startDate", equalTo("2020-06-15T16:00:00"))
                .body("endDate", equalTo("2020-12-31T23:59:59"))
                .body("value", equalTo(38.95f));
    }


    @Test
    public void testGetProductPriceForDateOutOfRange() {
        final long productId = 35455;
        final long brandId = 1;

        given()
                .param("productId", productId)
                .param("brandId", brandId)
                .param("priceDate", "2021-08-16T21:00:00")
                .when()
                .get("/prices")
                .then()
                .statusCode(404)
                .body(isEmptyOrNullString());
    }
}
