package com.camal.microservice_finanzas.service.reportesfinancieros;

import java.time.LocalDate;

public interface IReportesFinancieros {
    String generarReporteIngresosEgresosXfechas(LocalDate fechaInicio, LocalDate fechaFin, Long empresa, String Formato);
    String generarReporteUtilidadPorDia(LocalDate fecha, Long empresa, String Formato);
}
