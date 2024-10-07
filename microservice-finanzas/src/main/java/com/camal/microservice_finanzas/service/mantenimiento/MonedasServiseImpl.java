package com.camal.microservice_finanzas.service.mantenimiento;

import com.camal.microservice_finanzas.clients.EmpresaClient;
import com.camal.microservice_finanzas.controller.DTO.MonedasDTO;
import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import com.camal.microservice_finanzas.persistence.repository.IMonedasRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonedasServiseImpl implements IMonedasService{

    private final IMonedasRepository monedasRepository;
    private final EmpresaClient empresaClient;
    private final ModelMapper modelMapper;

    @Override
    public MonedasDTO save(MonedasDTO monedasDTO) {
        Boolean existeEmpresa = empresaClient.verificarEmpresaExiste(monedasDTO.getIdEmpresa());
        if (!existeEmpresa) {
            throw new RuntimeException("La empresa no existe");
        }
        MonedasEntity monedas = modelMapper.map(monedasDTO, MonedasEntity.class);
        monedasRepository.save(monedas);
        return modelMapper.map(monedas, MonedasDTO.class);
    }


    @Override
    public boolean deleteById(String id) {
        Optional<MonedasEntity> monedas = monedasRepository.findById(id);
        if (monedas.isEmpty()) {
            return false;
        }
        monedasRepository.delete(monedas.get());
        return true;
    }

    @Override
    public MonedasDTO update(String id, MonedasDTO monedasDTO) {
        boolean existeEmpresa = empresaClient.verificarEmpresaExiste(monedasDTO.getIdEmpresa());
        if (!existeEmpresa) {
            throw new RuntimeException("La empresa no existe");
        }
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
    public MonedasDTO findById(String id) {
        MonedasEntity monedas = monedasRepository.findById(id).orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
        return modelMapper.map(monedas, MonedasDTO.class);
    }

    @Override
    public List<MonedasDTO> findByIdEmpresa(Long idEmpresa) {
        return monedasRepository.findByIdEmpresa(idEmpresa).stream()
                .map(monedas -> modelMapper.map(monedas, MonedasDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MonedasDTO> findAll() {
        return monedasRepository.findAll().stream()
                .map(monedas -> modelMapper.map(monedas, MonedasDTO.class))
                .collect(Collectors.toList());
    }
}
