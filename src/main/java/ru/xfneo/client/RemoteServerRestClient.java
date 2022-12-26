package ru.xfneo.client;

import okhttp3.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import ru.xfneo.entity.RequestData;
import ru.xfneo.entity.ResponseData;

import javax.inject.Singleton;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;


@Singleton
public class RemoteServerRestClient {

    private static final Logger LOG = Logger.getLogger(RemoteServerRestClient.class);
    @ConfigProperty(name = "origin.server.host")
    String remoteServerHost;
    @ConfigProperty(name = "origin.server.auth.header.name")
    String authHeaderName;
    @ConfigProperty(name = "origin.server.auth.header.value")
    String authHeaderValue;

    OkHttpClient client = new OkHttpClient().newBuilder().build();

    public ResponseData proxyRequest(RequestData requestData) throws IOException {
        RequestBody requestBody = null;
        if (requestData.requestBody() != null && !requestData.requestBody().isEmpty()) {
            requestBody = RequestBody.create(requestData.requestBody(), MediaType.get(requestData.contentType()));
        }
        Request request = new Request.Builder()
                .url(remoteServerHost + requestData.uri())
                .addHeader(authHeaderName, authHeaderValue)
                .method(requestData.method(), requestBody)
                .build();
        LOG.infof("Request: %s", request);
        Response response = client.newCall(request).execute();
        LOG.infof("Received response: %s", response);
        if (response.isSuccessful()) {
            try (ResponseBody responseBody = response.body()) {
                if (responseBody != null) {
                    final String stringBody = responseBody.string();
                    final String responseContentType = Optional.ofNullable(responseBody.contentType()).map(MediaType::toString).orElse("*/*");
                    return new ResponseData(requestData, LocalDateTime.now(), stringBody, responseContentType);
                } else {
                    return new ResponseData(requestData, LocalDateTime.now(), null, null);
                }
            }
        } else {
            LOG.errorf("Received unexpected response code %d from origin server", response.code());
            throw new IOException(String.format("Received unexpected response code %d from origin server", response.code()));
        }
    }
}
