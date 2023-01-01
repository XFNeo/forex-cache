package ru.xfneo.controller;

import io.vertx.ext.web.RoutingContext;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import ru.xfneo.entity.ResponseData;
import ru.xfneo.filter.LoggingFilter;
import ru.xfneo.service.ProxyService;

import javax.inject.Inject;
import javax.ws.rs.*;

import static ru.xfneo.Constants.API_V1;

@Produces
@Consumes
@Path(API_V1 + "/{path:.*}")
public class ProxyController {

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class);

    @Inject
    ProxyService proxyService;

    @GET
    public RestResponse<String> proxyGet(RoutingContext rc) {
        try {
            final ResponseData responseData = proxyService.proxy(rc.request(), null);
            return RestResponse.ResponseBuilder.ok(responseData.responseBody(), responseData.contentType()).build();
        } catch (Exception e) {
            LOG.error(e);
            return RestResponse.ResponseBuilder.<String>create(RestResponse.StatusCode.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    public RestResponse<String> proxyPost(RoutingContext rc, String body) {
        try {
            final ResponseData responseData = proxyService.proxy(rc.request(), body);
            return RestResponse.ResponseBuilder.ok(responseData.responseBody(), responseData.contentType()).build();
        } catch (Exception e) {
            LOG.error(e);
            return RestResponse.ResponseBuilder.<String>create(RestResponse.StatusCode.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
