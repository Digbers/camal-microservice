package com.camal.microservice_finanzas.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormasDeCobrosRequest {
    private List<FormasCobrosDTO> formasDeCobro = new ArrayList<>();
}
