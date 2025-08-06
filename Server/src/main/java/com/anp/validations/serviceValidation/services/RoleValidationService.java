package com.anp.validations.serviceValidation.services;

import com.anp.domain.entities.UserRole;

public interface RoleValidationService {
    boolean isValid(UserRole role);
}
