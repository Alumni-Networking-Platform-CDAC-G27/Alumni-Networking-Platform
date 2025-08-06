package com.anp.validations.annotations;

import org.springframework.stereotype.Component;

import com.anp.domain.models.bindingModels.user.UserRegisterBindingModel;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class PasswordMatchingValidator implements ConstraintValidator<PasswordMatching, Object> {
    @Override
    public void initialize(PasswordMatching constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o instanceof UserRegisterBindingModel) {
            UserRegisterBindingModel user = (UserRegisterBindingModel) o;
            return user.getPassword().equals(user.getConfirmPassword());
        }
        return false;
    }
}
