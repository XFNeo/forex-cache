package ru.xfneo.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.http.Fault.CONNECTION_RESET_BY_PEER;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class WiremockOriginServer implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;
    static final String TEST_ENDPOINT_OK = "/test_ok";
    static final String TEST_ENDPOINT_OK_NO_CONTENT = "/test_ok_no_content";
    static final String TEST_ENDPOINT_FAIL = "/test_fail";
    static final String TEST_ENDPOINT_CONNECTION_RESET_BY_PEER = "/test_reset";
    static final String TEST_REQUEST_BODY = "{\"Request\": \"Where\"}";
    static final String TEST_RESPONSE_BODY = "{\"Result\": \"OK\"}";
    static final String TEST_RESPONSE_CONTENT_TYPE = APPLICATION_JSON;
    static final String TEST_REQUEST_CONTENT_TYPE = APPLICATION_JSON;
    private static final int PORT = 8888;

    String authHeaderName = "apikey";
    String authHeaderValue = "change-me";

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
        configureFor(PORT);

        stubFor(
                get(urlEqualTo(TEST_ENDPOINT_OK_NO_CONTENT)).withHeader(authHeaderName, equalTo(authHeaderValue))
                        .willReturn(aResponse().withStatus(204)));

        stubFor(
                get(urlEqualTo(TEST_ENDPOINT_OK)).withHeader(authHeaderName, equalTo(authHeaderValue))
                        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", TEST_RESPONSE_CONTENT_TYPE).withBody(TEST_RESPONSE_BODY)));

        stubFor(
                post(urlEqualTo(TEST_ENDPOINT_OK))
                        .withHeader(authHeaderName, equalTo(authHeaderValue))
                        .withHeader("Content-Type", containing(TEST_REQUEST_CONTENT_TYPE))
                        .withRequestBody(equalTo(TEST_REQUEST_BODY))
                        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", TEST_RESPONSE_CONTENT_TYPE).withBody(TEST_RESPONSE_BODY)));

        stubFor(
                get(urlEqualTo(TEST_ENDPOINT_FAIL))
                        .willReturn(aResponse().withStatus(500)));

        stubFor(
                get(urlEqualTo(TEST_ENDPOINT_CONNECTION_RESET_BY_PEER))
                        .willReturn(aResponse().withFault(CONNECTION_RESET_BY_PEER)));

        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
