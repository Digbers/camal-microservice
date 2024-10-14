package com.microservice.empresas.response;

import com.microservice.empresas.request.EntidadRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {
    private boolean success;
    private EntidadRequest data;
    private double time;
    private String source;
}
