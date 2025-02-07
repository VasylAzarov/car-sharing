package dev.vasyl.car.sharing.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {PasswordValidator.class, PasswordMatchValidator.class})
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Invalid password";

    String matchMessage() default "Passwords do not match";

    String passwordField();

    String confirmPasswordField();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
