package com.microservice.inventario.service.unidades;

import com.microservice.inventario.controller.DTO.UnidadesDTO;
import com.microservice.inventario.persistence.entity.UnidadesEntity;
import com.microservice.inventario.persistence.especification.UnidadesEspecification;
import com.microservice.inventario.persistence.repository.IUnidadesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class UnidadesService implements IUnidadesService {
    private final IUnidadesRepository unidadesRepository;
    private  final ModelMapper mapper;
    @Override
    public List<UnidadesDTO> findByIdEmpresa(Long idEmpresa) {
        try{
            List<UnidadesEntity> unidadesList = unidadesRepository.findByIdEmpresa(idEmpresa);
            return unidadesList.stream().map( unidadesEntity -> mapper.map(unidadesEntity, UnidadesDTO.class)).toList();

        }catch (Exception e){
            log.error("Error al obtener unidades por id empresa");
            throw new RuntimeException(e);
        }
    }

    @Override
    public UnidadesDTO save(UnidadesDTO unidadesDTO) {
        try{
            UnidadesEntity unidadesEntity = mapper.map(unidadesDTO, UnidadesEntity.class);
            return mapper.map(unidadesRepository.save(unidadesEntity), UnidadesDTO.class);
        }catch (Exception e){
            log.error("Error al guardar unidades");
            throw new RuntimeException(e);
        }
    }

    @Override
    public UnidadesDTO update(Long id, UnidadesDTO unidadesDTO) {
        try{
            UnidadesEntity unidadesEntity = unidadesRepository.findById(id).orElseThrow(() -> new RuntimeException("Unidades no encontrada con id: " + id));
            unidadesEntity.setCodigo(unidadesDTO.getCodigo());
            unidadesEntity.setNombre(unidadesDTO.getNombre());
            unidadesEntity.setIdEmpresa(unidadesDTO.getIdEmpresa());
            unidadesEntity.setUsuarioActualizacion(unidadesDTO.getUsuarioActualizacion());
            return mapper.map(unidadesRepository.save(unidadesEntity), UnidadesDTO.class);
        }catch (Exception e){
            log.error("Error al actualizar unidades");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try{
            UnidadesEntity unidadesEntity = unidadesRepository.findById(id).orElseThrow(() -> new RuntimeException("Unidades no encontrada con id: " + id));
            unidadesRepository.delete(unidadesEntity);
        }catch (Exception e){
            log.error("Error al eliminar unidades");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<UnidadesDTO> findAllByEmpresa(String codigo, String nombre, Pageable pageable, Long idEmpresa) {
        try{
            Specification<UnidadesEntity> specification = UnidadesEspecification.getUnidades(codigo, nombre, idEmpresa);
            return unidadesRepository.findAll(specification, pageable).map(unidadesEntity -> mapper.map(unidadesEntity, UnidadesDTO.class));
        } catch (Exception e) {
            log.error("Error al obtener las unidades: " + e.getMessage());
            throw new RuntimeException("Error al obtener las unidades: " + e.getMessage());
        }
    }
}
