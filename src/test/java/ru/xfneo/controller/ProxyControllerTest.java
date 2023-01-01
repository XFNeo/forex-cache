package ru.xfneo.controller;

import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.xfneo.entity.ResponseData;
import ru.xfneo.service.ProxyService;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@QuarkusTest
class ProxyControllerTest {


    private static final String BODY = "OK";

//    @BeforeEach
    void setUp() {
        ProxyService mock = Mockito.mock(ProxyService.class);
//        Mockito.when(mock.findBy("Asimov"))
//                .thenReturn(Arrays.stream(new Book[] {
//                        new Book("Foundation", "Isaac Asimov"),
//                        new Book("I Robot", "Isaac Asimov")}));
        QuarkusMock.installMockForType(mock, ProxyService.class);
    }

//    @Test
    void proxyGet() {


//
//        RoutingContext routingContextMock = Mockito.mock(RoutingContext.class);
//        HttpServerRequest httpServerRequestMock = Mockito.mock(HttpServerRequest.class);
//        Mockito.when(routingContextMock.request())
//                .thenReturn(httpServerRequestMock);
//        final RestResponse<String> expectedRestResponse = RestResponse.ResponseBuilder.ok(BODY, MediaType.TEXT_PLAIN).build();
//        final RestResponse<String> actualRestResponse = proxyController.proxyGet(routingContextMock);
//        assertEquals(expectedRestResponse.toResponse(), actualRestResponse.toResponse());
    }

//    @Test
    void proxyPost() {
    }
}