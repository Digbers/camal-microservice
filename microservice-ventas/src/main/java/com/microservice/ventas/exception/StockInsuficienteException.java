package com.microservice.ventas.exception;

public class StockInsuficienteException extends RuntimeException{

    public StockInsuficienteException(String message) {
        super(message);
    }
}
