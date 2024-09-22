package com.microservice.empresas.service.impl;

import com.microservice.empresas.controller.dto.DocumentosTiposDTO;
import com.microservice.empresas.persistence.entity.DocumentoTiposEntity;
import com.microservice.empresas.persistence.entity.ids.DocumentosTiposId;
import com.microservice.empresas.persistence.repository.IDocumentosTiposRepository;
import com.microservice.empresas.service.IDocumentosTiposService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class DocumentosTiposServiceImpl implements IDocumentosTiposService {
    @Autowired
    private IDocumentosTiposRepository documentosTiposRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public List<DocumentoTiposEntity> findAll() {
        return (List<DocumentoTiposEntity>) documentosTiposRepository.findAll();
    }

    @Override
    public Optional<DocumentoTiposEntity> findById(String docCodigo, Long empresa) {
        DocumentosTiposId id = DocumentosTiposId.builder()
                .docCodigo(docCodigo)
                .empresa(empresa)
                .build();
        return documentosTiposRepository.findById(id)
                .or(() -> Optional.empty());
    }

    @Override
    public DocumentoTiposEntity save(DocumentosTiposDTO documentosTiposDTO) {
        DocumentoTiposEntity documentoTiposEntity = modelMapper.map(documentosTiposDTO, DocumentoTiposEntity.class);
        return documentosTiposRepository.save(documentoTiposEntity);
    }

    @Override
    public void deleteById(String docCodigo, Long empresa) {
        DocumentosTiposId id = DocumentosTiposId.builder()
                .docCodigo(docCodigo)
                .empresa(empresa)
                .build();
        documentosTiposRepository.deleteById(id);
    }

    @Override
    public DocumentoTiposEntity update(String docCodigo, Long empresa, DocumentosTiposDTO documentosTiposDTO) {
        DocumentosTiposId id = DocumentosTiposId.builder()
                .docCodigo(docCodigo)
                .empresa(empresa)
                .build();
        DocumentoTiposEntity documentoExistente = documentosTiposRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentoTipos no encontrada con docCodigo: " + docCodigo + " y empresaId: " + empresa));
        documentoExistente.setDocCodigo(documentosTiposDTO.getDocCodigo());
        documentoExistente.setDescripcion(documentosTiposDTO.getDescripcion());
        documentoExistente.setUsuarioActualizacion(documentosTiposDTO.getUsuarioActualizacion());
        return documentosTiposRepository.save(documentoExistente);
    }
}
