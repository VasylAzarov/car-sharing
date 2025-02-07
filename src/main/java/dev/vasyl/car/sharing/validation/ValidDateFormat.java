package dev.vasyl.car.sharing.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = DateFormatValidator.class)
public @interface ValidDateFormat {
    String message() default "Invalid date format, expected yyyy-MM-dd";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
