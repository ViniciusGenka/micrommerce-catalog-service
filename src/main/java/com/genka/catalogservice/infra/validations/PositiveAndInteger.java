package com.genka.catalogservice.infra.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PositiveAndIntegerValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveAndInteger {
    String message() default "field must be positive and integer";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
