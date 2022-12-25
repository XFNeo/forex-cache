package ru.xfneo.filter;


import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Priority(Priorities.USER + 5)
@Provider
public class SecurityFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(SecurityFilter.class);
    @ConfigProperty(name = "security.apikey.header.name")
    String securityApiKeyHeaderName;
    @ConfigProperty(name = "security.apikey.header.value")
    String securityApiKeyHeaderValue;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final String apiKey = requestContext.getHeaderString(securityApiKeyHeaderName);
        if (apiKey == null || !apiKey.equals(securityApiKeyHeaderValue)) {
            LOG.warnf("API key is not valid, apikey = %s", apiKey);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
