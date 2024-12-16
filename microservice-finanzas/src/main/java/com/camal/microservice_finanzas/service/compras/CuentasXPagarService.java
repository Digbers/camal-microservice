package com.camal.microservice_finanzas.service.compras;

import com.camal.microservice_finanzas.clients.VentasClient;
import com.camal.microservice_finanzas.controller.DTO.compras.ComprobantesComprasCaDTO;
import com.camal.microservice_finanzas.controller.DTO.compras.ComprobantesComprasPagosDTO;
import com.camal.microservice_finanzas.controller.DTO.compras.CuentasXPagarDTO;

import com.camal.microservice_finanzas.persistence.entity.ComprobantesComprasPagosEntity;
import com.camal.microservice_finanzas.persistence.entity.ComprobantesVentasCobrosEntity;
import com.camal.microservice_finanzas.persistence.entity.FormasPagosEntity;
import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import com.camal.microservice_finanzas.persistence.repository.IComprobantesComprasPagosRepository;

import com.camal.microservice_finanzas.persistence.repository.IFormasPagosRepository;
import com.camal.microservice_finanzas.persistence.repository.IMonedasRepository;
import jakarta.persistence.EntityNotFoundException;
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
public class CuentasXPagarService implements ICuentasXPagar{

    private final VentasClient ventasClient;
    private final IComprobantesComprasPagosRepository comprobantesComprasPagosRepository;
    private final ModelMapper modelMapper;
    private final IMonedasRepository monedasRepository;
    private final IFormasPagosRepository formasPagosRepository;

