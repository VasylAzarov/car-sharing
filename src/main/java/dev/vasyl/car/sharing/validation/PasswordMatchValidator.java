package dev.vasyl.car.sharing.validation;

import dev.vasyl.car.sharing.exception.RegistrationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Objects;

public class PasswordMatchValidator implements ConstraintValidator<ValidPassword, Object> {
    private String passwordField;
    private String confirmPasswordField;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        this.passwordField = constraintAnnotation.passwordField();
        this.confirmPasswordField = constraintAnnotation.confirmPasswordField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field passwordField
                    = value.getClass().getDeclaredField(this.passwordField);
            Field confirmPasswordField
                    = value.getClass().getDeclaredField(this.confirmPasswordField);
            passwordField.setAccessible(true);
            confirmPasswordField.setAccessible(true);
            String password = (String) passwordField.get(value);
            String confirmPassword = (String) confirmPasswordField.get(value);
            return Objects.equals(password, confirmPassword);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RegistrationException(
                    "Error accessing fields for password match validation", e);
        }
    }
}
