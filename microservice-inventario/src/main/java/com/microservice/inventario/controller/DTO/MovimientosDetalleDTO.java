package com.microservice.inventario.controller.DTO;

import com.microservice.inventario.persistence.entity.EnvaseEntity;
import com.microservice.inventario.persistence.entity.MovimientosCabeceraEntity;
import com.microservice.inventario.persistence.entity.ProductosEntity;
import com.microservice.inventario.persistence.entity.UnidadesEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientosDetalleDTO {
    private Long id;
    private Long idEmpresa;
    private Long idMovimiento;
    private Long idProducto;
    private String nombreProducto;// agregado
    private Long envase;
    private BigDecimal peso;
    private BigDecimal total;
    private Integer cantidad;
    private BigDecimal tara;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;

}
