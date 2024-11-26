package com.camal.microservice_finanzas.service.ventas;

import com.camal.microservice_finanzas.clients.VentasClient;
import com.camal.microservice_finanzas.controller.DTO.ventas.CuentasXCobrarDTO;
import com.camal.microservice_finanzas.controller.response.ComprobanteVentaResponseDTO;
import com.camal.microservice_finanzas.persistence.repository.IComprobantesVentasCobrosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CuentasXCobrarService implements ICuentasXCobrarService{
    private final VentasClient ventasClient;
    private final IComprobantesVentasCobrosRepository comprobantesVentasCobrosRepository;

    @Override
    public Page<CuentasXCobrarDTO> findAll(String comprobanteTipo, String serie, String numero, String numeroDoc, String nombre, String monedaCodigo, BigDecimal total, BigDecimal pagado, BigDecimal saldo, Pageable pageable) {
        try {
            // 1. Obtener comprobantes paginados del servicio de ventas
            Page<ComprobanteVentaResponseDTO> comprobantesPage = ventasClient.getComprobantesFromVentas(comprobanteTipo, serie, numero, numeroDoc, nombre, monedaCodigo, total, pagado, saldo, pageable);
            if (comprobantesPage.isEmpty()) {
                return Page.empty(pageable);
            }
            // 2. Obtener los cobros para los comprobantes de la página actual
            List<Long> idsComprobantes = comprobantesPage.getContent().stream()
                    .map(ComprobanteVentaResponseDTO::getId)
                    .collect(Collectors.toList());

            Map<Long, BigDecimal> cobrosPorComprobante = comprobantesVentasCobrosRepository
                    .findTotalCobrosPorComprobante(idsComprobantes);
            // 3. Construir DTOs con la información combinada
            List<CuentasXCobrarDTO> cuentasXCobrar = comprobantesPage.getContent().stream()
                    .map(comp -> buildCuentasXCobrarDTO(comp, cobrosPorComprobante))
                    .filter(cuenta -> cumpleFiltros(cuenta,
                            comprobanteTipo, serie, numero, numeroDoc,
                            nombre, monedaCodigo, total, pagado, saldo))
                    .collect(Collectors.toList());
            // 4. Crear nueva página con los resultados filtrados
            return new PageImpl<>(
                    cuentasXCobrar,
                    pageable,
                    comprobantesPage.getTotalElements()
            );

        } catch (WebClientResponseException e) {
            log.error("Error al obtener comprobantes de ventas: {}", e.getMessage());
            throw new RuntimeException("Error al comunicarse con el servicio de ventas", e);
        }
    }
    private CuentasXCobrarDTO buildCuentasXCobrarDTO(
            ComprobanteVentaResponseDTO comprobante,
            Map<Long, BigDecimal> cobrosPorComprobante) {

        BigDecimal pagado = cobrosPorComprobante
                .getOrDefault(comprobante.getId(), BigDecimal.ZERO);

        return CuentasXCobrarDTO.builder()
                .idComprobanteVenta(comprobante.getId())
                .comprobanteTipo(comprobante.getTipoComprobante())
                .serie(comprobante.getSerie())
                .fechaEmision(comprobante.getFechaEmision())
                .numero(comprobante.getNumero())
                .numeroDoc(comprobante.getNumeroDocumentoCliente())
                .nombre(comprobante.getNombreCliente())
                .monedaCodigo(comprobante.getCodigoMoneda())
                .total(comprobante.getTotal())
                .pagado(pagado)
                .saldo(comprobante.getTotal().subtract(pagado))
                .build();
    }

    private boolean cumpleFiltros(
            CuentasXCobrarDTO cuenta,
            String comprobanteTipo,
            String serie,
            String numero,
            String numeroDoc,
            String nombre,
            String monedaCodigo,
            BigDecimal total,
            BigDecimal pagado,
            BigDecimal saldo) {

        return (comprobanteTipo == null || cuenta.getComprobanteTipo().contains(comprobanteTipo)) &&
                (serie == null || cuenta.getSerie().contains(serie)) &&
                (numero == null || cuenta.getNumero().contains(numero)) &&
                (numeroDoc == null || cuenta.getNumeroDoc().contains(numeroDoc)) &&
                (nombre == null || cuenta.getNombre().toLowerCase().contains(nombre.toLowerCase())) &&
                (monedaCodigo == null || cuenta.getMonedaCodigo().equals(monedaCodigo)) &&
                (total == null || cuenta.getTotal().compareTo(total) == 0) &&
                (pagado == null || cuenta.getPagado().compareTo(pagado) == 0) &&
                (saldo == null || cuenta.getSaldo().compareTo(saldo) == 0);
    }
}
