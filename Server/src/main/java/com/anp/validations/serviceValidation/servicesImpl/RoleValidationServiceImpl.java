package com.anp.validations.serviceValidation.servicesImpl;

import org.springframework.stereotype.Component;

import com.anp.domain.entities.UserRole;
import com.anp.validations.serviceValidation.services.RoleValidationService;

@Component
public class RoleValidationServiceImpl implements RoleValidationService {
    @Override
    public boolean isValid(UserRole role) {
        return role != null;
    }
}
