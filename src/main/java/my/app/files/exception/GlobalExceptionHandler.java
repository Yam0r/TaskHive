package my.app.files.exception;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            RegistrationException.class
    })
    public ResponseEntity<Object> handleAlreadyExists(RuntimeException ex) {
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            EntityNotFoundException.class,
            jakarta.persistence.EntityNotFoundException.class
    })
    public ResponseEntity<Object> handleEntityNotFound(RuntimeException ex) {
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<Object> handleUnauthorized(RuntimeException ex) {
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherExceptions(Exception ex) {
        if (ex instanceof ErrorResponse errorResponse) {
            return new ResponseEntity<>(Map.of("error", ex.getMessage()),
                    errorResponse.getStatusCode());
        }
        log.error("Unexpected error", ex);
        return new ResponseEntity<>(Map.of("error", "Internal Server Error"),
                HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(Map.of("errors", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LabelAlreadyExistsException.class)
    public ResponseEntity<Object> handleLabelAlreadyExists(LabelAlreadyExistsException ex) {
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthentication(AuthenticationException ex) {
        return new ResponseEntity<>(Map.of("error", "Invalid email or password"),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>(Map.of("error", "Access denied"), HttpStatus.FORBIDDEN);
    }
}
