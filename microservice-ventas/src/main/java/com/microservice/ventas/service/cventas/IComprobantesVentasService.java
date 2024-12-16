package com.microservice.ventas.service.cventas;

import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasCabDTO;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasDetDTO;
import com.microservice.ventas.entity.ComprobantesVentasCabEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IComprobantesVentasService {
    Page<ComprobantesVentasCabDTO> findALL(LocalDate fechaEmision, LocalDate fechaVencimiento, String codigoTipo,
                                           String serie, String numero, String numeroDoc, String nombre,
                                           BigDecimal subtotal, BigDecimal impuesto, BigDecimal total, String monedaCodigo,
                                           String estadoCodigo, Long idPuntoVenta, String sunat, Pageable pageable);
    List<ComprobantesVentasDetDTO> findDetalleById(Long id);
}
