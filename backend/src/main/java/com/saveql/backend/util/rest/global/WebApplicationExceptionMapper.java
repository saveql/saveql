package com.saveql.backend.util.rest.global;

import com.saveql.backend.util.rest.HttpResponsePayload;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.ZonedDateTime;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response originalResponse = exception.getResponse();
        int statusCode = originalResponse.getStatus();

        String errorId = switch (statusCode) {
            case 404 -> "ROUTE_NOT_FOUND";
            case 405 -> "METHOD_NOT_ALLOWED";
            case 415 -> "UNSUPPORTED_MEDIA_TYPE";
            default -> "HTTP_ERROR_" + statusCode;
        };

        HttpResponsePayload payload = new HttpResponsePayload(
                errorId,
                statusCode,
                null,
                ZonedDateTime.now()
        );

        return Response.status(statusCode)
                .entity(payload)
                .build();
    }
}
