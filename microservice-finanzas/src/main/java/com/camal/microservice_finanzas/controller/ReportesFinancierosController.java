package com.camal.microservice_finanzas.controller;

import com.camal.microservice_finanzas.service.reportesfinancieros.IReportesFinancieros;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("api/finanzas/repostes")
@RequiredArgsConstructor
public class ReportesFinancierosController {
    private final IReportesFinancieros reportesFinancieros;

    @GetMapping("ingresos_egresos_xfechas")
    public ResponseEntity<String> ingresosEgresosXfechas(@RequestParam LocalDate fechaInicio,
                                                         @RequestParam LocalDate fechaFin,
                                                         @RequestParam Long empresa,
                                                         @RequestParam String formato) {
        return ResponseEntity.ok(reportesFinancieros.generarReporteIngresosEgresosXfechas(fechaInicio, fechaFin, empresa, formato));
    }
    @GetMapping("utilidad-por-dia")
    public ResponseEntity<String> utilidadPorDia(@RequestParam LocalDate fecha,
                                                 @RequestParam Long empresa,
                                                 @RequestParam String formato) {
        return ResponseEntity.ok(reportesFinancieros.generarReporteUtilidadPorDia(fecha, empresa, formato));
    }

}
