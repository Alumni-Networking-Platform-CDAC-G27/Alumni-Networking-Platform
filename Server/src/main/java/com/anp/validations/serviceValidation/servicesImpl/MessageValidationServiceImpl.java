package com.anp.validations.serviceValidation.servicesImpl;

import org.springframework.stereotype.Component;

import com.anp.domain.models.bindingModels.message.MessageCreateBindingModel;
import com.anp.validations.serviceValidation.services.MessageValidationService;

@Component
public class MessageValidationServiceImpl implements MessageValidationService {

    @Override
    public boolean isValid(MessageCreateBindingModel messageCreateBindingModel) {
        return messageCreateBindingModel != null;
    }
}
