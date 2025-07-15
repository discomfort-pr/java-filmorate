package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.NotBefore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ReleaseDateValidator implements ConstraintValidator<NotBefore, LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyy");
    private LocalDate referenceDate;

    @Override
    public void initialize(NotBefore constraintAnnotation) {
        String value = constraintAnnotation.value();
        if (value.equals("now")) {
            referenceDate = LocalDate.now();
        } else {
            try {
                referenceDate = LocalDate.parse(value, FORMATTER);
            } catch (DateTimeParseException exception) {
                referenceDate = LocalDate.now();
            }
        }
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return false;
        }
        return !localDate.isBefore(referenceDate);
    }
}
