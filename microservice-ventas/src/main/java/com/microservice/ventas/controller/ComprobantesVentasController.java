package com.microservice.ventas.controller;

import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasCabDTO;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasDetDTO;
import com.microservice.ventas.controller.response.ComprobanteVentaResponseDTO;
import com.microservice.ventas.entity.ComprobantesVentasCabEntity;
import com.microservice.ventas.service.cventas.ComprobantesVentasService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/ventas/comprobantes")
@RequiredArgsConstructor
public class ComprobantesVentasController {
    private final ComprobantesVentasService comprobantesVentasServiceImpl;

    @GetMapping("/list")
    public ResponseEntity<Page<ComprobantesVentasCabDTO>> getAll(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEmision,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimiento,
            @RequestParam(required = false) String codigoTipo,
            @RequestParam(required = false) String serie,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String numeroDoc,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal subtotal,
            @RequestParam(required = false) BigDecimal impuesto,
            @RequestParam(required = false) BigDecimal total,
            @RequestParam(required = false) String monedaCodigo,
            @RequestParam(required = false) String estadoCodigo,
            @RequestParam(required = false) Long idPuntoVenta,
            @RequestParam(required = false) String sunat
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ComprobantesVentasCabDTO> result = comprobantesVentasServiceImpl.findALL(
                fechaEmision, fechaVencimiento, codigoTipo, serie, numero,
                numeroDoc, nombre, subtotal, impuesto, total, monedaCodigo,
                estadoCodigo, idPuntoVenta, sunat, pageable
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/find-detalle/{id}")
    public ResponseEntity<List<ComprobantesVentasDetDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(comprobantesVentasServiceImpl.findDetalleById(id));
    }


}
