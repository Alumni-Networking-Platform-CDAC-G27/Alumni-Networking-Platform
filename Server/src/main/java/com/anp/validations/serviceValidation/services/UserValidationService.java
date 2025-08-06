package com.anp.validations.serviceValidation.services;

import org.springframework.security.core.userdetails.UserDetails;

import com.anp.domain.entities.User;
import com.anp.domain.models.bindingModels.user.UserRegisterBindingModel;
import com.anp.domain.models.bindingModels.user.UserUpdateBindingModel;
import com.anp.domain.models.serviceModels.UserServiceModel;

public interface UserValidationService {
    boolean isValid(User user);

    boolean isValid(UserServiceModel userServiceModel);

    boolean isValid(UserRegisterBindingModel userRegisterBindingModel);

    boolean isValid(String firstParam, String secondParam);

    boolean isValid(UserUpdateBindingModel userUpdateBindingModel);

    boolean isValid(UserDetails userData);
}
