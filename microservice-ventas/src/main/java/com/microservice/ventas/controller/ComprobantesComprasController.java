package com.microservice.ventas.controller;

import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasCaDTO;
import com.microservice.ventas.service.ccompras.ComprobantesComprasService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("api/compras/comprobantes")
@RequiredArgsConstructor
public class ComprobantesComprasController {
    private final ComprobantesComprasService comprobantesComprasService;

    @GetMapping("/list")
    public ResponseEntity<Page<ComprobantesComprasCaDTO>> getAll(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEmision,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCreacion,
            @RequestParam(required = false) String codigoTipo,
            @RequestParam(required = false) String serie,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String numeroDoc,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal subtotal,
            @RequestParam(required = false) BigDecimal impuesto,
            @RequestParam(required = false) BigDecimal total,
            @RequestParam(required = false) String monedaCodigo,
            @RequestParam(required = false) Double tipoCambio,
            @RequestParam(required = false) String estadoCodigo,
            @RequestParam(required = false) Long idPuntoVenta,
            @RequestParam(required = false) String sort // Parámetro sort opcional
    ) {
        Pageable pageable;
        // Verificar si el parámetro sort está presente
        if (sort != null && !sort.isEmpty()) {
            // Dividir el sort en columna y dirección
            String[] sortParams = sort.split(",");
            String column = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            pageable = PageRequest.of(page, size, Sort.by(direction, column));
        } else {
            // Solo paginación si no hay sort
            pageable = PageRequest.of(page, size);
        }
        Page<ComprobantesComprasCaDTO> comprobantesCompras = comprobantesComprasService.findAll(
                fechaEmision,fechaCreacion,codigoTipo, serie,
                numero, numeroDoc, nombre, subtotal, impuesto, total,
                monedaCodigo, tipoCambio, estadoCodigo, idPuntoVenta, pageable);
        return ResponseEntity.ok(comprobantesCompras);
    }

}
