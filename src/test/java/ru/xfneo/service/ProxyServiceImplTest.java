package ru.xfneo.service;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import org.junit.jupiter.api.Test;
import ru.xfneo.client.RemoteServerRestClient;
import ru.xfneo.entity.RequestData;
import ru.xfneo.entity.ResponseData;
import ru.xfneo.repo.CacheRepo;
import ru.xfneo.repo.ResponseDataInMemoryRepo;

import java.io.IOException;

import static java.time.LocalDateTime.now;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static ru.xfneo.Constants.API_V1;

class ProxyServiceImplTest {

    private static final String TEST_ENDPOINT = "/test";

    @Test
    void proxy() throws IOException {
        final String emptyBody = "";

        final HttpServerRequest httpServerRequestMock = mock(HttpServerRequest.class);
        when(httpServerRequestMock.uri())
                .thenReturn(API_V1 + TEST_ENDPOINT);
        when(httpServerRequestMock.method())
                .thenReturn(HttpMethod.GET);
        when(httpServerRequestMock.getHeader("Content-Type"))
                .thenReturn(APPLICATION_JSON);

        var expectedRequestData = new RequestData(TEST_ENDPOINT, HttpMethod.GET.toString(), APPLICATION_JSON, emptyBody);
        var expectedResponseData = new ResponseData(expectedRequestData, now(), null, null);

        var spyRepo = spy(new ResponseDataInMemoryRepo());
        var remoteServerRestClientMock = mock(RemoteServerRestClient.class);
        when(remoteServerRestClientMock.proxyRequest(expectedRequestData))
                .thenReturn(expectedResponseData);

        ProxyService proxyService = new ProxyServiceImpl(remoteServerRestClientMock, spyRepo);
        var actualResponseData = proxyService.proxy(httpServerRequestMock, emptyBody);
        var actualRequestData = actualResponseData.requestData();

        assertEquals(TEST_ENDPOINT, actualRequestData.uri());
        assertEquals(APPLICATION_JSON, actualRequestData.contentType());
        assertEquals(HttpMethod.GET.toString(), actualRequestData.method());
        assertEquals(expectedResponseData, actualResponseData);
        verify(spyRepo).withCache(eq(expectedRequestData.getDigest()), any(CacheRepo.OmnivoreSupplier.class));
        verify(httpServerRequestMock).uri();
        verify(httpServerRequestMock).method();
        verify(httpServerRequestMock).getHeader("Content-Type");
    }
}