    @Override
    public Page<CuentasXPagarDTO> findAll(Boolean inPagado, LocalDate fechaEmision, String comprobanteTipo, String serie, String numero, String numeroDoc, String nombre, String monedaCodigo, BigDecimal total, BigDecimal pagado, BigDecimal saldo, Pageable pageable) {
        try {
            // 1. Obtener comprobantes paginados del servicio de ventas
            Page<ComprobantesComprasCaDTO> comprobantesPage = ventasClient.getComprobantesFromCompras(fechaEmision, comprobanteTipo, serie, numero, numeroDoc, nombre, monedaCodigo, total, pagado, saldo, pageable);
            if (comprobantesPage.isEmpty()) {
                return Page.empty(pageable);
            }
            // 2. Obtener los cobros para los comprobantes de la página actual
            List<Long> idsComprobantes = comprobantesPage.getContent().stream()
                    .map(ComprobantesComprasCaDTO::getId)
                    .collect(Collectors.toList());

            Map<Long, BigDecimal> cobrosPorComprobante = comprobantesComprasPagosRepository
                    .findTotalPagosPorComprobante(idsComprobantes);
            // 3. Construir DTOs con la información combinada
            List<CuentasXPagarDTO> cuentasXCobrar = comprobantesPage.getContent().stream()
                    .map(comp -> buildCuentasXPagarDTO(comp, cobrosPorComprobante))
                    .filter(cuenta -> // Filtrar solo por cobrado y saldo
                            (pagado == null || cuenta.getPagado().compareTo(pagado) == 0) &&
                                    (saldo == null || cuenta.getSaldo().compareTo(saldo) == 0) &&
                            (!inPagado || cuenta.getPagado().compareTo(BigDecimal.ZERO) == 0)
                    )
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

    @Override
    public List<ComprobantesComprasPagosDTO> findPagosByComprobante(Long idComprobante) {
        try {
            List<ComprobantesComprasPagosEntity> comprobantesComprasPagosEntities = comprobantesComprasPagosRepository.findByIdComprobanteCompra(idComprobante);
            return comprobantesComprasPagosEntities.stream().map(comprobantesComprasPagosEntity -> modelMapper.map(comprobantesComprasPagosEntity, ComprobantesComprasPagosDTO.class)).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error al obtener cobros por comprobante: {}", e.getMessage());
            throw new RuntimeException("Error al comunicarse con el servicio de ventas", e);
        }
    }

    @Override
    public ComprobantesComprasPagosDTO savePago(ComprobantesComprasPagosDTO comprobanteCompraP) {
        try{
            FormasPagosEntity forma = formasPagosRepository.findByIdEmpresaAndCodigo(comprobanteCompraP.getIdEmpresa(), comprobanteCompraP.getFormaPagosEntity()).orElseThrow(() -> new RuntimeException("No se encontro forma de pago con codigo " + comprobanteCompraP.getFormaPagosEntity()));
            MonedasEntity moneda = monedasRepository.findByIdEmpresaAndCodigo(comprobanteCompraP.getIdEmpresa(), comprobanteCompraP.getMonedasEntity()).orElseThrow(() -> new RuntimeException("No se encontro moneda con codigo " + comprobanteCompraP.getMonedasEntity()));
            ComprobantesComprasPagosEntity comprobantesComprasPagosEntity = modelMapper.map(comprobanteCompraP, ComprobantesComprasPagosEntity.class);
            comprobantesComprasPagosEntity.setFormaPagosEntity(forma);
            comprobantesComprasPagosEntity.setMonedasEntity(moneda);
            comprobantesComprasPagosRepository.save(comprobantesComprasPagosEntity);
            return modelMapper.map(comprobantesComprasPagosEntity, ComprobantesComprasPagosDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error al guardar cobro: {}", e.getMessage());
            throw new RuntimeException("Error al guardar cobro", e);
        }
    }
    @Override
    public ComprobantesComprasPagosDTO updatePago(Long id, ComprobantesComprasPagosDTO comprobantesComprasPagosDTO) {
        try{
            ComprobantesComprasPagosEntity comprobantesComprasPagosEntity = comprobantesComprasPagosRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No existe el comprobante con id " + id));
            FormasPagosEntity forma = formasPagosRepository.findByIdEmpresaAndCodigo(comprobantesComprasPagosDTO.getIdEmpresa(), comprobantesComprasPagosDTO.getFormaPagosEntity()).orElseThrow(() -> new RuntimeException("No se encontro forma de pago con codigo " + comprobantesComprasPagosDTO.getFormaPagosEntity()));
            comprobantesComprasPagosEntity.setMontoPagado(comprobantesComprasPagosDTO.getMontoPagado());
            comprobantesComprasPagosEntity.setFechaPago(comprobantesComprasPagosDTO.getFechaPago());
            comprobantesComprasPagosEntity.setDescripcion(comprobantesComprasPagosDTO.getDescripcion());
            comprobantesComprasPagosEntity.setIdComprobanteCompra(comprobantesComprasPagosDTO.getIdComprobanteCompra());
            comprobantesComprasPagosEntity.setUsuarioCreacion(comprobantesComprasPagosDTO.getUsuarioCreacion());
            comprobantesComprasPagosEntity.setFormaPagosEntity(forma);
            comprobantesComprasPagosRepository.save(comprobantesComprasPagosEntity);
            return modelMapper.map(comprobantesComprasPagosEntity, ComprobantesComprasPagosDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error al guardar cobro: {}", e.getMessage());
            throw new RuntimeException("Error al guardar cobro", e);
        }
    }

    private CuentasXPagarDTO buildCuentasXPagarDTO(
            ComprobantesComprasCaDTO comprobante,
            Map<Long, BigDecimal> cobrosPorComprobante) {

        BigDecimal pagado = cobrosPorComprobante
                .getOrDefault(comprobante.getId(), BigDecimal.ZERO);

        return CuentasXPagarDTO.builder()
                .idComprobanteVenta(comprobante.getId())
                .comprobanteTipo(comprobante.getComprobantesTipos().getCodigo())
                .serie(comprobante.getSerie())
                .fechaEmision(comprobante.getFechaEmision())
                .numero(comprobante.getNumero())
                .numeroDoc(comprobante.getNroDocumentoProveedor())
                .nombre(comprobante.getNombreProveedor())
                .monedaCodigo(comprobante.getCodigoMoneda())
                .total(comprobante.getTotal())
                .pagado(pagado)
                .saldo(comprobante.getTotal().subtract(pagado))
                .build();
    }
}
