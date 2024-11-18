package com.microservice.ventas.service.cventas;

import com.microservice.ventas.client.EmpresaClient;
import com.microservice.ventas.controller.DTO.ClienteDTO;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasCabDTO;
import com.microservice.ventas.controller.response.EntidadResponse;
import com.microservice.ventas.entity.ComprobantesVentasCabEntity;
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
import java.time.LocalDate;
import java.util.List;
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

            return dto;
        });

        return pageDTO;
    }

    // Método para convertir la entidad en DTO usando ModelMapper
    private ComprobantesVentasCabDTO convertToDTO(ComprobantesVentasCabEntity entity) {
        return modelMapper.map(entity, ComprobantesVentasCabDTO.class);
    }
    public Page<ComprobantesVentasCabEntity> findAllComprobantes(
            String comprobanteTipo, String serie, String numero, String numeroDoc, String nombre,
            String monedaCodigo, BigDecimal total, BigDecimal pagado, BigDecimal saldo, Pageable pageable) {
        try {
            // Crear una especificación que combine todos los filtros opcionales
            Specification<ComprobantesVentasCabEntity> specification = Specification.where((root, query, cb) -> {
                // Realizar fetch join para evitar N+1 queries
                if (query.getResultType().equals(ComprobantesVentasCabEntity.class)) {
                    root.fetch("comprobantesTiposEntity", JoinType.LEFT);
                    root.fetch("comprobantesVentasDetEntity", JoinType.LEFT);
                }
                return cb.conjunction(); // Punto de inicio de la especificación
            });
            // Añadir condiciones dinámicas para cada filtro opcional
            if (comprobanteTipo != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("comprobantesTiposEntity").get("codigo"), comprobanteTipo));
            }
            if (serie != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("serie"), serie));
            }
            if (numero != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("numero"), numero));
            }
            if (numeroDoc != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("numeroDocumentoCliente"), numeroDoc));
            }
            if (nombre != null) {
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("nombreCliente")), "%" + nombre.toLowerCase() + "%"));
            }
            if (monedaCodigo != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("codigoMoneda"), monedaCodigo));
            }
            if (total != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.join("comprobantesVentasDetEntity").get("total"), total));
            }
            if (pagado != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.join("comprobantesVentasCuotas").get("pagado"), pagado));
            }
            if (saldo != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.join("comprobantesVentasCuotas").get("saldo"), saldo));
            }

            // Realizar la consulta con la especificación y paginación
            return icomprobantesVentasCabRepository.findAll(specification, pageable);

        } catch (Exception e) {
            log.error("Error al obtener los comprobantes de venta: {}", e.getMessage());
            throw new RuntimeException("Error al obtener los comprobantes de venta", e);
        }
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
