package com.zim4ik.spacecatmarket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        ProblemDetail problem = buildProblemDetail(
                HttpStatus.NOT_FOUND,
                "Not found",
                ex.getMessage()
        );
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetail problem = buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "Validation failed for request"

        );

        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(fieldError -> {
                    errors.computeIfAbsent(fieldError.getField(), key -> new ArrayList<>())
                            .add(fieldError.getDefaultMessage());
                });

        problem.setProperty("fieldErrors", errors);

        return problem;
    }

    @ExceptionHandler(DomainValidationException.class)
    public ProblemDetail handleDomainValidation(DomainValidationException ex) {
        ProblemDetail problem = buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid domain state",
                ex.getMessage()
        );
        return problem;
    }


    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatus(status);

        problem.setTitle(title);
        problem.setDetail(detail);
        return problem;
    }
}
