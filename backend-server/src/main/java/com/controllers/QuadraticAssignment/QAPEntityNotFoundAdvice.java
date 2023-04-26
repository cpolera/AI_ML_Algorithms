package com.controllers.QuadraticAssignment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class QAPEntityNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(QAPEntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String qapEntityNotFoundHandler(QAPEntityNotFoundException ex) {
        return ex.getMessage();
    }
}