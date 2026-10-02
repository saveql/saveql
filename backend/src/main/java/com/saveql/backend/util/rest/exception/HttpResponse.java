package com.saveql.backend.util.rest.exception;

import com.saveql.backend.util.rest.HttpResponsePayload;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class HttpResponse extends RuntimeException {

    private final String id;
    private final int status;
    private final Object data;
    private final ZonedDateTime timestamp;

    public HttpResponse(Status status, String id, Object data) {
        super(id);
        this.id = id;
        this.status = status.getStatusCode();
        this.data = data;
        this.timestamp = ZonedDateTime.now();
    }

    public HttpResponse(Status status, String id) {
        super(id);
        this.id = id;
        this.status = status.getStatusCode();
        this.data = null;
        this.timestamp = ZonedDateTime.now();
    }

    public static Response send(Status status, String id, Object data) {
        HttpResponsePayload payload = new HttpResponsePayload(
                id,
                status.getStatusCode(),
                data,
                ZonedDateTime.now()
        );

        return Response.status(status).entity(payload).build();
    }

    public static Response send(Status status, String id) {
        HttpResponsePayload payload = new HttpResponsePayload(
                id,
                status.getStatusCode(),
                null,
                ZonedDateTime.now()
        );

        return Response.status(status).entity(payload).build();
    }
}
