package ru.xfneo.controller;

import org.jboss.resteasy.reactive.RestResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static ru.xfneo.Constants.HEALTH;

@Produces
@Path(HEALTH)
public class HealthController {

    @GET
    public RestResponse<String> health() {
        return RestResponse.ok();
    }
}
