package ru.xfneo.service;

import io.vertx.core.http.HttpServerRequest;
import ru.xfneo.client.RemoteServerRestClient;
import ru.xfneo.entity.RequestData;
import ru.xfneo.entity.ResponseData;
import ru.xfneo.repo.CacheRepo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProxyServiceImpl implements ProxyService {

    @Inject
    RemoteServerRestClient client;

    @Inject
    CacheRepo<ResponseData> repo;

    @Override
    public ResponseData proxy(HttpServerRequest request, String body) {
        var requestData = new RequestData(
                request.uri(),
                request.method().toString(),
                request.getHeader("Content-Type"),
                body);

        return repo.withCache(requestData.getDigest(), () -> client.proxyRequest(requestData));
    }


}
