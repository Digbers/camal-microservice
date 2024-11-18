package com.camal.microservice_finanzas.service.mantenimiento;

import com.camal.microservice_finanzas.clients.EmpresaClient;
import com.camal.microservice_finanzas.controller.DTO.FormasPagosDTO;
import com.camal.microservice_finanzas.exception.EmpresaNotFoundException;
import com.camal.microservice_finanzas.persistence.entity.FormasPagosEntity;
import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import com.camal.microservice_finanzas.persistence.repository.IFormasPagosRepository;
import com.camal.microservice_finanzas.persistence.repository.IMonedasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormasPagosServiceImlp implements IFormasPagosService{
    private final IFormasPagosRepository formasPagosRepository;
    private final EmpresaClient empresaClient;
    private final ModelMapper modelMapper;
    private final IMonedasRepository monedasRepository;
    @Override
    public FormasPagosDTO save(FormasPagosDTO formasPagosDTO) {
        Boolean existeEmpresa = empresaClient.verificarEmpresaExiste(formasPagosDTO.getIdEmpresa());
        if (!existeEmpresa) {
            throw new EmpresaNotFoundException("La empresa no existe");
        }
        FormasPagosEntity formasPagos = modelMapper.map(formasPagosDTO, FormasPagosEntity.class);
        formasPagosRepository.save(formasPagos);
        return modelMapper.map(formasPagos, FormasPagosDTO.class);
    }

    @Override
    public FormasPagosDTO findById(Long id) {
        FormasPagosEntity formasPagos = formasPagosRepository.findById(id).orElseThrow(() -> new RuntimeException("Forma de pago no encontrada"));
        return modelMapper.map(formasPagos, FormasPagosDTO.class);
    }

    @Override
    public boolean deleteById(Long id) {
        Optional<FormasPagosEntity> formasPagos = formasPagosRepository.findById(id);
        if (formasPagos.isEmpty()) {
            return false;
        }
        formasPagosRepository.delete(formasPagos.get());
        return true;
    }

    @Override
    public FormasPagosDTO update(Long id, FormasPagosDTO formasPagosDTO) {
        boolean existeEmpresa = empresaClient.verificarEmpresaExiste(formasPagosDTO.getIdEmpresa());
        if (!existeEmpresa) {
            throw new EmpresaNotFoundException("La empresa no existe");
        }
        FormasPagosEntity formasPagos = formasPagosRepository.findById(id).orElseThrow(() -> new RuntimeException("Forma de pago no encontrada"));
        formasPagos.setCodigo(formasPagosDTO.getCodigo());
        formasPagos.setDescripcion(formasPagosDTO.getDescripcion());
        formasPagos.setIdEmpresa(formasPagosDTO.getIdEmpresa());
        MonedasEntity monedas = monedasRepository.findById(formasPagosDTO.getMoneda().getId()).orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
        formasPagos.setMoneda(monedas);
        formasPagos.setUsuarioCreacion(formasPagosDTO.getUsuarioCreacion());
        formasPagos.setFechaCreacion(formasPagosDTO.getFechaCreacion());
        formasPagos.setUsuarioActualizacion(formasPagosDTO.getUsuarioActualizacion());
        formasPagos.setFechaActualizacion(formasPagosDTO.getFechaActualizacion());
        formasPagosRepository.save(formasPagos);
        return modelMapper.map(formasPagos, FormasPagosDTO.class);
    }

    @Override
    public List<FormasPagosDTO> findByIdEmpresa(Long idEmpresa) {
        try {
            return formasPagosRepository.findByIdEmpresa(idEmpresa).stream()
                    .map(formasPagos -> modelMapper.map(formasPagos, FormasPagosDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener formas de pago por empresa", e);
            throw new RuntimeException("Error al obtener formas de pago por empresa");
        }
    }

    @Override
    public List<FormasPagosDTO> findAll() {
        return formasPagosRepository.findAll().stream()
                .map(formasPagos -> modelMapper.map(formasPagos, FormasPagosDTO.class))
                .collect(Collectors.toList());
    }
}
