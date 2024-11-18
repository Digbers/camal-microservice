package com.camal.microservice_finanzas.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse<T> {
    private T data;
    private String message;
    private boolean success;

    // Constructor conveniente cuando solo tenemos data
    public RestResponse(T data) {
        this.data = data;
        this.success = true;
        this.message = "Operación exitosa";
    }

    // Constructor conveniente para errores
    public static <T> RestResponse<T> error(String message) {
        RestResponse<T> response = new RestResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    // Constructor conveniente para respuestas exitosas con mensaje personalizado
    public static <T> RestResponse<T> success(T data, String message) {
        RestResponse<T> response = new RestResponse<>();
        response.setData(data);
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }
}