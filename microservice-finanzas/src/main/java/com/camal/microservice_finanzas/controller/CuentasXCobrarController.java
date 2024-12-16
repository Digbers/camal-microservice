package com.camal.microservice_finanzas.controller;

import com.camal.microservice_finanzas.controller.DTO.ComprobantesVentasCobrosDTO;
import com.camal.microservice_finanzas.controller.DTO.ventas.CuentasXCobrarDTO;
import com.camal.microservice_finanzas.service.ventas.CuentasXCobrarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/finanzas/cuentas-cobrar")
@RequiredArgsConstructor
public class CuentasXCobrarController {
    private final CuentasXCobrarService cuentasXCobrarService;

    @GetMapping("/findAll")
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEmision,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int size,
            @RequestParam(required = false, defaultValue = "true") Boolean inCobrados) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(cuentasXCobrarService.findAll(inCobrados,fechaEmision,comprobanteTipo, serie, numero, numeroDoc, nombre, monedaCodigo, total, pagado, saldo, pageable));
    }
    @GetMapping("/find-cobros-by-comprobante/{idComprobante}")
    public ResponseEntity<List<ComprobantesVentasCobrosDTO>> findCobrosByComprobante(@PathVariable("idComprobante") Long idComprobante){
        return ResponseEntity.ok(cuentasXCobrarService.findCobrosByComprobante(idComprobante));
    }
    @PostMapping("/save-cobro")
    public ResponseEntity<ComprobantesVentasCobrosDTO> saveCobro(@RequestBody ComprobantesVentasCobrosDTO comprobantesVentasCobrosDTO){
        return ResponseEntity.ok(cuentasXCobrarService.saveCobro(comprobantesVentasCobrosDTO));
    }
    @PatchMapping("/update-cobro/{id}")
    public ResponseEntity<ComprobantesVentasCobrosDTO> updateCobro(@PathVariable("id") Long id, @RequestBody ComprobantesVentasCobrosDTO comprobantesVentasCobrosDTO){
        return ResponseEntity.ok(cuentasXCobrarService.updateCobro(id, comprobantesVentasCobrosDTO));
    }
}
