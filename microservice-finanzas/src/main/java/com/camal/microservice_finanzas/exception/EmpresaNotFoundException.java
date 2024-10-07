package com.camal.microservice_finanzas.exception;

public class EmpresaNotFoundException extends RuntimeException{
    public EmpresaNotFoundException(String message) {
        super(message);
    }
}
