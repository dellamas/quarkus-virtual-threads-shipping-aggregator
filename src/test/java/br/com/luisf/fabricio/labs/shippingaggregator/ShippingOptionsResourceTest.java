package br.com.luisf.fabricio.demos.shippingaggregator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class ShippingOptionsResourceTest {
    @Test
    @Order(1)
    void shouldReturnThreePartnersOrderedByLowestPrice() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "origin", "sao-paulo-sp",
                        "destination", "belo-horizonte-mg",
                        "sku", "SKU-9001",
                        "quantity", 3,
                        "orderValue", new BigDecimal("799.90")))
                .when()
                .post("/api/shipping-options/quotes")
                .then()
                .statusCode(200)
                .body("options", hasSize(3))
                .body("options[0].partner", equalTo("ECONOSHIP"))
                .body("options[1].partner", equalTo("PICKNPACK"))
                .body("options[2].partner", equalTo("FASTBOX"))
                .body("options[0].price", lessThanOrEqualTo(40.50f))
                .body("options[0].latencyMs", greaterThanOrEqualTo(200))
                .body("totalAggregationTimeMs", greaterThanOrEqualTo(200));
    }

    @Test
    @Order(2)
    void shouldExposeSupportedPartners() {
        given()
                .when()
                .get("/api/shipping-options/partners")
                .then()
                .statusCode(200)
                .body("partners", hasItems("FASTBOX", "ECONOSHIP", "PICKNPACK"));
    }

    @Test
    @Order(3)
    void shouldExposeDiagnosticsAfterQuoteExecution() {
        given()
                .when()
                .get("/api/shipping-options/diagnostics")
                .then()
                .statusCode(200)
                .body("totalQuotes", equalTo(1))
                .body("partners", hasSize(3))
                .body("partners.partner", hasItems("FASTBOX", "ECONOSHIP", "PICKNPACK"))
                .body("partners[0].calls", equalTo(1))
                .body("partners[1].calls", equalTo(1))
                .body("partners[2].calls", equalTo(1));
    }
    @Test
    @Order(4)
    void shouldRejectInvalidQuotePayload() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "origin", "sao-paulo-sp",
                        "destination", "belo-horizonte-mg",
                        "sku", "SKU-9001",
                        "quantity", 0,
                        "orderValue", BigDecimal.ZERO))
                .when()
                .post("/api/shipping-options/quotes")
                .then()
                .statusCode(400);
    }

}
