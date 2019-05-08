package nl.fontys.api.advice;

import nl.fontys.Utils.Exceptions.KwetterNotFoundException;
import nl.fontys.Utils.Exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NotFoundAdvice {
    @ResponseBody
    @ExceptionHandler({KwetterNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String notFoundHandler(final RuntimeException ex) {
        return ex.getMessage();
    }
}
