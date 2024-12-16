package com.microservice.inventario.service.movimientos;

import com.microservice.inventario.controller.DTO.MovimientosCabeceraDTO;
import com.microservice.inventario.controller.DTO.MovimientosDetalleDTO;
import com.microservice.inventario.controller.DTO.MovimientosEstadosDTO;
import com.microservice.inventario.controller.DTO.MovimientosMotivosDTO;
import com.microservice.inventario.persistence.entity.MovimientosCabeceraEntity;
import com.microservice.inventario.persistence.entity.MovimientosDetallesEntity;
import com.microservice.inventario.persistence.entity.MovimientosEstadosEntity;
import com.microservice.inventario.persistence.entity.MovimientosMotivosEntity;
import com.microservice.inventario.persistence.especification.MovimientosEspecification;
import com.microservice.inventario.persistence.repository.IMovimientosCabeceraRepository;
import com.microservice.inventario.persistence.repository.IMovimientosEstadosRepository;
import com.microservice.inventario.persistence.repository.IMovimientosMotivosRepository;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovimientosService implements IMovimientosService {
    private final IMovimientosCabeceraRepository movimientosCabeceraRepository;
    private final ModelMapper modelMapper;
    private final IMovimientosMotivosRepository movimientosMotivosRepository;
    private final IMovimientosEstadosRepository movimientosEstadosRepository;

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

    @Override
    public List<MovimientosDetalleDTO> findByIdMovimiento(Long id) {
        try {
            MovimientosCabeceraEntity movimiento = movimientosCabeceraRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Movimiento no encontrado"));
            List<MovimientosDetallesEntity> movimientosDetalles = movimiento.getMovimientosDetallesEntity();
            return movimientosDetalles.stream().map(movimientoDetalle -> {
                MovimientosDetalleDTO movimientoDetalleDTO = modelMapper.map(movimientoDetalle, MovimientosDetalleDTO.class);
                return movimientoDetalleDTO;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener movimientos: " + e.getMessage());
            throw new RuntimeException("Error al obtener movimientos: " + e.getMessage());
        }
    }

    @Override
    public List<MovimientosMotivosDTO> findAllMotivos() {
        try {
            List<MovimientosMotivosEntity> movimientosMotivos = movimientosMotivosRepository.findAll();
            return movimientosMotivos.stream().map(movimientosMotivo -> {
                MovimientosMotivosDTO movimientosMotivoDTO = modelMapper.map(movimientosMotivo, MovimientosMotivosDTO.class);
                return movimientosMotivoDTO;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener movimientos: " + e.getMessage());
            throw new RuntimeException("Error al obtener movimientos: " + e.getMessage());
        }
    }

    @Override
    public List<MovimientosEstadosDTO> findAllEstados() {
        try {
            List<MovimientosEstadosEntity> movimientosEstados = movimientosEstadosRepository.findAll();
            return movimientosEstados.stream().map(movimientosEstado -> {
                MovimientosEstadosDTO movimientosEstadoDTO = modelMapper.map(movimientosEstado, MovimientosEstadosDTO.class);
                return movimientosEstadoDTO;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener movimientos: " + e.getMessage());
            throw new RuntimeException("Error al obtener movimientos: " + e.getMessage());
        }
    }
}
