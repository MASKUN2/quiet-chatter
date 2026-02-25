package maskun.quietchatter.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import maskun.quietchatter.security.adaptor.ExpiredAuthTokenException;
import maskun.quietchatter.security.application.in.MemberDeactivatedException;
import org.springframework.http.HttpStatus;

import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
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

    @ExceptionHandler(MemberDeactivatedException.class)
    ProblemDetail handleMemberDeactivatedException(MemberDeactivatedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(UNAUTHORIZED, ex.getMessage());
        problemDetail.setType(java.net.URI.create("/errors/member-deactivated"));
        problemDetail.setProperty("reactivationToken", ex.getReactivationToken());
        return problemDetail;
    }

    @ExceptionHandler(ExpiredAuthTokenException.class)
    ProblemDetail handleExpiredAuthTokenException(ExpiredAuthTokenException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(UNAUTHORIZED, "Reactivation token has expired. Please log in again to receive a new token.");
        problemDetail.setType(java.net.URI.create("/errors/token-expired"));
        problemDetail.setTitle("Reactivation Token Expired");
        return problemDetail;
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
