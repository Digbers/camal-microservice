package com.camal.microservice_finanzas.service.mantenimiento;

import com.camal.microservice_finanzas.clients.EmpresaClient;
import com.camal.microservice_finanzas.controller.DTO.FormasCobrosDTO;
import com.camal.microservice_finanzas.controller.DTO.FormasDeCobrosDTO;
import com.camal.microservice_finanzas.exception.EmpresaNotFoundException;
import com.camal.microservice_finanzas.persistence.entity.FormasCobrosEntity;
import com.camal.microservice_finanzas.persistence.entity.MonedasEntity;
import com.camal.microservice_finanzas.persistence.repository.IFormasCobrosRepository;
import com.camal.microservice_finanzas.persistence.repository.IMonedasRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FormasCobrosServiceImpl implements IFormasCobrosService{

    private static final Logger log = LoggerFactory.getLogger(FormasCobrosServiceImpl.class);
    private final IFormasCobrosRepository formasCobrosRepository;
    private final EmpresaClient empresaClient;
    private final ModelMapper modelMapper;
    private final IMonedasRepository monedasRepository;

    @Override
    public FormasDeCobrosDTO save(FormasDeCobrosDTO formasCobrosDTO) {
        Boolean existeEmpresa = empresaClient.verificarEmpresaExiste(formasCobrosDTO.getIdEmpresa());
        if (!existeEmpresa) {
            throw new EmpresaNotFoundException("La empresa no existe");
        }
        FormasCobrosEntity formasCobros = modelMapper.map(formasCobrosDTO, FormasCobrosEntity.class);
        formasCobrosRepository.save(formasCobros);
        return modelMapper.map(formasCobros, FormasDeCobrosDTO.class);
    }

    @Override
    public FormasDeCobrosDTO findById(Long id) {
        try {
            FormasCobrosEntity formasCobros = formasCobrosRepository.findById(id).orElseThrow(() -> new RuntimeException("Forma de cobro no encontrada"));
            return modelMapper.map(formasCobros, FormasDeCobrosDTO.class);
        }catch (Exception e){
            throw new RuntimeException("Error al obtener forma de cobro");
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Optional<FormasCobrosEntity> formasCobros = formasCobrosRepository.findById(id);
        if (formasCobros.isEmpty()) {
            return false;
        }
        formasCobrosRepository.delete(formasCobros.get());
        return true;
    }

    @Override
    public FormasDeCobrosDTO update(Long id, FormasDeCobrosDTO formasCobrosDTO) {
        FormasCobrosEntity formasCobros = formasCobrosRepository.findById(id).orElseThrow(() -> new RuntimeException("Forma de cobro no encontrada"));
        formasCobros.setCodigo(formasCobrosDTO.getCodigo());
        formasCobros.setDescripcion(formasCobrosDTO.getDescripcion());
        formasCobros.setIdEmpresa(formasCobrosDTO.getIdEmpresa());
        MonedasEntity monedas = monedasRepository.findById(formasCobrosDTO.getMoneda().getId()).orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
        formasCobros.setMoneda(monedas);
        formasCobros.setUsuarioCreacion(formasCobrosDTO.getUsuarioCreacion());
        formasCobros.setFechaCreacion(formasCobrosDTO.getFechaCreacion());
        formasCobros.setUsuarioActualizacion(formasCobrosDTO.getUsuarioActualizacion());
        formasCobros.setFechaActualizacion(formasCobrosDTO.getFechaActualizacion());
        formasCobrosRepository.save(formasCobros);
        return modelMapper.map(formasCobros, FormasDeCobrosDTO.class);
    }

    @Override
    public List<FormasDeCobrosDTO> findByIdEmpresa(Long idEmpresa) {
        try {
            return formasCobrosRepository.findByIdEmpresa(idEmpresa).stream()
                    .map(formasCobros -> modelMapper.map(formasCobros, FormasDeCobrosDTO.class))
                    .collect(Collectors.toList());
        }catch (Exception e){
            log.info("Ocurrio un error al buscar las formas de cobors para la empresa :  "+ idEmpresa);
            throw new RuntimeException("Ocurrio un error al buscar las formas de cobros para la empresa : "+ idEmpresa);
        }

    }

    @Override
    public List<FormasDeCobrosDTO> findAll() {
        return formasCobrosRepository.findAll().stream()
                .map(formasCobros -> modelMapper.map(formasCobros, FormasDeCobrosDTO.class))
                .collect(Collectors.toList());
    }
}
