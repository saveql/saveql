package com.saveql.backend.entity.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
@MappedSuperclass
public abstract class AbstractAuditingEntity extends PanacheEntityBase {

    @JsonIgnore
    @Column(updatable = false, nullable = false)
    private String createdBy;

    @JsonIgnore
    @Column(updatable = false, nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @JsonIgnore
    @Column(nullable = false)
    private String lastModifiedBy;

    @JsonIgnore
    @Column(nullable = false)
    private ZonedDateTime lastModifiedAt;
}
