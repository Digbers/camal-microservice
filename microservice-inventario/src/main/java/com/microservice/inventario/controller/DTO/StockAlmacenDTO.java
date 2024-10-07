package com.microservice.inventario.controller.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microservice.inventario.persistence.entity.AlmacenEntity;
import com.microservice.inventario.persistence.entity.EnvaseEntity;
import com.microservice.inventario.persistence.entity.ProductosEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAlmacenDTO {
    private Long idStock;
    private Long idEmpresa;
    private Long idProducto;
    private Long idEnvase;
    private Long idAlmacen;
    private int cantidadEnvase;  // Cantidad de javas u otros envases
    private int cantidadProducto; // Cantidad de productos (pollos)
    private BigDecimal pesoTotal;  // Peso total de productos
    private LocalDate fechaRegistro;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
}
