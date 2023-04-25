package com.controllers;

public class QAPEntityNotFoundException extends RuntimeException {
    public QAPEntityNotFoundException(Long id) {
        super("Could not find QAPEntity " + id);
    }
}