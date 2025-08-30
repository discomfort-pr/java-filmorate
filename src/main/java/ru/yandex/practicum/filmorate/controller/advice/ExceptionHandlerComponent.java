package ru.yandex.practicum.filmorate.controller.advice;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.*;

import java.util.Map;

@RestControllerAdvice
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExceptionHandlerComponent {

    String notFoundTableValue = "Значение(-я) должно(-ы) содержаться в соответствующей таблице";

    @ExceptionHandler({
            FilmNotFoundException.class, UserNotFoundException.class,
            GenreNotFoundException.class, RatingNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundExceptions(final NotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(final MethodArgumentNotValidException e) {
        ExceptionResponse response = new ExceptionResponse("ошибка при валидации данных", e.getMessage());

        return new ResponseEntity<>(
                response,
                (e.getMessage().contains((notFoundTableValue)) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST)
        );
    }
}
