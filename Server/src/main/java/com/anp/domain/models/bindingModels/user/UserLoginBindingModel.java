package com.anp.domain.models.bindingModels.user;

import java.io.Serializable;

import com.anp.utils.constants.ValidationMessageConstants;
import com.anp.validations.annotations.Password;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserLoginBindingModel implements Serializable {
    private String username;
    private String password;

    public UserLoginBindingModel() {
    }

    @Pattern(regexp = "^([a-zA-Z0-9]+)$")
    @Size(min = 4, max = 16, message = ValidationMessageConstants.INVALID_CREDENTIALS_MESSAGE)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Password(minLength = 4, maxLength = 16, containsOnlyLettersAndDigits = true, message = ValidationMessageConstants.INVALID_CREDENTIALS_MESSAGE )
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}