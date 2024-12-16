package com.camal.microservice_finanzas.controller.DTO.compras;

import com.camal.microservice_finanzas.controller.DTO.FormasPagosDTO;
import com.camal.microservice_finanzas.controller.DTO.MonedasDTO;
import com.camal.microservice_finanzas.persistence.entity.FormasPagosEntity;
import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantesComprasPagosDTO {
    private Long id;
    private Long idEmpresa;
    private Long idComprobanteCompra;
    private String formaPagosEntity;
    private BigDecimal montoPagado;
    private LocalDate fechaPago;
    private String descripcion;
    private String monedasEntity;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;
}
