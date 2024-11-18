package com.camal.microservice_finanzas.controller.DTO;



import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormasDeCobrosDTO {
    private Long id;
    private String codigo;
    private String descripcion;
    private Long idEmpresa;
    private MonedasDTO moneda;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;
}
