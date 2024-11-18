package com.microservice.ventas.service.ccompras;

import com.microservice.ventas.client.EmpresaClient;
import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasCaDTO;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasCabDTO;
import com.microservice.ventas.controller.response.EntidadResponse;
import com.microservice.ventas.entity.ComprobantesComprasCaEntity;
import com.microservice.ventas.entity.especification.ComprobantesComprasEspecification;
import com.microservice.ventas.repository.IComprobanteCompraCaRepository;
import com.microservice.ventas.service.cventas.IComprobantesVentasService;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComprobantesComprasService implements IComprobantesComprasService {
    private final IComprobanteCompraCaRepository icomprobanteCompraCaRepository;
    private final ModelMapper modelMapper;
    private final EmpresaClient empresaClient;
    private final ComprobantesComprasEspecification comprobantesComprasEspecification;

    @Override
    public Page<ComprobantesComprasCaDTO> findAll(LocalDate fechaEmision, LocalDate fechaCreacion, String codigoTipo, String serie, String numero, String numeroDoc, String nombre, BigDecimal subtotal, BigDecimal impuesto, BigDecimal total, String monedaCodigo, Double tipoCambio, String estadoCodigo, Long idPuntoVenta, Pageable pageable) {
        Specification<ComprobantesComprasCaEntity> specification = comprobantesComprasEspecification.getComprobantesCompras(fechaEmision, fechaCreacion, codigoTipo, serie, numero, numeroDoc, nombre, subtotal, impuesto, total, monedaCodigo, tipoCambio, estadoCodigo, idPuntoVenta);
        Page<ComprobantesComprasCaEntity> page = icomprobanteCompraCaRepository.findAll(specification, pageable);
        List<Long> idProveedores = page.stream().map(ComprobantesComprasCaEntity::getIdProveedor).distinct().collect(Collectors.toList());

        // Hacer una sola petición en lote al microservicio de clientes para obtener la información
        List<EntidadResponse> clientes = empresaClient.getClientesByIds(idProveedores);
        Page<ComprobantesComprasCaDTO> pageDTO = page.map(entity -> {
            // Buscar la información del cliente desde la lista obtenida
            EntidadResponse cliente = clientes.stream()
                    .filter(c -> c.getId().equals(entity.getIdProveedor()))
                    .findFirst()
                    .orElse(null);
            // Convertir entidad a DTO usando ModelMapper
            ComprobantesComprasCaDTO dto = convertToDTO(entity);
            // Asignar la información del cliente (si la obtuviste)
            if (cliente != null) {
                dto.setNombreProveedor(cliente.getNombre());
                dto.setNroDocumentoProveedor(cliente.getNumeroDocumento());
            }
            return dto;
        });
        return page.map(comprobantesComprasCaEntity -> modelMapper.map(comprobantesComprasCaEntity, ComprobantesComprasCaDTO.class));
    }
    private ComprobantesComprasCaDTO convertToDTO(ComprobantesComprasCaEntity entity) {
        // Usa ModelMapper para mapear la entidad a DTO
        return modelMapper.map(entity, ComprobantesComprasCaDTO.class);
    }
    public Page<ComprobantesComprasCaEntity> findAllComprobantes(Pageable pageable) {
        try {
            return icomprobanteCompraCaRepository.findAll(
                    Specification
                            .where((root, query, cb) -> {
                                // Realizar fetch join para evitar N+1 queries
                                if (query.getResultType().equals(ComprobantesComprasCaEntity.class)) {
                                    root.fetch("comprobantesTiposEntity", JoinType.LEFT);
                                    root.fetch("comprobanteCompraEstadosEntity", JoinType.LEFT);
                                }
                                return null;
                            }),
                    pageable
            );
        } catch (Exception e) {
            log.error("Error al obtener los comprobantes de venta: {}", e.getMessage());
            throw new RuntimeException("Error al obtener los comprobantes de venta", e);
        }
    }
}
