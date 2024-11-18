package com.microservice.inventario.service.puntoVentaI;

import com.microservice.inventario.controller.DTO.AlmacenDTO;
import com.microservice.inventario.controller.DTO.PuntoVentaDTO;
import com.microservice.inventario.controller.DTO.response.DatosGeneralesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IPuntoVentaService {
    Page<PuntoVentaDTO> findAllByEmpresa(String direccion, String nombre, Pageable pageable, Long idEmpresa);

    Optional<PuntoVentaDTO> findById(Long id);
    PuntoVentaDTO save(PuntoVentaDTO puntoVentaDTO);
    boolean deleteById(Long id);
    List<PuntoVentaDTO> findAllByIdAlmacen(Long id);
    DatosGeneralesResponse findDatosGenerales(Long idEmpresa, Long idAlmacen, Long idPuntoVenta);
}
