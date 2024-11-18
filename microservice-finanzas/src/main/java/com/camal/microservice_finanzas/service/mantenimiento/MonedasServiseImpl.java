package com.camal.microservice_finanzas.service.mantenimiento;

import com.camal.microservice_finanzas.clients.EmpresaClient;
import com.camal.microservice_finanzas.controller.DTO.MonedasDTO;
import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import com.camal.microservice_finanzas.persistence.especification.MonedasEspecifications;
import com.camal.microservice_finanzas.persistence.repository.IMonedasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonedasServiseImpl implements IMonedasService{

    private final IMonedasRepository monedasRepository;
    private final EmpresaClient empresaClient;
    private final ModelMapper modelMapper;

    @Override
    public MonedasDTO save(MonedasDTO monedasDTO) {
        MonedasEntity monedas = modelMapper.map(monedasDTO, MonedasEntity.class);
        monedasRepository.save(monedas);
        return modelMapper.map(monedas, MonedasDTO.class);
    }


    @Override
    public boolean deleteById(Long id) {
        Optional<MonedasEntity> monedas = monedasRepository.findById(id);
        if (monedas.isEmpty()) {
            return false;
        }
        monedasRepository.delete(monedas.get());
        return true;
    }

    @Override
    public MonedasDTO update(Long id, MonedasDTO monedasDTO) {
        MonedasEntity monedas = monedasRepository.findById(id).orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
        monedas.setCodigo(monedasDTO.getCodigo());
        monedas.setIdEmpresa(monedasDTO.getIdEmpresa());
        monedas.setNombre(monedasDTO.getNombre());
        monedas.setSimbolo(monedasDTO.getSimbolo());
        monedas.setUsuarioCreacion(monedasDTO.getUsuarioCreacion());
        monedas.setFechaCreacion(monedasDTO.getFechaCreacion());
        monedas.setUsuarioActualizacion(monedasDTO.getUsuarioActualizacion());
        monedas.setFechaActualizacion(monedasDTO.getFechaActualizacion());
        monedasRepository.save(monedas);
        return modelMapper.map(monedas, MonedasDTO.class);
    }

    @Override
    public MonedasDTO findById(Long id) {
        MonedasEntity monedas = monedasRepository.findById(id).orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
        return modelMapper.map(monedas, MonedasDTO.class);
    }

    @Override
    public List<MonedasDTO> findByIdEmpresa(Long idEmpresa) {
        try {
            return monedasRepository.findByIdEmpresa(idEmpresa).stream()
                    .map(monedas -> modelMapper.map(monedas, MonedasDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error al obtener monedas");
        }
    }

    @Override
    public List<MonedasDTO> findAll() {
        return monedasRepository.findAll().stream()
                .map(monedas -> modelMapper.map(monedas, MonedasDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<MonedasDTO> findAll(String codigo, String nombre, String simbolo, Pageable pageable, Long idEmpresa) {
        Specification<MonedasEntity> specification = MonedasEspecifications.getMonedas(codigo, nombre, simbolo, idEmpresa);
        return monedasRepository.findAll(specification, pageable).map(monedas -> modelMapper.map(monedas, MonedasDTO.class));
    }
}
