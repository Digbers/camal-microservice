package com.microservice.empresas.service.impl;

import com.microservice.empresas.controller.dto.DocumentosTiposDTO;
import com.microservice.empresas.persistence.entity.DocumentoTiposEntity;
import com.microservice.empresas.persistence.entity.EmpresaEntity;
import com.microservice.empresas.persistence.especification.DocumentosTiposEspecification;
import com.microservice.empresas.persistence.repository.IDocumentosTiposRepository;
import com.microservice.empresas.persistence.repository.IEmpresaRepository;
import com.microservice.empresas.service.IDocumentosTiposService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentosTiposServiceImpl implements IDocumentosTiposService {

    private final IDocumentosTiposRepository documentosTiposRepository;
    private final IEmpresaRepository empresaRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<DocumentosTiposDTO> findAllByEmpresa(String docCodigo, String descripcion, String codigoSunat, Pageable pageable, Long idEmpresa) {
        try {
            Specification<DocumentoTiposEntity> specification = DocumentosTiposEspecification.getDocumentosTipos(docCodigo, descripcion, codigoSunat);
            return documentosTiposRepository.findAll(specification, pageable).map(documentosTipos -> modelMapper.map(documentosTipos, DocumentosTiposDTO.class));
        } catch (Exception e) {
            log.error("Error al obtener DocumentosTipos", e);
            throw new RuntimeException("Error al obtener DocumentosTipos");
        }
    }

    @Override
    public List<DocumentosTiposDTO> findAll() {
        try {
            List<DocumentoTiposEntity> documentosTipos = documentosTiposRepository.findAll();
            return documentosTipos.stream().map(documentosTiposEntity -> modelMapper.map(documentosTiposEntity, DocumentosTiposDTO.class)).toList();
        } catch (Exception e) {
            log.error("Error al obtener DocumentosTipos", e);
            throw new RuntimeException("Error al obtener DocumentosTipos");
        }
    }

    @Override
    public Optional<DocumentosTiposDTO> findById(String docCodigo, Long empresa) {
        try {
            DocumentoTiposEntity documentoTipos = documentosTiposRepository.findByEmpresaAndDocCodigo(empresa, docCodigo).orElseThrow(() -> new EntityNotFoundException("DocumentoTipos no encontrada con docCodigo: " + docCodigo + " y empresaId: " + empresa));
            return Optional.of(modelMapper.map(documentoTipos, DocumentosTiposDTO.class));
        } catch (NullPointerException ex) {
            throw new EntityNotFoundException("DocumentoTipos no encontrada con docCodigo: " + docCodigo + " y empresaId: " + empresa);
        }
    }

    @Override
    public DocumentosTiposDTO save(DocumentosTiposDTO documentosTiposDTO) {
        try {
            DocumentoTiposEntity documentoTiposEntity = modelMapper.map(documentosTiposDTO, DocumentoTiposEntity.class);
            EmpresaEntity empresa = empresaRepository.findById(documentosTiposDTO.getEmpresa()).orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con id: " + documentosTiposDTO.getEmpresa()));
            documentoTiposEntity.setEmpresa(empresa);
            return modelMapper.map(documentosTiposRepository.save(documentoTiposEntity), DocumentosTiposDTO.class);
        } catch (Exception e) {
            log.error("Error al guardar DocumentosTipos", e);
            throw new RuntimeException("Error al guardar DocumentosTipos");
        }
    }

    @Override
    public DocumentosTiposDTO update(Long id, DocumentosTiposDTO documentosTiposDTO) {
        try {
            DocumentoTiposEntity documentoExistente = documentosTiposRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DocumentoTipos no encontrada con id: " + id));
            EmpresaEntity empresa = empresaRepository.findById(documentosTiposDTO.getEmpresa()).orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con id: " + documentosTiposDTO.getEmpresa()));
            documentoExistente.setDocCodigo(documentosTiposDTO.getDocCodigo());
            documentoExistente.setEmpresa(empresa);
            documentoExistente.setCodigoSunat(documentosTiposDTO.getCodigoSunat());
            documentoExistente.setDescripcion(documentosTiposDTO.getDescripcion());
            documentoExistente.setUsuarioActualizacion(documentosTiposDTO.getUsuarioActualizacion());
            return modelMapper.map(documentosTiposRepository.save(documentoExistente), DocumentosTiposDTO.class);
        } catch (Exception e) {
            log.error("Error al actualizar DocumentosTipos", e);
            throw new RuntimeException("Error al actualizar DocumentosTipos");
        }
    }

    @Override
    public void deleteByIdOriginal(Long id) {
        try {
            DocumentoTiposEntity documentoTipos = documentosTiposRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DocumentoTipos no encontrada con id: " + id));
            documentosTiposRepository.delete(documentoTipos);
        } catch (Exception e) {
            log.error("Error al eliminar DocumentosTipos", e);
            throw new RuntimeException("Error al eliminar DocumentosTipos");
        }
    }

    @Override
    public void deleteById(String docCodigo, Long empresa) {
        try {
            DocumentoTiposEntity documentoTipos = documentosTiposRepository.findByEmpresaAndDocCodigo(empresa, docCodigo).orElseThrow(() -> new EntityNotFoundException("DocumentoTipos no encontrada con docCodigo: " + docCodigo + " y empresaId: " + empresa));
            documentosTiposRepository.delete(documentoTipos);
        } catch (Exception e) {
            log.error("Error al eliminar DocumentosTipos", e);
            throw new RuntimeException("Error al eliminar DocumentosTipos");
        }
    }

    @Override
    public DocumentosTiposDTO update(String docCodigo, Long empresa, DocumentosTiposDTO documentosTiposDTO) {
        try {
            DocumentoTiposEntity documentoExistente = documentosTiposRepository.findByEmpresaAndDocCodigo(empresa, docCodigo)
                    .orElseThrow(() -> new EntityNotFoundException("DocumentoTipos no encontrada con docCodigo: " + docCodigo + " y empresaId: " + empresa));
            documentoExistente.setDocCodigo(documentosTiposDTO.getDocCodigo());
            documentoExistente.setDescripcion(documentosTiposDTO.getDescripcion());
            documentoExistente.setUsuarioActualizacion(documentosTiposDTO.getUsuarioActualizacion());
        return modelMapper.map(documentosTiposRepository.save(documentoExistente), DocumentosTiposDTO.class);
        } catch (Exception e) {
            log.error("Error al actualizar DocumentosTipos", e);
            throw new RuntimeException("Error al actualizar DocumentosTipos");
        }
    }

    @Override
    public List<DocumentosTiposDTO> findByIdEmpresa(Long empresa) {
        try {
            List<DocumentoTiposEntity> documentosTipos = documentosTiposRepository.findByEmpresaId(empresa);
            return documentosTipos.stream().map(documentosTiposEntity -> modelMapper.map(documentosTiposEntity, DocumentosTiposDTO.class)).toList();
        } catch (Exception e) {
            log.error("Error al obtener DocumentosTipos por empresa", e);
            throw new RuntimeException("Error al obtener DocumentosTipos por empresa");
        }
    }
}
