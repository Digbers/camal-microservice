package com.microservice.ventas.service;

import com.microservice.ventas.client.EmpresaClient;
import com.microservice.ventas.client.FinanzasClient;
import com.microservice.ventas.client.InventarioClient;
import com.microservice.ventas.controller.DTO.EmpresaDTO;
import com.microservice.ventas.controller.DTO.EntidadDTO;
import com.microservice.ventas.controller.request.MonedasRequest;
import com.microservice.ventas.controller.request.PuntoVentaResponse;
import com.microservice.ventas.controller.response.VentaResponseXReporteDet;
import com.microservice.ventas.controller.response.VentaResponseXResponseCab;
import com.microservice.ventas.entity.ComprobantesVentasCabEntity;
import com.microservice.ventas.entity.ComprobantesVentasDetEntity;
import com.microservice.ventas.repository.IcomprobantesVentasCabRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final IcomprobantesVentasCabRepository comprobantesVentasRepository;
    private final EmpresaClient empresaClient;
    private final InventarioClient inventarioClient;
    private final FinanzasClient finanzasClient;
    private final NumeroALetrasService numeroALetrasService;

    public String generarReporteBase64(Long idComprobante) throws Exception {
        // Obtener el comprobante de venta
        Optional<ComprobantesVentasCabEntity> comprobante = comprobantesVentasRepository.findById(idComprobante);
        if(comprobante.isEmpty()){
            log.error("No se encontro el documento de venta con el id : " + idComprobante);
            throw new RuntimeException("No se encontro el documento de venta con el id : " + idComprobante);
        }
        ComprobantesVentasCabEntity comprobanteVentaCa = comprobante.get();
        List<ComprobantesVentasDetEntity> comprobantesVentasDet = comprobanteVentaCa.getComprobantesVentasDetEntity();
        EmpresaDTO empresa = empresaClient.obtenerDetallesEmpresa(comprobanteVentaCa.getIdEmpresa());
        String dirLogos = empresaClient.getLogosDirectory();
        log.info("Directorio de logos: " + dirLogos);
        String dirImagenes = dirLogos.replace("\\", "/");
        log.info("Directorio de logos normalizado: " + dirImagenes);
        if(empresa == null){
            log.error("No se encontro la empresa con el id : " + comprobanteVentaCa.getIdEmpresa());
            throw new RuntimeException("No se encontro la empresa con el id : " + comprobanteVentaCa.getIdEmpresa());
        }
        EntidadDTO entidad = empresaClient.obtenerDetallesEntidad(comprobanteVentaCa.getIdCliente());
        if(entidad == null){
            log.error("No se encontro la entidad con el id : " + comprobanteVentaCa.getIdCliente());
            throw new RuntimeException("No se encontro la entidad con el id : " + comprobanteVentaCa.getIdCliente());
        }
        MonedasRequest monedas = finanzasClient.obtenerMoneda(comprobanteVentaCa.getIdEmpresa(), comprobanteVentaCa.getCodigoMoneda());
        if(monedas == null){
            log.error("No se encontro la moneda con el id : " + comprobanteVentaCa.getCodigoMoneda());
            throw new RuntimeException("No se encontro la moneda con el id : " + comprobanteVentaCa.getCodigoMoneda());
        }
        PuntoVentaResponse puntoVenta = inventarioClient.obtenerDetallesPuntoVenta(comprobanteVentaCa.getIdPuntoVenta());
        if(puntoVenta == null){
            log.error("No se encontro el punto de venta con el id : " + comprobanteVentaCa.getIdPuntoVenta());
            throw new RuntimeException("No se encontro el punto de venta con el id : " + comprobanteVentaCa.getIdPuntoVenta());
        }
        VentaResponseXResponseCab comprobanteVenta = VentaResponseXResponseCab.builder()
                .empresa(empresa.getRazonSocial())
                .rucEmpresa(empresa.getRuc())
                .empresaDireccion(empresa.getDireccion())
                .empresaTelefono(empresa.getTelefono())
                .empresaCelular(empresa.getCelular())
                .empresaCorreo(empresa.getCorreo())
                .serieNumero("N°: " +comprobanteVentaCa.getSerie() + " - " + comprobanteVentaCa.getNumero())
                .numeroDocumento(entidad.getNroDocumento())
                .fechaEmision(comprobanteVentaCa.getFechaEmision())
                .nombreCompleto(entidad.getNombre())
                .direccion(comprobanteVentaCa.getIdEmpresa().toString())
                .estadoComprobante(comprobanteVentaCa.getComprobantesVentasEstadoEntity().getCodigo())
                .observacion(comprobanteVentaCa.getObservacion())
                .usuCodigo(comprobanteVentaCa.getUsuarioCreacion())
                .moneda(monedas.getNombre())
                .puntoVenta(puntoVenta.getNombre())
                .puntoVentaDireccion(puntoVenta.getDireccion())
                .monedaCodigo(monedas.getSimbolo())
                .qr("https://www.google.com")
                .build();
        List<VentaResponseXReporteDet> listVentaXResponseDet = new ArrayList<>();
        AtomicReference<BigDecimal> montoTotal = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> descuentoTotal = new AtomicReference<>(BigDecimal.ZERO);
        BigDecimal impuestoOfTotal = BigDecimal.ZERO;
        BigDecimal subtotalOfTotal = BigDecimal.ZERO;
        comprobantesVentasDet.forEach(comprobantesV -> {
            BigDecimal pesoNeto = comprobantesV.getPeso().subtract(comprobantesV.getTara());
            BigDecimal subtotal = pesoNeto.multiply(comprobantesV.getPrecioUnitario());
            BigDecimal descuento = comprobantesV.getDescuento();

            montoTotal.updateAndGet(valorActual -> valorActual.add(subtotal.subtract(descuento)));
            descuentoTotal.updateAndGet(valorActual -> valorActual.add(descuento));
            VentaResponseXReporteDet ventaXResponseXReporteDet = VentaResponseXReporteDet.builder()
                    .numero(comprobantesV.getNumero())
                    .descripcion(comprobantesV.getDescripcion())
                    .peso(comprobantesV.getPeso())
                    .cantidad(comprobantesV.getCantidad())
                    .precioUnitario(comprobantesV.getPrecioUnitario())
                    .total(subtotal)  // total sin descuento
                    .descuento(descuento)  // Asignar el descuento
                    .tara(comprobantesV.getTara())
                    .build();
            listVentaXResponseDet.add(ventaXResponseXReporteDet);
        });
        impuestoOfTotal = montoTotal.get().multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        subtotalOfTotal = montoTotal.get().subtract(impuestoOfTotal);
        comprobanteVenta.setIgv(impuestoOfTotal);
        comprobanteVenta.setImporteTotalNeto(montoTotal.get());
        comprobanteVenta.setImporteTotal(subtotalOfTotal);
        comprobanteVenta.setDescuentoTotal(descuentoTotal.get());
        String moneda = comprobanteVentaCa.getCodigoMoneda().equals("SOL") ? "SOLES" : "DOLARES";
        comprobanteVenta.setTotalTexto("SON: " + numeroALetrasService.convertirNumeroALetras(montoTotal.get().doubleValue(), moneda));
        JRBeanCollectionDataSource cabeceraDataSource = new JRBeanCollectionDataSource(Collections.singletonList(comprobanteVenta));
        JRBeanCollectionDataSource detalleDataSource = new JRBeanCollectionDataSource(listVentaXResponseDet);
        // Parámetros del reporte
        Map<String, Object> parametros = new HashMap<>();
        if(comprobanteVentaCa.getComprobantesTiposEntity().getCodigo().equals("TIK")){
            parametros.put("tipo-comprobante", "Ticket");
        } else if (comprobanteVentaCa.getComprobantesTiposEntity().getCodigo().equals("BOL")){
            parametros.put("tipo-comprobante", "Boleta");
        } else if (comprobanteVentaCa.getComprobantesTiposEntity().getCodigo().equals("FAC")){
            parametros.put("tipo-comprobante", "Factura");
        } else{
            parametros.put("tipo-comprobante", "Ticket");
        }
        parametros.put("tipo-comprobante", comprobanteVentaCa.getComprobantesTiposEntity().getCodigo());
        parametros.put("detalleDataSource", detalleDataSource);
        log.info("Directorio de logos: " + dirLogos + "\\" + empresa.getLogo());
        parametros.put("dirImagenes",  dirLogos + "\\" + empresa.getLogo());
        return generarReporte("COMP", parametros, detalleDataSource);
    }
    public String generarReporte(String nombreReporte, Map<String, Object> parametros, JRDataSource dataSource) {
        try (InputStream reporteStream = getClass().getClassLoader().getResourceAsStream("reportes/" + nombreReporte + ".jasper")) {
            if (reporteStream == null) {
                throw new RuntimeException("No se pudo encontrar el archivo " + nombreReporte + ".jasper en el classpath");
            }
            // Cargar el reporte Jasper
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reporteStream);

            // Llenar el reporte con datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            // Exportar el reporte a PDF en Base64
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
                return Base64.getEncoder().encodeToString(outputStream.toByteArray());
            }
        } catch (JRException e) {
            log.error("Error al procesar el reporte Jasper: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar el reporte: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Error de E/S al leer el reporte: {}", e.getMessage(), e);
            throw new RuntimeException("Error de lectura/escritura al procesar el reporte: " + e.getMessage(), e);
        }
    }
    // Cargar el archivo .jasper
        /*
        try {
            InputStream reporteStream = this.getClass().getClassLoader().getResourceAsStream("reportes/COMP.jasper");
            if (reporteStream == null) {
                throw new FileNotFoundException("No se pudo encontrar el archivo TIK.jasper en el classpath");
            }

            // Intenta cargar el reporte primero para validar su integridad
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
        }*/

}
