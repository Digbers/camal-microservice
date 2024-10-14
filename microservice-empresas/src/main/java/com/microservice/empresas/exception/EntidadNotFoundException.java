package com.microservice.empresas.exception;

public class EntidadNotFoundException extends RuntimeException{
    public EntidadNotFoundException(String message){
        super(message);
    }
}
