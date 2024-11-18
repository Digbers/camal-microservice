package com.microservice.ventas.service.compras;

import com.microservice.ventas.controller.DTO.compras.ComprobantesComprasEstadosDTO;
import com.microservice.ventas.entity.ComprobantesComprasEstadosEntity;
import com.microservice.ventas.entity.especification.ComprobantesComprasEstadosEspecification;
import com.microservice.ventas.repository.IComprobantesCompraEstadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComprasEstadosService implements IComprasEstadosService {
    private final ModelMapper modelMapper;
    private final IComprobantesCompraEstadoRepository comprobantesCompraEstadoRepository;

    @Override
    public Page<ComprobantesComprasEstadosDTO> findAllByEmpresa(String codigo, String descripcion, Pageable pageable, Long idEmpresa) {
        try{
            Specification<ComprobantesComprasEstadosEntity> specification = ComprobantesComprasEstadosEspecification.getComprobantesComprasEstados(codigo, descripcion, idEmpresa);
            return comprobantesCompraEstadoRepository.findAll(specification, pageable).map(comprobantesCompraEstadoEntity -> modelMapper.map(comprobantesCompraEstadoEntity, ComprobantesComprasEstadosDTO.class));
        } catch (Exception e) {
            log.error("Error al obtener los estados de compra: " + e.getMessage());
            throw new RuntimeException("Error al obtener los estados de compra: " + e.getMessage());
        }
    }

    @Override
    public List<ComprobantesComprasEstadosDTO> findByIdEmpresa(Long idEmpresa) {
        try{
            List<ComprobantesComprasEstadosEntity> comprobatesEstados = comprobantesCompraEstadoRepository.findByIdEmpresa(idEmpresa);
            return comprobatesEstados.stream().map(comprobantesCompraEstadoEntity -> modelMapper.map(comprobantesCompraEstadoEntity, ComprobantesComprasEstadosDTO.class)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener los estados de compra: " + e.getMessage());
            throw new RuntimeException("Error al obtener los estados de compra: " + e.getMessage());
        }
    }
}
