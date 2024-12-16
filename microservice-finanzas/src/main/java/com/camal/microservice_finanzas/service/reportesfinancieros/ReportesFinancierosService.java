package com.camal.microservice_finanzas.service.reportesfinancieros;

import com.camal.microservice_finanzas.clients.EmpresaClient;
import com.camal.microservice_finanzas.controller.DTO.ReporteCabecera;
import com.camal.microservice_finanzas.controller.response.EmpresaResponse;
import com.camal.microservice_finanzas.controller.response.GastosXFechaDTO;
import com.camal.microservice_finanzas.controller.response.IngresosXFechaDTO;
import com.camal.microservice_finanzas.controller.response.UtilidadesResponse;
import com.camal.microservice_finanzas.persistence.repository.IComprobantesComprasPagosRepository;
import com.camal.microservice_finanzas.persistence.repository.IComprobantesVentasCobrosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportesFinancierosService implements IReportesFinancieros {
    private final IComprobantesVentasCobrosRepository comprobantesVentasRepository;
    private final IComprobantesComprasPagosRepository comprobantesComprasPagosRepository;
    private final EmpresaClient empresaClient;

    @Override
    public String generarReporteIngresosEgresosXfechas(LocalDate fechaInicio, LocalDate fechaFin, Long empresa, String Formato) {
        try{
            EmpresaResponse empresaResponse = empresaClient.obtenerDetallesEmpresa(empresa);
            if(empresaResponse == null){
                throw new RuntimeException("No se pudo obtener los detalles de la empresa");
            }
            List<GastosXFechaDTO> gastosXFecha = comprobantesComprasPagosRepository.findGastosXFecha(empresa, fechaInicio, fechaFin);
            List<IngresosXFechaDTO> ingresosXFecha = comprobantesVentasRepository.findIngresosPorFecha(empresa, fechaInicio, fechaFin);
            // Crear un mapa de fechas con ingresos
            Map<LocalDate, BigDecimal> mapaIngresos = ingresosXFecha.stream()
                    .collect(Collectors.toMap(IngresosXFechaDTO::getFechaIngreso, IngresosXFechaDTO::getIngresoTotal));
            // Crear un mapa de fechas con gastos
            Map<LocalDate, BigDecimal> mapaGastos = gastosXFecha.stream()
                    .collect(Collectors.toMap(GastosXFechaDTO::getFechaPago, GastosXFechaDTO::getGastoTotal));
            // Unificar las fechas
            Set<LocalDate> todasLasFechas = new HashSet<>();
            todasLasFechas.addAll(mapaIngresos.keySet());
            todasLasFechas.addAll(mapaGastos.keySet());

            // Crear la lista de UtilidadesResponse
            List<UtilidadesResponse> utilidades = todasLasFechas.stream()
                    .map(fecha -> {
                        BigDecimal ingresos = mapaIngresos.getOrDefault(fecha, BigDecimal.ZERO);
                        BigDecimal egresos = mapaGastos.getOrDefault(fecha, BigDecimal.ZERO);
                        BigDecimal utilidad = ingresos.subtract(egresos);
                        String estado = utilidad.compareTo(BigDecimal.ZERO) > 0 ? "Ganancia" :
                                utilidad.compareTo(BigDecimal.ZERO) < 0 ? "Pérdida" : "Neutral";

                        return UtilidadesResponse.builder()
                                .fecha(fecha)
                                .ingresos(ingresos)
                                .egresos(egresos)
                                .utilidad(utilidad)
                                .estado(estado)
                                .build();
                    })
                    .sorted(Comparator.comparing(UtilidadesResponse::getFecha)) // Ordenar por fecha
                    .collect(Collectors.toList());
            ReporteCabecera cabecera = ReporteCabecera.builder()
                    .titulo("UTILIDADES")
                    .fechaEmision(LocalDate.now().toString())
                    .empresa(empresaResponse.getRazonSocial())
                    .ruc(empresaResponse.getRuc())
                    .direccion(empresaResponse.getDireccion())
                    .build();
            JRBeanCollectionDataSource cabeceraDataSource = new JRBeanCollectionDataSource(Collections.singletonList(cabecera));
            JRBeanCollectionDataSource detalleDataSource = new JRBeanCollectionDataSource(utilidades);
            // Parámetros del reporte
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("detalleDataSource", detalleDataSource);

            try {
                InputStream reporteStream = this.getClass().getClassLoader().getResourceAsStream("reportes/UTILIDADES.jasper");
                if (reporteStream == null) {
                    throw new FileNotFoundException("No se pudo encontrar el archivo TIK.jasper en el classpath");
                }

                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(reporteStream)) {
                    JasperReport jasperReport = (JasperReport) JRLoader.loadObject(bufferedInputStream);
                    // Si llega aquí, el reporte se cargó correctamente
                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, cabeceraDataSource);
                    // Exportar el reporte a un ByteArrayOutputStream
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

                    return Base64.getEncoder().encodeToString(outputStream.toByteArray());
                }
            } catch (JRException e) {
                log.error("Error al procesar el reporte Jasper: " + e.getMessage(), e);
                throw new RuntimeException("Error al procesar el reporte: " + e.getMessage());
            } catch (FileNotFoundException e) {
                log.error("No se encontró el archivo del reporte: " + e.getMessage(), e);
                throw new RuntimeException("No se encontró el archivo del reporte: " + e.getMessage());
            } catch (IOException e) {
                log.error("Error de E/S al leer el reporte: " + e.getMessage(), e);
                throw new RuntimeException("Error al leer el archivo del reporte: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("Error al generar reporte de ingresos y egresos por fecha", e);
            throw new RuntimeException("Error al generar reporte de ingresos y egresos por fecha", e);
        }
    }

    @Override
    public String generarReporteUtilidadPorDia(LocalDate fecha, Long empresa, String Formato) {
        return "";
    }
}
