package ru.xfneo.service;

import io.vertx.core.http.HttpServerRequest;
import ru.xfneo.entity.ResponseData;

public interface ProxyService {
    ResponseData proxy(HttpServerRequest request, String body);
}
