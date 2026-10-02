package com.saveql.backend.service.smtp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.saveql.backend.dto.request.system.SmtpSendMailRequest;
import com.saveql.backend.entity.system.SmtpServer;
import com.saveql.backend.util.math.Crypto;
import com.saveql.backend.util.rest.exception.HttpResponse;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.mail.MailClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SmtpService {

    @Inject
    Vertx vertx;

    @Inject
    Crypto crypto;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    SmtpMailTemplateService smtpMailTemplateService;

    public Uni<Void> send(SmtpSendMailRequest request) {
        String to = request.to();
        String subject = request.subject();
        String templateId = request.template();
        Map<String, String> variables = request.variables();
        String html = this.smtpMailTemplateService.render(templateId, variables);
        return this.send(to, subject, html);
    }

    public Uni<Void> send(String to, String subject, String templateId, Map<String, String> variables) {
        String html = this.smtpMailTemplateService.render(templateId, variables);
        return this.send(to, subject, html);
    }

    private Uni<Void> send(String to, String subject, String body) {
        return Uni.createFrom()
                .item(() -> this.prepare(to, subject, body))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .chain(mail -> mail.client().sendMail(mail.message())
                        .ifNoItem().after(Duration.ofSeconds(30)).fail()
                        .eventually(mail.client()::close)
                        .onFailure().transform(e -> new HttpResponse(Response.Status.INTERNAL_SERVER_ERROR, "FAILED_TO_SEND_EMAIL")))
                .replaceWithVoid();
    }

    private PreparedMail prepare(String to, String subject, String body) {
        SmtpServer smtp = this.get();

        MailConfig config = new MailConfig()
                .setHostname(smtp.getHost())
                .setPort(smtp.getPort())
                .setUsername(smtp.getUsername())
                .setPassword(this.crypto.decrypt(smtp.getPassword()))
                .setAuthMethods(smtp.getAuthMethods())
                .setSsl(smtp.isSsl())
                .setStarttls(smtp.getStartTls());

        MailMessage message = new MailMessage()
                .setFrom(smtp.getFromAddress())
                .setTo(to)
                .setSubject(subject)
                .setHtml(body);

        return new PreparedMail(MailClient.createShared(this.vertx, config), message);
    }

    private record PreparedMail(MailClient client, MailMessage message) {}

    public SmtpServer get() {
        SmtpServer smtp = SmtpServer.findAll().firstResult();
        if (smtp == null) throw new HttpResponse(Response.Status.NOT_FOUND, "SMTP_SERVER_NOT_FOUND");
        return smtp;
    }

    @Transactional
    public SmtpServer create(SmtpServer smtp) {
        if (SmtpServer.count() > 0) throw new HttpResponse(Response.Status.CONFLICT, "SMTP_SERVER_ALREADY_EXISTS");
        smtp.setPassword(this.crypto.encrypt(smtp.getPassword()));
        smtp.persist();
        return smtp;
    }

    @Transactional
    public SmtpServer update(ObjectNode input) {
        SmtpServer smtp = this.get();

        JsonNode password = input.remove("password");

        if (input.get("id") != null) throw new HttpResponse(Response.Status.BAD_REQUEST, "CANNOT_UPDATE_SMTP_SERVER_ID");
        input.remove("id");

        List<String> fields = new ArrayList<>();
        input.properties().forEach(field -> {
            if (field.getValue().isNull()) fields.add(field.getKey());
        });

        fields.forEach(input::remove);

        try {
            this.objectMapper.readerForUpdating(smtp).readValue(input);
        } catch (IOException e) {
            throw new HttpResponse(Response.Status.BAD_REQUEST, "INVALID_SMTP_SERVER_UPDATE");
        }

        if (password != null && !password.isNull() && !password.asText().isBlank()) {
            smtp.setPassword(this.crypto.encrypt(password.asText()));
        }

        return smtp;
    }

    @Transactional
    public void delete() {
        SmtpServer smtp = this.get();
        smtp.delete();
    }
}
