package com.jonathanfoucher.securityexample.controllers.advisers;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.slf4j.event.Level.DEBUG;
import static org.slf4j.event.Level.ERROR;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@Slf4j
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ProblemDetail> handleForbiddenExceptions(Exception exception, WebRequest request) {
        return generateResponseEntity(exception, request, FORBIDDEN, DEBUG);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleInternalServerErrorExceptions(Exception exception, WebRequest request) {
        return generateResponseEntity(exception, request, INTERNAL_SERVER_ERROR, ERROR);
    }

    private ResponseEntity<ProblemDetail> generateResponseEntity(Exception exception, WebRequest request, HttpStatus status, Level level) {
        log.makeLoggingEventBuilder(level)
                .log(exception.getMessage(), exception);

        ProblemDetail details = ProblemDetail.forStatus(status);
        details.setTitle(status.getReasonPhrase());
        details.setDetail(exception.getMessage());
        details.setInstance(URI.create(request.getDescription(false)));
        details.setProperty("timestamp", LocalDateTime.now().format(DATE_TIME_FORMATTER));

        return new ResponseEntity<>(details, status);
    }
}
