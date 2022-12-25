package ru.xfneo.controller;

import io.vertx.ext.web.RoutingContext;
import org.jboss.resteasy.reactive.RestResponse;
import ru.xfneo.entity.ResponseData;
import ru.xfneo.service.ProxyService;

import javax.inject.Inject;
import javax.ws.rs.*;

@Produces
@Consumes
@Path("/{path:.*}")
public class ProxyController {

    @Inject
    ProxyService proxyService;

    @GET
    public RestResponse<String> proxyGet(RoutingContext rc) {
        try {
            final ResponseData responseData = proxyService.proxy(rc.request(), null);
            return RestResponse.ResponseBuilder.ok(responseData.responseBody(), responseData.contentType()).build();
        } catch (Exception e) {
            return RestResponse.ResponseBuilder.<String>create(RestResponse.StatusCode.INTERNAL_SERVER_ERROR).entity(e.getCause().getMessage()).build();
        }
    }

    @POST
    public RestResponse<String> proxyPost(RoutingContext rc, String body) {
        try {
            final ResponseData responseData = proxyService.proxy(rc.request(), body);
            return RestResponse.ResponseBuilder.ok(responseData.responseBody(), responseData.contentType()).build();
        } catch (Exception e) {
            return RestResponse.ResponseBuilder.<String>create(RestResponse.StatusCode.INTERNAL_SERVER_ERROR).entity(e.getCause().getMessage()).build();
        }
    }
}