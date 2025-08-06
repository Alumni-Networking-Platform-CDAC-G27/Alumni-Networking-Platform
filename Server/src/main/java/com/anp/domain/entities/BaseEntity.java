package com.anp.domain.entities;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;


/**
 * Base entity class providing UUID-based ID for all entities.
 */
@MappedSuperclass
@Access(AccessType.PROPERTY)
public abstract class BaseEntity {

    protected String id;

    public BaseEntity() {
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, unique = true, updatable = false, length = 36)
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
