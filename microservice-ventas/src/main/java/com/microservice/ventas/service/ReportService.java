package com.microservice.ventas.service;

import com.microservice.ventas.client.EmpresaClient;
import com.microservice.ventas.controller.DTO.EmpresaDTO;
import com.microservice.ventas.controller.DTO.EntidadDTO;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final IcomprobantesVentasCabRepository comprobantesVentasRepository;
    private final EmpresaClient empresaClient;
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
        if(empresa == null){
            log.error("No se encontro la empresa con el id : " + comprobanteVentaCa.getIdEmpresa());
            throw new RuntimeException("No se encontro la empresa con el id : " + comprobanteVentaCa.getIdEmpresa());
        }
        EntidadDTO entidad = empresaClient.obtenerDetallesEntidad(comprobanteVentaCa.getIdCliente());
        if(entidad == null){
            log.error("No se encontro la entidad con el id : " + comprobanteVentaCa.getIdCliente());
            throw new RuntimeException("No se encontro la entidad con el id : " + comprobanteVentaCa.getIdCliente());
        }
        VentaResponseXResponseCab comprobanteVenta = VentaResponseXResponseCab.builder()
                .empresa(empresa.getRazonSocial())
                .rucEmpresa("RUC: " + empresa.getRuc())
                .empresaDireccion(empresa.getDireccion() +  " WhatsApp: " + empresa.getCelular() + " Telefono: " + empresa.getTelefono())
                .serieNumero(comprobanteVentaCa.getSerie() + " - " + comprobanteVentaCa.getNumero())
                .numeroDocumento("DNI: " + entidad.getNroDocumento())
                .fechaEmision(comprobanteVentaCa.getFechaEmision())
                .nombreCompleto("Cliente:" + entidad.getNombre())
                .direccion("Dirección: " + comprobanteVentaCa.getIdEmpresa().toString())
                .estadoComprobante("Condicion: " + comprobanteVentaCa.getComprobantesVentasEstadoEntity().getCodigo())
                .observacion("Obs." + comprobanteVentaCa.getObservacion())
                .usuCodigo("Usuario: " + comprobanteVentaCa.getUsuarioCreacion())
                .build();
        List<VentaResponseXReporteDet> listVentaXResponseDet = new ArrayList<>();
        comprobantesVentasDet.forEach(comprobantesV -> {
            // Calcular el total (precio unitario * cantidad)
            BigDecimal total = comprobantesV.getPrecioUnitario().multiply(BigDecimal.valueOf(comprobantesV.getCantidad()));
            // Restar el descuento al total
            BigDecimal descuento = comprobantesV.getDescuento();
            BigDecimal totalConDescuento = total.subtract(descuento);  // Resta el descuento del total

            // Crear el objeto con los valores calculados
            VentaResponseXReporteDet ventaXResponseXReporteDet = VentaResponseXReporteDet.builder()
                    .numero(comprobantesV.getNumero())
                    .descripcion(comprobantesV.getDescripcion())
                    .peso(comprobantesV.getPeso())
                    .cantidad(comprobantesV.getCantidad())
                    .precioUnitario(comprobantesV.getPrecioUnitario())
                    .total(totalConDescuento)  // total sin descuento
                    .descuento(descuento)  // Asignar el descuento
                    .build();
            listVentaXResponseDet.add(ventaXResponseXReporteDet);
        });
        // Sumar el total con descuento de todos los detalles
        BigDecimal totalVenta = listVentaXResponseDet.stream()
                .map(VentaResponseXReporteDet::getTotal)  // Obtener el total con descuento por cada detalle
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // Sumar todos los totales

        // Sumar el total de descuentos de todos los detalles
        BigDecimal totalDescuento = listVentaXResponseDet.stream()
                .map(VentaResponseXReporteDet::getDescuento)  // Obtener el descuento por cada detalle
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // Sumar todos los descuentos

        comprobanteVenta.setImporteTotal(totalVenta);
        comprobanteVenta.setDescuentoTotal(totalDescuento);
        String moneda = comprobanteVentaCa.getCodigoMoneda().equals("SOL") ? "SOLES" : "DOLARES";
        comprobanteVenta.setTotalTexto("SON: " + numeroALetrasService.convertirNumeroALetras(totalVenta.doubleValue(), moneda));
        JRBeanCollectionDataSource cabeceraDataSource = new JRBeanCollectionDataSource(Collections.singletonList(comprobanteVenta));
        JRBeanCollectionDataSource detalleDataSource = new JRBeanCollectionDataSource(listVentaXResponseDet);
        // Parámetros del reporte
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("titulo", "Reporte de Comprobante de Venta");
        parametros.put("detalleDataSource", detalleDataSource);
        // Cargar el archivo .jasper
        try {
            InputStream reporteStream = this.getClass().getClassLoader().getResourceAsStream("reportes/TIKv6.jasper");
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
        }
    }
}
