package com.microservice.ventas.controller;

import com.microservice.ventas.controller.DTO.compras.CompraRequest;
import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasCaDTO;
import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasTiposDTO;
import com.microservice.ventas.service.compras.ComprasServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class ComprasController {
    private final ComprasServiceImpl comprasService;

    @GetMapping("/get-comprobantes-tipos/{idEmpresa}")
    public ResponseEntity<List<ComprobantesComprasTiposDTO>> getComprobantesTiposCompras(@PathVariable Long idEmpresa){
        return ResponseEntity.ok(comprasService.getComprobantesTiposCompras(idEmpresa));
    }
    @PostMapping("/guardar-compra")
    public CompletableFuture<ResponseEntity<Long>> guardarCompra(@RequestBody CompraRequest compraRequest){
        return comprasService.save(compraRequest)
                .thenApply(compraId -> ResponseEntity.ok(compraId))
                .exceptionally(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }
}
