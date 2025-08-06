package com.anp.validations.serviceValidation.services;

import com.anp.domain.entities.Like;

public interface LikeValidationService {
    boolean isValid(Like like);
}
