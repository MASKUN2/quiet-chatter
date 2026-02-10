package maskun.quietchatter.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import maskun.quietchatter.talk.domain.NotTalkOwnerException;

import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;

@Slf4j
@ControllerAdvice
class WebExceptionHandler {
    static final String ERROR_UNCAUGHT = "내부 서버 오류";

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleException(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(badRequest().build().getStatusCode(), ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    ProblemDetail handleException(NoSuchElementException ex) {
        return ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NotTalkOwnerException.class)
    ProblemDetail handleException(NotTalkOwnerException ex) {
        return ProblemDetail.forStatusAndDetail(FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUncaught(Exception ex) {
        log.error("catch되지 않은 예외", ex);
        return ProblemDetail.forStatusAndDetail(internalServerError().build().getStatusCode(), ERROR_UNCAUGHT);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    ProblemDetail handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ProblemDetail.forStatus(404);
    }
}
