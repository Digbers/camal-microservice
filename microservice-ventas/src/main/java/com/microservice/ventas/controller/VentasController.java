package com.microservice.ventas.controller;

import com.microservice.ventas.controller.DTO.ComprobantesTiposVentasDTO;
import com.microservice.ventas.controller.DTO.ComprobantesVentasCabDTO;
import com.microservice.ventas.controller.DTO.SeriesDTO;
import com.microservice.ventas.controller.DTO.VentaRequest;
import com.microservice.ventas.service.ventas.VentasServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/ventas")
public class VentasController {
    @Autowired
    private VentasServiceImpl ventasService;
    @PostMapping("/save")
    public ResponseEntity<ComprobantesVentasCabDTO> save(VentaRequest ventaRequest) {
        return ResponseEntity.ok(ventasService.save(ventaRequest));
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


}
