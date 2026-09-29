package com.saveql.backend.entity.singleton;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class SingletonEntity extends PanacheEntityBase {

    public static final Long SINGLETON_ID = 1L;

    @Id
    @Column(
            nullable = false,
            updatable = false,
            check = @CheckConstraint(name = "singleton_id_chk", constraint = "id = 1")
    )
    public Long id = SINGLETON_ID;
}