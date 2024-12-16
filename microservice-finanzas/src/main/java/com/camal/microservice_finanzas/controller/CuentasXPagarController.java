package com.camal.microservice_finanzas.controller;

import com.camal.microservice_finanzas.controller.DTO.ComprobantesVentasCobrosDTO;
import com.camal.microservice_finanzas.controller.DTO.compras.ComprobantesComprasPagosDTO;
import com.camal.microservice_finanzas.controller.DTO.compras.CuentasXPagarDTO;
import com.camal.microservice_finanzas.controller.DTO.ventas.CuentasXCobrarDTO;
import com.camal.microservice_finanzas.service.compras.CuentasXPagarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/finanzas/cuentas-pagar")
@RequiredArgsConstructor
public class CuentasXPagarController {

    private final CuentasXPagarService cuentasXCobrarService;
    @GetMapping("/findAll")
    public ResponseEntity<Page<CuentasXPagarDTO>> findAll(
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEmision,
            @RequestParam(required = false) int size,
            @RequestParam(required = false, defaultValue = "false") Boolean inPagado) {
        return ResponseEntity.ok(cuentasXCobrarService.findAll(inPagado,fechaEmision,comprobanteTipo, serie, numero, numeroDoc, nombre, monedaCodigo, total, pagado, saldo, PageRequest.of(page, size)));
    }
    @GetMapping("/find-pagos-by-comprobante")
    public ResponseEntity<List<ComprobantesComprasPagosDTO>> findPagosByComprobante(@PathVariable("idComprobante") Long idComprobante){
        return ResponseEntity.ok(cuentasXCobrarService.findPagosByComprobante(idComprobante));
    }
    @PostMapping("/save-pago")
    public ResponseEntity<ComprobantesComprasPagosDTO> savePago(@RequestBody ComprobantesComprasPagosDTO comprobantesVentasCobrosDTO){
        return ResponseEntity.ok(cuentasXCobrarService.savePago(comprobantesVentasCobrosDTO));
    }
    @PatchMapping("/update-pago/{id}")
    public ResponseEntity<ComprobantesComprasPagosDTO> updatePago(@PathVariable("id") Long id, @RequestBody ComprobantesComprasPagosDTO comprobantesVentasCobrosDTO){
        return ResponseEntity.ok(cuentasXCobrarService.updatePago(id, comprobantesVentasCobrosDTO));
    }
}
