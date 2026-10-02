package com.saveql.backend.service.smtp;

import com.saveql.backend.util.rest.exception.HttpResponse;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class SmtpMailTemplateService {

    private static final Logger LOG = Logger.getLogger(SmtpMailTemplateService.class);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    @Inject
    Vertx vertx;

    @ConfigProperty(name = "saveql.mail.templates.repository", defaultValue = "saveql/mail-templates")
    String repository;

    @ConfigProperty(name = "saveql.mail.templates.branch", defaultValue = "master")
    String branch;

    private final Map<String, String> templates = new ConcurrentHashMap<>();

    void onStart(@Observes StartupEvent event) {
        WebClient webClient = WebClient.create(this.vertx, new WebClientOptions().setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/37.0.2062.94 Chrome/37.0.2062.94 Safari/537.36"));

        this.download(webClient).subscribe().with(
                count -> LOG.infof("Loaded %d mail template(s) from %s", count, this.repository),
                error -> LOG.warn("Could not load mail templates", error));
    }

    public String render(String templateId, Map<String, String> variables) {
        String template = this.templates.get(templateId);
        if (template == null) throw new HttpResponse(Response.Status.NOT_FOUND, "MAIL_TEMPLATE_NOT_FOUND");

        for (Map.Entry<String, String> variable : variables.entrySet()) {
            String value = variable.getValue() == null ? "" : variable.getValue();
            template = template.replaceAll(
                    "\\{\\{\\s*" + Pattern.quote(variable.getKey()) + "\\s*}}",
                    Matcher.quoteReplacement(value));
        }

        return template;
    }

    private Uni<Integer> download(WebClient webClient) {
        return webClient
                .getAbs("https://api.github.com/repos/" + this.repository + "/git/trees/" + this.branch + "?recursive=1")
                .putHeader("Accept", "application/vnd.github+json")
                .send()
                .ifNoItem().after(REQUEST_TIMEOUT).fail()
                .chain(response -> {
                    if (response.statusCode() != 200) {
                        return Uni.createFrom().<Integer>failure(new IllegalStateException(
                                "Could not fetch mail template tree from " + this.repository + "@" + this.branch
                                        + ": HTTP " + response.statusCode() + " " + response.statusMessage()));
                    }

                    IO.println("Response body: " + response.bodyAsString());
                    List<Uni<Void>> downloads = response.bodyAsJsonObject().getJsonArray("tree").stream()
                            .map(JsonObject.class::cast)
                            .filter(entry -> "blob".equals(entry.getString("type")))
                            .map(entry -> entry.getString("path"))
                            .filter(path -> path.endsWith(".html"))
                            .map(path -> webClient
                                    .getAbs("https://raw.githubusercontent.com/" + this.repository + "/" + this.branch + "/" + path)
                                    .send()
                                    .ifNoItem().after(REQUEST_TIMEOUT).fail()
                                    .invoke(file -> {
                                        if (file.statusCode() != 200) return;

                                        int index = path.lastIndexOf('/');
                                        String name = index < 0 ? path : path.substring(index + 1);
                                        this.templates.put(name.substring(0, name.length() - ".html".length()), file.bodyAsString());
                                    })
                                    .replaceWithVoid())
                            .toList();

                    return Uni.join().all(downloads).andFailFast()
                            .replaceWith(this.templates.size());
                });
    }
}
