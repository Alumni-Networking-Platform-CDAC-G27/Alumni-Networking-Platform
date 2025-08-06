package com.anp.validations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Component
@Constraint(validatedBy = PasswordMatchingValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatching {

    String message() default "Passwords mismatch.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload()default {};
}
