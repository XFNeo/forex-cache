package ru.xfneo.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.Header;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import ru.xfneo.entity.ResponseData;
import ru.xfneo.service.ProxyService;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static java.time.LocalDateTime.now;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.xfneo.Constants.API_V1;

@QuarkusTest
class ProxyControllerTest {

    @ConfigProperty(name = "security.apikey.header.name")
    String securityApiKeyHeaderName;

    @ConfigProperty(name = "security.apikey.header.value")
    String securityApiKeyHeaderValue;

    private static final String BODY_OK = "OK";
    private static final String BODY_FAIL = "FAIL";

    @InjectMock
    ProxyService proxyServiceMock;

    @Test
    void proxyGet() {
        when(proxyServiceMock.proxy(any(), any()))
                .thenReturn(new ResponseData(null, now(), BODY_OK, MediaType.TEXT_PLAIN));
        given()
                .when().header(new Header(securityApiKeyHeaderName, securityApiKeyHeaderValue)).get(API_V1 + "/any")
                .then()
                .statusCode(200)
                .body(is(BODY_OK));
    }

    @Test
    void proxyPost() {
        when(proxyServiceMock.proxy(any(), any()))
                .thenReturn(new ResponseData(null, now(), BODY_OK, MediaType.TEXT_PLAIN));
        given()
                .when().header(new Header(securityApiKeyHeaderName, securityApiKeyHeaderValue)).post(API_V1 + "/any")
                .then()
                .statusCode(200)
                .body(is(BODY_OK));
    }

    @Test
    void proxyGetFail() {
        when(proxyServiceMock.proxy(any(), any()))
                .thenThrow(new RuntimeException(BODY_FAIL));
        given()
                .when().header(new Header(securityApiKeyHeaderName, securityApiKeyHeaderValue)).get(API_V1 + "/any")
                .then()
                .statusCode(500)
                .body(is(BODY_FAIL));
    }

    @Test
    void proxyPostFail() {
        when(proxyServiceMock.proxy(any(), any()))
                .thenThrow(new RuntimeException(BODY_FAIL));
        given()
                .when().header(new Header(securityApiKeyHeaderName, securityApiKeyHeaderValue)).post(API_V1 + "/any")
                .then()
                .statusCode(500)
                .body(is(BODY_FAIL));
    }

    @Test
    void authFailed() {
        given()
                .when().header(new Header(securityApiKeyHeaderName, "wrong-key")).post(API_V1 + "/any")
                .then()
                .statusCode(401);
    }
}