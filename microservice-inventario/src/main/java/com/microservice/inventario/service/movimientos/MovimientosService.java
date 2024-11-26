package com.microservice.inventario.service.movimientos;

import com.microservice.inventario.controller.DTO.MovimientosCabeceraDTO;
import com.microservice.inventario.persistence.entity.MovimientosCabeceraEntity;
import com.microservice.inventario.persistence.especification.MovimientosEspecification;
import com.microservice.inventario.persistence.repository.IMovimientosCabeceraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovimientosService implements IMovimientosService {
    private final IMovimientosCabeceraRepository movimientosCabeceraRepository;
    private final ModelMapper modelMapper;
    @Override
    public Page<MovimientosCabeceraDTO> finAll(Long id, Long idEmpresa, Long numero, LocalDate fechaEmision, BigDecimal total, String motivoCodigo, String idUsuario, String monedaCodigo, Pageable pageable) {
        try {
            Specification<MovimientosCabeceraEntity> specification = MovimientosEspecification.getMovimientos(id, idEmpresa, numero, fechaEmision, total, motivoCodigo, idUsuario, monedaCodigo);
           return movimientosCabeceraRepository.findAll(specification, pageable).map(movimientosCabecera -> {
                MovimientosCabeceraDTO dtoMovimientosCabecera = modelMapper.map(movimientosCabecera, MovimientosCabeceraDTO.class);
                return dtoMovimientosCabecera;
            });
        } catch (Exception e) {
            log.error("Error al obtener movimientos: " + e.getMessage());
            throw new RuntimeException("Error al obtener movimientos: " + e.getMessage());
        }
    }
}
