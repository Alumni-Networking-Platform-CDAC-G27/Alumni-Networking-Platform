package com.anp.validations.serviceValidation.services;

import com.anp.domain.entities.Relationship;

public interface RelationshipValidationService {
    boolean isValid(Relationship relationship);
}
