package com.anp.validations.serviceValidation.servicesImpl;

import org.springframework.stereotype.Component;

import com.anp.domain.entities.Relationship;
import com.anp.validations.serviceValidation.services.RelationshipValidationService;

@Component
public class RelationshipValidationServiceImpl implements RelationshipValidationService {
    @Override
    public boolean isValid(Relationship relationship) {
        return relationship != null;
    }
}
