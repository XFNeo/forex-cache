package ru.xfneo.controller;

import io.vertx.ext.web.RoutingContext;
import org.jboss.logging.Logger;
import ru.xfneo.entity.ResponseData;
import ru.xfneo.service.ProxyService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static ru.xfneo.Constants.API_V1;

@Produces
@Consumes
@Path(API_V1 + "/{path:.*}")
public class ProxyController {

    private static final Logger LOG = Logger.getLogger(ProxyController.class);

    private final ProxyService proxyService;

    public ProxyController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @GET
    public Response proxyGet(RoutingContext rc) {
        try {
            final ResponseData responseData = proxyService.proxy(rc.request(), null);
            return Response.ok(responseData.responseBody(), responseData.contentType()).build();
        } catch (Exception e) {
            LOG.error("Error proxy GET request: {}", rc.request(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    public Response proxyPost(RoutingContext rc, String body) {
        try {
            final ResponseData responseData = proxyService.proxy(rc.request(), body);
            return Response.ok(responseData.responseBody(), responseData.contentType()).build();
        } catch (Exception e) {
            LOG.error("Error proxy POST request: {}", rc.request(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
