package ru.xfneo.client;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import ru.xfneo.entity.RequestData;
import ru.xfneo.entity.ResponseData;

import javax.inject.Inject;
import java.io.IOException;

import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.*;
import static ru.xfneo.client.WiremockOriginServer.*;
import static javax.ws.rs.core.MediaType.WILDCARD;

@QuarkusTest
@QuarkusTestResource(WiremockOriginServer.class)
class RemoteServerRestClientTest {

    @ConfigProperty(name = "cache.validation.period.minutes")
    int validationPeriodMinutes;

    @Inject
    RemoteServerRestClient remoteServerRestClient;

    @Test
    void proxyRequestSuccess_GetWithoutBody() throws IOException {
        var requestData = new RequestData(TEST_ENDPOINT_OK, GET.toString(), null, null);
        var expectedResponseData = new ResponseData(requestData, now(), TEST_RESPONSE_BODY, TEST_RESPONSE_CONTENT_TYPE);

        final ResponseData actualResponseData = remoteServerRestClient.proxyRequest(requestData);

        assertEquals(expectedResponseData.responseBody(), actualResponseData.responseBody());
        assertEquals(expectedResponseData.contentType(), actualResponseData.contentType());
        assertTrue(actualResponseData.expirationDate().isBefore(now().plus(validationPeriodMinutes+1, MINUTES)));
        assertTrue(actualResponseData.expirationDate().isAfter(now().plus(validationPeriodMinutes-1, MINUTES)));
        assertEquals(expectedResponseData.requestData(), actualResponseData.requestData());
    }

    @Test
    void proxyRequestSuccess_PostWithBody() throws IOException {
        var requestData = new RequestData(TEST_ENDPOINT_OK, POST.toString(), TEST_REQUEST_CONTENT_TYPE, TEST_REQUEST_BODY);
        var expectedResponseData = new ResponseData(requestData, now(), TEST_RESPONSE_BODY, TEST_RESPONSE_CONTENT_TYPE);

        final ResponseData actualResponseData = remoteServerRestClient.proxyRequest(requestData);

        assertEquals(expectedResponseData.responseBody(), actualResponseData.responseBody());
        assertEquals(expectedResponseData.contentType(), actualResponseData.contentType());
        assertTrue(actualResponseData.expirationDate().isBefore(now().plus(validationPeriodMinutes+1, MINUTES)));
        assertTrue(actualResponseData.expirationDate().isAfter(now().plus(validationPeriodMinutes-1, MINUTES)));
        assertEquals(expectedResponseData.requestData(), actualResponseData.requestData());
    }

    @Test
    void proxyRequestSuccess_GetWithoutBody_204() throws IOException {
        var requestData = new RequestData(TEST_ENDPOINT_OK_NO_CONTENT, GET.toString(), null, null);
        var expectedResponseData = new ResponseData(requestData, now(), null, null);

        final ResponseData actualResponseData = remoteServerRestClient.proxyRequest(requestData);

        assertTrue(actualResponseData.responseBody().isEmpty());
        assertEquals(WILDCARD, actualResponseData.contentType());
        assertTrue(actualResponseData.expirationDate().isBefore(now().plus(validationPeriodMinutes+1, MINUTES)));
        assertTrue(actualResponseData.expirationDate().isAfter(now().plus(validationPeriodMinutes-1, MINUTES)));
        assertEquals(expectedResponseData.requestData(), actualResponseData.requestData());
    }

    @Test
    void proxyRequestFail() {
        var requestData = new RequestData(TEST_ENDPOINT_FAIL, GET.toString(), null, null);
        assertThrows(IOException.class, () -> remoteServerRestClient.proxyRequest(requestData));

    }

    @Test
    void proxyRequestFailConnectionReset() {
        var requestData = new RequestData(TEST_ENDPOINT_CONNECTION_RESET_BY_PEER, GET.toString(), null, null);
        assertThrows(IOException.class, () -> remoteServerRestClient.proxyRequest(requestData));
    }
}