package ru.xfneo.client;

import okhttp3.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import ru.xfneo.entity.RequestData;
import ru.xfneo.entity.ResponseData;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;


@ApplicationScoped
public class RemoteServerRestClient {

    private static final Logger LOG = Logger.getLogger(RemoteServerRestClient.class);
    @ConfigProperty(name = "origin.server.host")
    String remoteServerHost;
    @ConfigProperty(name = "origin.server.auth.header.name")
    String authHeaderName;
    @ConfigProperty(name = "origin.server.auth.header.value")
    String authHeaderValue;
    @ConfigProperty(name = "cache.validation.period.minutes")
    int validationPeriodMinutes;

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
        try {
            Response response = client.newCall(request).execute();
            LOG.infof("Received response: %s", response);
            if (response.isSuccessful()) {
                try (ResponseBody responseBody = response.body()) {
                    final String stringBody = responseBody.string();
                    LOG.infof("Response body: %s", stringBody);
                    final String responseContentType = Optional.ofNullable(responseBody.contentType()).map(MediaType::toString).orElse("*/*");
                    return new ResponseData(requestData, getExpirationDate(), stringBody, responseContentType);
                }
            } else {
                LOG.errorf("Received unexpected response code %d from origin server", response.code());
                throw new IOException(String.format("Received unexpected response code %d from origin server", response.code()));
            }
        } catch (IOException e) {
            LOG.errorf("Request to original server failed, err: %s ", e);
            throw e;
        }
    }

    private LocalDateTime getExpirationDate() {
        return LocalDateTime.now().plus(validationPeriodMinutes, ChronoUnit.MINUTES);
    }
}
