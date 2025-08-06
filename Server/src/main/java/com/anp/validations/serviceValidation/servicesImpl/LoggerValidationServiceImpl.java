package com.anp.validations.serviceValidation.servicesImpl;

import org.springframework.stereotype.Component;

import com.anp.domain.models.serviceModels.LoggerServiceModel;
import com.anp.validations.serviceValidation.services.LoggerValidationService;

@Component
public class LoggerValidationServiceImpl implements LoggerValidationService {
    @Override
    public boolean isValid(LoggerServiceModel loggerServiceModel) {
        return loggerServiceModel != null;
    }

    @Override
    public boolean isValid(String method, String principal, String tableName, String action) {
        return method != null && principal != null && tableName != null && action != null;
    }

    @Override
    public boolean isValid(String username) {
        return username != null;
    }
}
