package com.microservice.inventario.service.puntoVentaI;

import com.microservice.inventario.controller.DTO.AlmacenDTO;
import com.microservice.inventario.controller.DTO.PuntoVentaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IPuntoVentaService {
    Page<PuntoVentaDTO> findAll(Pageable pageable);

    Optional<PuntoVentaDTO> findById(Long id);
    PuntoVentaDTO save(PuntoVentaDTO puntoVentaDTO);
    boolean deleteById(Long id);
    List<PuntoVentaDTO> findAllByIdAlmacen(Long id);
}
