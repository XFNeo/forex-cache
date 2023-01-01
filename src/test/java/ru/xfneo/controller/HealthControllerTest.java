package ru.xfneo.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static ru.xfneo.Constants.HEALTH;

@QuarkusTest
class HealthControllerTest {

    @Test
    void health() {
        given()
                .when().get(HEALTH)
                .then()
                .statusCode(200)
                .body(is(""));
    }
}