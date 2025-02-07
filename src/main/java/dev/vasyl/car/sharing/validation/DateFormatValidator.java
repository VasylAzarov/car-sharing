package dev.vasyl.car.sharing.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class DateFormatValidator implements ConstraintValidator<ValidDateFormat, LocalDate> {
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final Pattern PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String dateString = value.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        return PATTERN.matcher(dateString).matches();
    }
}
