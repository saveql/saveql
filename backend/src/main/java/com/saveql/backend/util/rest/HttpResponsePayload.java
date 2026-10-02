package com.saveql.backend.util.rest;

import java.time.ZonedDateTime;

public record HttpResponsePayload(
        String id,
        Integer status,
        Object data,
        ZonedDateTime timestamp
) { }