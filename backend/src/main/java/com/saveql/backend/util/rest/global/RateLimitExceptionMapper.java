package com.saveql.backend.util.rest.global;

import com.saveql.backend.util.rest.HttpResponsePayload;
import io.smallrye.faulttolerance.api.RateLimitException;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.ZonedDateTime;

@Provider
public class RateLimitExceptionMapper implements ExceptionMapper<RateLimitException> {
    @Override
    public Response toResponse(RateLimitException exception) {
        HttpResponsePayload payload = new HttpResponsePayload(
                "RATE_LIMIT_EXCEEDED",
                Response.Status.TOO_MANY_REQUESTS.getStatusCode(),
                null,
                ZonedDateTime.now()
        );

        return Response.status(Response.Status.TOO_MANY_REQUESTS)
                .entity(payload)
                .build();
    }
}
