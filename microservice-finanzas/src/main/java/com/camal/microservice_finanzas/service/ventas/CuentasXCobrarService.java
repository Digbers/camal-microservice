package com.camal.microservice_finanzas.service.ventas;

import com.camal.microservice_finanzas.clients.VentasClient;
import com.camal.microservice_finanzas.controller.DTO.ComprobantesVentasCobrosDTO;
import com.camal.microservice_finanzas.controller.DTO.ventas.ComprobantesVentasCabDTO;
import com.camal.microservice_finanzas.controller.DTO.ventas.CuentasXCobrarDTO;
import com.camal.microservice_finanzas.persistence.entity.ComprobantesVentasCobrosEntity;
import com.camal.microservice_finanzas.persistence.entity.FormasCobrosEntity;
import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import com.camal.microservice_finanzas.persistence.repository.IComprobantesVentasCobrosRepository;
import com.camal.microservice_finanzas.persistence.repository.IFormasCobrosRepository;
import com.camal.microservice_finanzas.persistence.repository.IMonedasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CuentasXCobrarService implements ICuentasXCobrarService{
    private final VentasClient ventasClient;
    private final IComprobantesVentasCobrosRepository comprobantesVentasCobrosRepository;
    private final ModelMapper modelMapper;
    private final IMonedasRepository monedasRepository;
    private final IFormasCobrosRepository formasCobrosRepository;

    @Override
    public Page<CuentasXCobrarDTO> findAll(Boolean inCobrados, LocalDate fechaEmision, String comprobanteTipo, String serie, String numero, String numeroDoc, String nombre, String monedaCodigo, BigDecimal total, BigDecimal cobrado, BigDecimal saldo, Pageable pageable) {
        try {
            // 1. Obtener comprobantes paginados del servicio de ventas
            Page<ComprobantesVentasCabDTO> comprobantesPage = ventasClient.getComprobantesFromVentas(fechaEmision, comprobanteTipo, serie, numero, numeroDoc, nombre, monedaCodigo, total, cobrado, saldo, pageable);
            if (comprobantesPage.isEmpty()) {
                return Page.empty(pageable);
            }
            // 2. Obtener los cobros para los comprobantes de la página actual
            List<Long> idsComprobantes = comprobantesPage.getContent().stream()
                    .map(ComprobantesVentasCabDTO::getId)
                    .collect(Collectors.toList());

            Map<Long, BigDecimal> cobrosPorComprobante = comprobantesVentasCobrosRepository
                    .findTotalCobrosPorComprobante(idsComprobantes);
            // 3. Construir DTOs con la información combinada
            List<CuentasXCobrarDTO> cuentasXCobrar = comprobantesPage.getContent().stream()
                    .map(comp -> buildCuentasXCobrarDTO(comp, cobrosPorComprobante))
                    .filter(cuenta ->
                            (cobrado == null || cuenta.getCobrado().compareTo(cobrado) == 0) &&
                                    (saldo == null || cuenta.getSaldo().compareTo(saldo) == 0) &&
                                    (inCobrados || (!inCobrados && cuenta.getSaldo().compareTo(BigDecimal.ZERO) != 0))
                    )
                    .collect(Collectors.toList());
            // 4. Crear nueva página con los resultados filtrados
            return new PageImpl<>(
                    cuentasXCobrar,
                    pageable,
                    comprobantesPage.getTotalElements()
            );

        } catch (WebClientResponseException e) {
            e.printStackTrace();
            log.error("Error al obtener comprobantes de ventas: {}", e.getMessage());
            throw new RuntimeException("Error al comunicarse con el servicio de ventas", e);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error al obtener comprobantes de ventas: {}", e.getMessage());
            throw new RuntimeException("Error al comunicarse con el servicio de ventas", e);
        }
    }

    @Override
    public List<ComprobantesVentasCobrosDTO> findCobrosByComprobante(Long idComprobante) {
        try {
            List<ComprobantesVentasCobrosEntity> comprobantesVentasCobros = comprobantesVentasCobrosRepository.findByIdComprobanteVenta(idComprobante);
            return comprobantesVentasCobros.stream().map(comprobantesVentasCobrosEntity -> modelMapper.map(comprobantesVentasCobrosEntity, ComprobantesVentasCobrosDTO.class)).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error al obtener cobros por comprobante: {}", e.getMessage());
            throw new RuntimeException("Error al comunicarse con el servicio de ventas", e);
        }
    }

    @Override
    public ComprobantesVentasCobrosDTO saveCobro(ComprobantesVentasCobrosDTO comprobantesVentasCobrosDTO) {
        try {
            MonedasEntity moneda = monedasRepository.findByIdEmpresaAndCodigo(comprobantesVentasCobrosDTO.getIdEmpresa(), comprobantesVentasCobrosDTO.getMonedas()).orElseThrow(() -> new RuntimeException("No se encontro moneda con codigo " + comprobantesVentasCobrosDTO.getMonedas()));
            FormasCobrosEntity forma = formasCobrosRepository.findByIdEmpresaAndCodigo(comprobantesVentasCobrosDTO.getIdEmpresa(), comprobantesVentasCobrosDTO.getFormasDeCobros()).orElseThrow(() -> new RuntimeException("No se encontro forma de cobro con codigo " + comprobantesVentasCobrosDTO.getFormasDeCobros()));
            ComprobantesVentasCobrosEntity comprobantesVentasCobrosEntity = modelMapper.map(comprobantesVentasCobrosDTO, ComprobantesVentasCobrosEntity.class);
            comprobantesVentasCobrosEntity.setFormasCobrosEntity(forma);
            comprobantesVentasCobrosEntity.setMonedasEntity(moneda);
            comprobantesVentasCobrosRepository.save(comprobantesVentasCobrosEntity);
            return modelMapper.map(comprobantesVentasCobrosEntity, ComprobantesVentasCobrosDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error al guardar cobro: {}", e.getMessage());
            throw new RuntimeException("Error al guardar cobro", e);
        }
    }

    @Override
    public ComprobantesVentasCobrosDTO updateCobro(Long id, ComprobantesVentasCobrosDTO comprobantesVentasCobrosDTO) {
        try {
            ComprobantesVentasCobrosEntity comprobanterCobro = comprobantesVentasCobrosRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro el cobro con el id " + id));
            //MonedasEntity moneda = monedasRepository.findByIdEmpresaAndCodigo(comprobantesVentasCobrosDTO.getIdEmpresa(), comprobantesVentasCobrosDTO.getMonedas()).orElseThrow(() -> new RuntimeException("No se encontro moneda con codigo " + comprobantesVentasCobrosDTO.getMonedas()));
            FormasCobrosEntity forma = formasCobrosRepository.findByIdEmpresaAndCodigo(comprobantesVentasCobrosDTO.getIdEmpresa(), comprobantesVentasCobrosDTO.getFormasDeCobros()).orElseThrow(() -> new RuntimeException("No se encontro forma de cobro con codigo " + comprobantesVentasCobrosDTO.getFormasDeCobros()));
            comprobanterCobro.setFormasCobrosEntity(forma);
            comprobanterCobro.setUsuarioActualizacion(comprobantesVentasCobrosDTO.getUsuarioActualizacion());
            comprobanterCobro.setMontoCobrado(comprobantesVentasCobrosDTO.getMontoCobrado());
            comprobanterCobro.setDescripcion(comprobantesVentasCobrosDTO.getDescripcion());
            comprobantesVentasCobrosRepository.save(comprobanterCobro);
            return modelMapper.map(comprobanterCobro, ComprobantesVentasCobrosDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error al guardar cobro: {}", e.getMessage());
            throw new RuntimeException("Error al guardar cobro", e);
        }
    }

    private CuentasXCobrarDTO buildCuentasXCobrarDTO(
            ComprobantesVentasCabDTO comprobante,
            Map<Long, BigDecimal> cobrosPorComprobante) {

        BigDecimal cobrado = cobrosPorComprobante
                .getOrDefault(comprobante.getId(), BigDecimal.ZERO);

        return CuentasXCobrarDTO.builder()
                .idComprobanteVenta(comprobante.getId())
                .comprobanteTipo(comprobante.getComprobantesTipos().getCodigo())
                .serie(comprobante.getSerie())
                .fechaEmision(comprobante.getFechaEmision())
                .numero(comprobante.getNumero())
                .numeroDoc(comprobante.getNumeroDocumentoCliente())
                .nombre(comprobante.getNombreCliente())
                .monedaCodigo(comprobante.getCodigoMoneda())
                .total(comprobante.getTotal())
                .cobrado(cobrado)
                .saldo(comprobante.getTotal().subtract(cobrado))
                .build();
    }
}
