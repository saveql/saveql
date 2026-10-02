package com.saveql.backend.resource.mail;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.saveql.backend.dto.request.system.SmtpSendMailRequest;
import com.saveql.backend.entity.system.SmtpServer;
import com.saveql.backend.service.smtp.SmtpService;
import com.saveql.backend.util.rest.exception.HttpResponse;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/v1/system/smtp")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SmtpServerResource {

    @Inject
    SmtpService smtpService;

    @POST
    @Path("/send")
    @Blocking
    public Uni<Response> send(@Valid SmtpSendMailRequest request) {
        return this.smtpService.send(request)
                .replaceWith(() -> HttpResponse.send(Response.Status.OK, "SUCCESSFULLY"));
    }

    @GET
    public Response get() {
        return HttpResponse.send(Response.Status.OK, "SUCCESSFULLY", this.smtpService.get());
    }

    @POST
    public Response create(SmtpServer smtp) {
        return HttpResponse.send(Response.Status.CREATED, "SUCCESSFULLY", this.smtpService.create(smtp));
    }

    @PATCH
    public Response update(ObjectNode input) {
        return HttpResponse.send(Response.Status.OK, "SUCCESSFULLY", this.smtpService.update(input));
    }

    @DELETE
    public Response delete() {
        this.smtpService.delete();
        return HttpResponse.send(Response.Status.OK, "SUCCESSFULLY");
    }
}
