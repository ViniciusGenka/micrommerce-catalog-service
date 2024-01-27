package com.genka.catalogservice.infra.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PositiveAndIntegerValidator implements
        ConstraintValidator<PositiveAndInteger, Object> {

    @Override
    public void initialize(PositiveAndInteger constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if(value instanceof Integer) {
            return (Integer) value > 0;
        }
        if (value instanceof String) {
            try {
                int intValue = Integer.parseInt((String) value);
                return intValue > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
}
