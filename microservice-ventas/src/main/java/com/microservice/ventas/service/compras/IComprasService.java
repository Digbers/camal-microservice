package com.microservice.ventas.service.compras;

import com.microservice.ventas.controller.DTO.compras.CompraRequest;
import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasCaDTO;
import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasTiposDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IComprasService {
    CompletableFuture<Long> save(CompraRequest compraRequest);
    Boolean deleteById(Long id);
    Boolean anularCompra(Long id);
    List<ComprobantesComprasTiposDTO> getComprobantesTiposCompras(Long IdEmpresa);
}
