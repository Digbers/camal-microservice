package com.microservice.ventas.controller;

import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasCabDTO;
import com.microservice.ventas.controller.response.ComprobanteVentaResponseDTO;
import com.microservice.ventas.entity.ComprobantesVentasCabEntity;
import com.microservice.ventas.service.cventas.ComprobantesVentasService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    @GetMapping("/reduced")
    public ResponseEntity<RestResponse<Page<ComprobanteVentaResponseDTO>>> getComprobantes(
            @RequestParam(required = false) String comprobanteTipo,
            @RequestParam(required = false) String serie,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String numeroDoc,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String monedaCodigo,
            @RequestParam(required = false) BigDecimal total,
            @RequestParam(required = false) BigDecimal pagado,
            @RequestParam(required = false) BigDecimal saldo,
            Pageable pageable) {

        Page<ComprobanteVentaResponseDTO> comprobantes = comprobantesVentasServiceImpl
                .findAllComprobantes(comprobanteTipo, serie, numero, numeroDoc, nombre, monedaCodigo, total, pagado, saldo, pageable)
                .map(this::toResponseDTO);

        return ResponseEntity.ok(new RestResponse<>(comprobantes));
    }

    private ComprobanteVentaResponseDTO toResponseDTO(ComprobantesVentasCabEntity entity) {
        BigDecimal total = comprobantesVentasServiceImpl.calculateTotal(entity);

        return ComprobanteVentaResponseDTO.builder()
                .id(entity.getId())
                .tipoComprobante(entity.getComprobantesTiposEntity().getCodigo())
                .serie(entity.getSerie())
                .numero(entity.getNumero())
                .numeroDocumentoCliente(entity.getNumeroDocumentoCliente())
                .nombreCliente(entity.getNombreCliente())
                .codigoMoneda(entity.getCodigoMoneda())
                .total(total)
                .build();
    }
}
