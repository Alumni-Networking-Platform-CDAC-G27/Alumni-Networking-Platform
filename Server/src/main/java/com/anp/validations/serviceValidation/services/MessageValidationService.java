package com.anp.validations.serviceValidation.services;

import com.anp.domain.models.bindingModels.message.MessageCreateBindingModel;

public interface MessageValidationService {
    boolean isValid(MessageCreateBindingModel messageCreateBindingModel);
}
