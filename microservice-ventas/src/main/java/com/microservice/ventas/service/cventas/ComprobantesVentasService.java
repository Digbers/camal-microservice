package com.microservice.ventas.service.cventas;

import com.microservice.ventas.client.EmpresaClient;
import com.microservice.ventas.controller.DTO.ClienteDTO;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasCabDTO;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasDetDTO;
import com.microservice.ventas.controller.response.EntidadResponse;
import com.microservice.ventas.entity.ComprobantesVentasCabEntity;
import com.microservice.ventas.entity.ComprobantesVentasDetEntity;
import com.microservice.ventas.entity.especification.ComprobantesVentasEspecification;
import com.microservice.ventas.repository.IcomprobantesVentasCabRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComprobantesVentasService implements IComprobantesVentasService {
    private final ComprobantesVentasEspecification comprobantesVentasEspecification;
    private final IcomprobantesVentasCabRepository icomprobantesVentasCabRepository;
    private final EmpresaClient empresaClient;
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMappings() {
        modelMapper.typeMap(ComprobantesVentasCabEntity.class, ComprobantesVentasCabDTO.class).addMappings(mapper -> {
            mapper.map(ComprobantesVentasCabEntity::getComprobantesVentasEstadoEntity, ComprobantesVentasCabDTO::setComprobantesVentaEstado);
        });
    }

    @Override
    public Page<ComprobantesVentasCabDTO> findALL(LocalDate fechaEmision, LocalDate fechaVencimiento, String codigoTipo, String serie, String numero, String numeroDoc, String nombre, BigDecimal subtotal, BigDecimal impuesto, BigDecimal total, String monedaCodigo, String estadoCodigo, Long idPuntoVenta, String sunat, Pageable pageable) {
        // Usar la especificación para filtrar los resultados
        Specification<ComprobantesVentasCabEntity> specification = comprobantesVentasEspecification.getComprobantesVentas(
                fechaEmision, fechaVencimiento, codigoTipo, serie, numero, numeroDoc, nombre, subtotal, impuesto, total, monedaCodigo, estadoCodigo, idPuntoVenta, sunat);

        // Aplicar la especificación en el repositorio
        Page<ComprobantesVentasCabEntity> page = icomprobantesVentasCabRepository.findAll(specification, pageable);

        // Obtener los ids de los clientes del resultado
        List<Long> idClientes = page.stream()
                .map(ComprobantesVentasCabEntity::getIdCliente)
                .distinct()
                .collect(Collectors.toList());
        // Hacer una sola petición en lote al microservicio de clientes para obtener la información
        List<EntidadResponse> clientes = empresaClient.getClientesByIds(idClientes);

        // Convertir los resultados a DTO y asignar la información del cliente
        Page<ComprobantesVentasCabDTO> pageDTO = page.map(entity -> {
            // Buscar la información del cliente desde la lista obtenida
            EntidadResponse cliente = clientes.stream()
                    .filter(c -> c.getId().equals(entity.getIdCliente()))
                    .findFirst()
                    .orElse(null);
            // Convertir entidad a DTO usando ModelMapper
            ComprobantesVentasCabDTO dto = convertToDTO(entity);
            // Asignar la información del cliente (si la obtuviste)
            if (cliente != null) {
                dto.setNombreCliente(cliente.getNombre());
                dto.setNumeroDocumentoCliente(cliente.getNumeroDocumento());
            }
            AtomicReference<BigDecimal> montoTotal = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> descuentoTotal = new AtomicReference<>(BigDecimal.ZERO);
            BigDecimal impuestoOfTotal = BigDecimal.ZERO;
            BigDecimal subtotalOfTotal = BigDecimal.ZERO;
            entity.getComprobantesVentasDetEntity().forEach(comprobantesV -> {
                BigDecimal pesoNeto = comprobantesV.getPeso().subtract(comprobantesV.getTara());
                BigDecimal subtotal1 = pesoNeto.multiply(comprobantesV.getPrecioUnitario());
                BigDecimal descuento = comprobantesV.getDescuento();

                montoTotal.updateAndGet(valorActual -> valorActual.add(subtotal1.subtract(descuento)));
                descuentoTotal.updateAndGet(valorActual -> valorActual.add(descuento));
            });
            impuestoOfTotal = montoTotal.get().multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
            subtotalOfTotal = montoTotal.get().subtract(impuestoOfTotal);
            dto.setSubtotal(subtotalOfTotal);
            dto.setImpuesto(impuestoOfTotal);
            dto.setTotal(montoTotal.get());
            return dto;
        });

        return pageDTO;
    }

    @Override
    public List<ComprobantesVentasDetDTO> findDetalleById(Long id) {
        try{
            ComprobantesVentasCabEntity comprobante = icomprobantesVentasCabRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro el comprobante con el ID: " + id));
            List<ComprobantesVentasDetEntity> listDet = comprobante.getComprobantesVentasDetEntity();
            return listDet.stream().map(comprobantesVentasDetEntity -> modelMapper.map(comprobantesVentasDetEntity, ComprobantesVentasDetDTO.class)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener los detalles de comprobantes de venta: {}", e.getMessage());
            throw new RuntimeException("Error al obtener los detalles de comprobantes de venta", e);
        }
    }

    // Método para convertir la entidad en DTO usando ModelMapper
    private ComprobantesVentasCabDTO convertToDTO(ComprobantesVentasCabEntity entity) {
        return modelMapper.map(entity, ComprobantesVentasCabDTO.class);
    }



    // Método auxiliar que puede ser utilizado por el controller para calcular el total
    public BigDecimal calculateTotal(ComprobantesVentasCabEntity comprobante) {
        return comprobante.getComprobantesVentasDetEntity().stream()
                .map(detalle -> {
                    // Multiplicar precio unitario por peso para cada detalle
                    BigDecimal precioUnitario = detalle.getPrecioUnitario() != null ?
                            detalle.getPrecioUnitario() : BigDecimal.ZERO;
                    BigDecimal peso = detalle.getPeso() != null ?
                            detalle.getPeso() : BigDecimal.ZERO;

                    return precioUnitario.multiply(peso);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
