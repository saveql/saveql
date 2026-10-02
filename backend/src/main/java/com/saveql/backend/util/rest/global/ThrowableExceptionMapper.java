package com.saveql.backend.util.rest.global;

import com.saveql.backend.util.rest.HttpResponsePayload;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.ZonedDateTime;

@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(ThrowableExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        LOG.error("An unexpected error occurred", exception);

        HttpResponsePayload payload = new HttpResponsePayload(
                "INTERNAL_SERVER_ERROR",
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                null,
                ZonedDateTime.now()
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(payload)
                .build();
    }
}