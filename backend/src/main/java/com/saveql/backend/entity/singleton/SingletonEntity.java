package com.saveql.backend.entity.singleton;

import com.saveql.backend.entity.base.AbstractAuditingEntity;
import com.saveql.backend.entity.base.AuditingEntityListener;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class SingletonEntity extends AbstractAuditingEntity {

    public static final Long SINGLETON_ID = 1L;

    @Id
    @Column(
            nullable = false,
            updatable = false,
            check = @CheckConstraint(name = "singleton_id_chk", constraint = "id = 1")
    )
    public Long id = SINGLETON_ID;
}