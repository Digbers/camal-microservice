package com.microservice.ventas.exception;

public class EmpresaNoEncontradaException extends RuntimeException{
    public EmpresaNoEncontradaException(String message) {
        super(message);
    }
}
