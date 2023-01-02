package ru.xfneo.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static ru.xfneo.Constants.HEALTH;

@Produces
@Path(HEALTH)
public class HealthController {

    @GET
    public Response health() {
        return Response.ok().build();
    }
}
