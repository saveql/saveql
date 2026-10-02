package com.saveql.backend.entity.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.saveql.backend.entity.base.AbstractAuditingEntity;
import com.saveql.backend.entity.base.AuditingEntityListener;
import com.saveql.backend.entity.singleton.SingletonEntity;
import com.saveql.backend.util.math.IdGenerator;
import io.vertx.ext.mail.StartTLSOptions;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "system_smtp_server")
public class SmtpServer extends SingletonEntity {

    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private Integer port;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    private String fromAddress;

    @Column(nullable = false)
    private String authMethods = "DIGEST-MD5 CRAM-SHA256 CRAM-SHA1 CRAM-MD5 PLAIN LOGIN";

    @Column(nullable = false)
    private boolean ssl = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StartTLSOptions startTls = StartTLSOptions.REQUIRED;

}
