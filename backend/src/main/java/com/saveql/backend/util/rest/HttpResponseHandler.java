package com.saveql.backend.util.rest;

import com.saveql.backend.util.rest.exception.HttpResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.ZonedDateTime;

@Provider
public class HttpResponseHandler implements ExceptionMapper<HttpResponse> {

    @Override
    public Response toResponse(HttpResponse response) {
        HttpResponsePayload payload = new HttpResponsePayload(
                response.getId(),
                response.getStatus(),
                response.getData(),
                ZonedDateTime.now()
        );

        return Response.status(response.getStatus()).entity(payload).build();
    }
}
