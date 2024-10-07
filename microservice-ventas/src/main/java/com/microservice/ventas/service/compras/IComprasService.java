package com.microservice.ventas.service.compras;

import com.microservice.ventas.controller.DTO.compras.CompraRequest;
import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasCaDTO;

public interface IComprasService {
    ComprobantesComprasCaDTO save(CompraRequest compraRequest);
    Boolean deleteById(Long id);
    Boolean anularCompra(Long id);
}
