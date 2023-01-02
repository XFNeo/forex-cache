package ru.xfneo.filter;


import io.vertx.core.http.HttpServerRequest;
import org.jboss.logging.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.util.Optional;

@Priority(Priorities.USER + 1)
@Provider
public class LoggingFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class);

    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    @Override
    public void filter(ContainerRequestContext context) {
        final String method = context.getMethod();
        final String url = info.getRequestUri().toString();
        final String address = request.remoteAddress().toString();
        final Optional<String> optionalXRealIP = Optional.ofNullable(request.headers().get("X-Real-IP"));
        final Optional<String> optionalXFF = Optional.ofNullable(request.headers().get("X-Forwarded-For"));
        LOG.infof("Request %s %s from IP:%s, real IP:%s", method, url, address, optionalXRealIP.or(() -> optionalXFF).orElse("none"));
    }
}
