package com.camal.microservice_finanzas.controller;

import com.camal.microservice_finanzas.controller.DTO.ventas.CuentasXCobrarDTO;
import com.camal.microservice_finanzas.service.ventas.CuentasXCobrarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/finanzas")
@RequiredArgsConstructor
public class CuentasXCobrarController {
    private final CuentasXCobrarService cuentasXCobrarService;

    @GetMapping("/cuentas-cobrar")
    public ResponseEntity<Page<CuentasXCobrarDTO>> findAll(
            @RequestParam(required = false) String comprobanteTipo,
            @RequestParam(required = false) String serie,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String numeroDoc,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String monedaCodigo,
            @RequestParam(required = false) BigDecimal total,
            @RequestParam(required = false) BigDecimal pagado,
            @RequestParam(required = false) BigDecimal saldo,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int size) {
        return ResponseEntity.ok(cuentasXCobrarService.findAll(comprobanteTipo, serie, numero, numeroDoc, nombre, monedaCodigo, total, pagado, saldo, PageRequest.of(page, size)));
    }

}
