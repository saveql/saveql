package com.saveql.backend.util.rest.global;

import com.saveql.backend.util.rest.HttpResponsePayload;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.ZonedDateTime;

@Provider
public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException> {

    @Override
    public Response toResponse(NullPointerException exception) {
        HttpResponsePayload payload = new HttpResponsePayload(
                "MISSING_BODY",
                Response.Status.BAD_REQUEST.getStatusCode(),
                null,
                ZonedDateTime.now()
        );

        return Response.status(Response.Status.BAD_REQUEST).entity(payload).build();
    }
}
