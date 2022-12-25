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
        final String uri = request.uri();
        final String method = request.method().toString();
        final String contentType = request.getHeader("Content-Type");
        var requestData = new RequestData(uri, method, contentType, body);


        return repo.withCache(requestData.getDigest(), () -> client.proxyRequest(requestData));
    }


}
