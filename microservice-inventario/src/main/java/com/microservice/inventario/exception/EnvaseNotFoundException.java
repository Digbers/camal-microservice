package com.microservice.inventario.exception;

public class EnvaseNotFoundException extends RuntimeException {
    public EnvaseNotFoundException(String message) {
        super(message);
    }
}
