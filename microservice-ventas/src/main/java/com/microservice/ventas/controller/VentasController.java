package com.microservice.ventas.controller;

import com.microservice.ventas.controller.DTO.ventas.ComprobantesTiposVentasDTO;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasCabDTO;
import com.microservice.ventas.controller.DTO.SeriesDTO;
import com.microservice.ventas.controller.DTO.ventas.VentaRequest;
import com.microservice.ventas.service.ventas.VentasServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ventas")
public class VentasController {
    @Autowired
    private VentasServiceImpl ventasService;


    @PostMapping("/guardar-venta")
    public CompletableFuture<ResponseEntity<Long>> save(@RequestBody VentaRequest ventaRequest) {
        // Llamar al servicio y procesar el resultado de forma asíncrona
        return ventasService.save(ventaRequest)
                .thenApply(ventaId -> ResponseEntity.ok(ventaId)) // Retorna 200 OK con el ID de la venta
                .exceptionally(e -> {
                    // En caso de error, retorna una respuesta 500 con el mensaje de error
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null);
                });
    }
    @GetMapping("/get-series/comprobantes/{codigoTipoComprobante}/{idPuntoVenta}")
    public ResponseEntity<List<SeriesDTO>> getSeries(@PathVariable String codigoTipoComprobante, @PathVariable Long idPuntoVenta) {
        List<SeriesDTO> series = ventasService.getSeries(codigoTipoComprobante, idPuntoVenta);
        return ResponseEntity.ok(series);
    }
    @GetMapping("/get-comprobantes-tipos-ventas/{idEmpresa}")
    public ResponseEntity<List<ComprobantesTiposVentasDTO>> getComprobantesTiposVentas(@PathVariable Long idEmpresa) {
        return ResponseEntity.ok(ventasService.getComprobantesTiposVentas(idEmpresa));
    }
    @GetMapping("/get-comprobante/{idComprobante}")
    public ResponseEntity<String> getComprobante(@PathVariable Long idComprobante){
        return ResponseEntity.ok(ventasService.getComprobante(idComprobante));
    }
    @GetMapping("/get-empresa/{idempresa}")
    public ResponseEntity<String> getEmpresa(@PathVariable Long idempresa){
        return ResponseEntity.ok(ventasService.getEmpresa(idempresa));
    }


}
