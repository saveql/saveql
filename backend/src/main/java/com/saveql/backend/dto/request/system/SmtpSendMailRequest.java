package com.saveql.backend.dto.request.system;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record SmtpSendMailRequest (
        @NotBlank(message = "EMAIL_REQUIRED")
        @Email(message = "EMAIL_INVALID")
        String to,

        @NotBlank(message = "SUBJECT_REQUIRED")
        String subject,

        @NotBlank(message = "TEMPLATE_REQUIRED")
        String template,

        Map<String, String> variables
) {}
