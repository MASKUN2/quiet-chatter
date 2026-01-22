package maskun.quietchatter.shared.web;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.status;

import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties.Problemdetails;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
class WebExceptionHandler {
    static final String ERROR_UNCAUGHT = "내부 서버 오류";

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> handleException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return badRequest().body(errorResponse);
    }

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<ErrorResponse> handleException(NoSuchElementException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return status(NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUncaught(Exception ex) {
        log.error("catch되지 않은 예외", ex);
        ErrorResponse errorResponse = new ErrorResponse(ERROR_UNCAUGHT);

        return internalServerError().body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    ProblemDetail handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ProblemDetail.forStatus(404);
    }
}
