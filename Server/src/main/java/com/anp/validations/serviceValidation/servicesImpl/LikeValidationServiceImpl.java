package com.anp.validations.serviceValidation.servicesImpl;

import org.springframework.stereotype.Component;

import com.anp.domain.entities.Like;
import com.anp.validations.serviceValidation.services.LikeValidationService;

@Component
public class LikeValidationServiceImpl implements LikeValidationService {
    @Override
    public boolean isValid(Like like) {
        return like != null;
    }
}
