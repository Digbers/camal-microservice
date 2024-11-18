package com.microservice.empresas.config;

import com.microservice.empresas.controller.dto.DocumentosTiposDTO;
import com.microservice.empresas.controller.dto.EmpresaDTO;
import com.microservice.empresas.controller.dto.EntidadDTO;
import com.microservice.empresas.controller.dto.EntidadesTiposDTO;
import com.microservice.empresas.persistence.entity.DocumentoTiposEntity;
import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.persistence.entity.EntidadEntity;
import com.microservice.empresas.persistence.entity.EntidadesTiposEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Configuración general
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // Mapeo de DocumentoTiposEntity a DocumentosTiposDTO
        modelMapper.createTypeMap(DocumentoTiposEntity.class, DocumentosTiposDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getEmpresa().getId(), DocumentosTiposDTO::setEmpresa);
                    mapper.map(src -> src.getDocCodigo(), DocumentosTiposDTO::setDocCodigo);
                    mapper.map(DocumentoTiposEntity::getDescripcion, DocumentosTiposDTO::setDescripcion);
                    mapper.map(DocumentoTiposEntity::getCodigoSunat, DocumentosTiposDTO::setCodigoSunat);
                    mapper.map(DocumentoTiposEntity::getUsuarioCreacion, DocumentosTiposDTO::setUsuarioCreacion);
                    mapper.map(DocumentoTiposEntity::getFechaCreacion, DocumentosTiposDTO::setFechaCreacion);
                    mapper.map(DocumentoTiposEntity::getUsuarioActualizacion, DocumentosTiposDTO::setUsuarioActualizacion);
                    mapper.map(DocumentoTiposEntity::getFechaActualizacion, DocumentosTiposDTO::setFechaActualizacion);
                });
        // Mapeo de EntidadesTiposEntity a EntidadesTiposDTO
        modelMapper.createTypeMap(EntidadesTiposEntity.class, EntidadesTiposDTO.class)
                .addMappings(mapper -> {
                    mapper.map(EntidadesTiposEntity::getId, EntidadesTiposDTO::setId);
                    mapper.map(src -> src.getEmpresa().getId(), EntidadesTiposDTO::setEmpresa);
                    mapper.map(EntidadesTiposEntity::getTipoCodigo, EntidadesTiposDTO::setTipoCodigo);
                    mapper.map(EntidadesTiposEntity::getDescripcion, EntidadesTiposDTO::setDescripcion);
                    mapper.map(EntidadesTiposEntity::getUsuarioCreacion, EntidadesTiposDTO::setUsuarioCreacion);
                    mapper.map(EntidadesTiposEntity::getFechaCreacion, EntidadesTiposDTO::setFechaCreacion);
                    mapper.map(EntidadesTiposEntity::getUsuarioActualizacion, EntidadesTiposDTO::setUsuarioActualizacion);
                    mapper.map(EntidadesTiposEntity::getFechaActualizacion, EntidadesTiposDTO::setFechaActualizacion);
                });
        // Mapeo de EntidadEntity a EntidadDTO
        modelMapper.createTypeMap(EntidadEntity.class, EntidadDTO.class)
                .addMappings(mapper -> {
                    // Mapeo de empresa.id a idEmpresa
                    mapper.map(src -> src.getEmpresa().getId(), EntidadDTO::setIdEmpresa);

                    // Mapeo de zona.id a zona
                    mapper.map(src -> src.getZona().getId(), EntidadDTO::setZona);

                    // Mapeo directo de campos simples
                    mapper.map(EntidadEntity::getNombre, EntidadDTO::setNombre);
                    mapper.map(EntidadEntity::getApellidoPaterno, EntidadDTO::setApellidoPaterno);
                    mapper.map(EntidadEntity::getApellidoMaterno, EntidadDTO::setApellidoMaterno);
                    mapper.map(EntidadEntity::getNroDocumento, EntidadDTO::setNroDocumento);
                    mapper.map(EntidadEntity::getEmail, EntidadDTO::setEmail);
                    mapper.map(EntidadEntity::getCelular, EntidadDTO::setCelular);
                    mapper.map(EntidadEntity::getDireccion, EntidadDTO::setDireccion);
                    mapper.map(EntidadEntity::getSexo, EntidadDTO::setSexo);
                    mapper.map(EntidadEntity::getEstado, EntidadDTO::setEstado);
                    mapper.map(EntidadEntity::getCondicion, EntidadDTO::setCondicion);
                    mapper.map(EntidadEntity::getUsuarioCreacion, EntidadDTO::setUsuarioCreacion);
                    mapper.map(EntidadEntity::getFechaCreacion, EntidadDTO::setFechaCreacion);
                    mapper.map(EntidadEntity::getUsuarioActualizacion, EntidadDTO::setUsuarioActualizacion);
                    mapper.map(EntidadEntity::getFechaActualizacion, EntidadDTO::setFechaActualizacion);

                    mapper.map(src -> src.getDocumentoTipo().getDocCodigo(), EntidadDTO::setDocumentoTipo);

                    // Mapeo de entidadesTiposList
                    mapper.using(ctx -> {
                        List<EntidadesTiposEntity> source = (List<EntidadesTiposEntity>) ctx.getSource();
                        if (source == null) return null;
                        return source.stream()
                                .map(entity -> entity.getTipoCodigo()).collect(Collectors.toList());
                    }).map(EntidadEntity::getEntidadesTiposList, EntidadDTO::setEntidadesTiposList);
                });

        return modelMapper;
    }
}

