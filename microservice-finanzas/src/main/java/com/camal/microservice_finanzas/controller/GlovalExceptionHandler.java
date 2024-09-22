package com.camal.microservice_finanzas.controller;

import com.camal.microservice_finanzas.exception.ComprobantesVentasCobrosException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlovalExceptionHandler {
    @ExceptionHandler(ComprobantesVentasCobrosException.class)
    public ResponseEntity<String> handleComprobantesVentasCobrosException(ComprobantesVentasCobrosException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + ex.getMessage());
    }
}
