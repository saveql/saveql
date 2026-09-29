package com.saveql.backend.entity.base;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.eclipse.microprofile.jwt.Claim;

import java.time.ZonedDateTime;

@RequestScoped
public class AuditingEntityListener {

    @Claim("id")
    String id;

    @PrePersist
    public void prePersist(AbstractAuditingEntity entity) {
        entity.setCreatedBy(this.actor());
        entity.setLastModifiedBy(this.actor());
        entity.setLastModifiedAt(ZonedDateTime.now());
    }

    @PreUpdate
    public void preUpdate(AbstractAuditingEntity entity) {
        entity.setLastModifiedBy(this.actor());
        entity.setLastModifiedAt(ZonedDateTime.now());
    }

    private String actor() {
        return id == null ? "SYSTEM" : id;
    }
}
