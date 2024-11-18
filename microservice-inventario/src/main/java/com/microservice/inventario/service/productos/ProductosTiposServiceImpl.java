package com.microservice.inventario.service.productos;

import com.microservice.inventario.controller.DTO.ProductosTiposDTO;
import com.microservice.inventario.persistence.entity.ProductosTiposEntity;
import com.microservice.inventario.persistence.especification.ProductoTipoEspecification;
import com.microservice.inventario.persistence.repository.ProductosTiposRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductosTiposServiceImpl implements IProductosTiposService{
    private final ProductosTiposRepository productosTiposRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<ProductosTiposDTO> findAllByEmpresa(String codigo, String nombre, Pageable pageable, Long idEmpresa) {
        try{
            Specification<ProductosTiposEntity> specification = ProductoTipoEspecification.getProductosTipos(codigo, nombre);
            return productosTiposRepository.findAll(specification, pageable).map(productosTipos -> modelMapper.map(productosTipos, ProductosTiposDTO.class));
        } catch (Exception e) {
            log.error("Error al obtener la lista de ProductosTipos", e);
            throw new RuntimeException("Error al obtener la lista de ProductosTipos");
        }
    }

    @Override
    public ProductosTiposDTO save(ProductosTiposDTO productosTiposDTO) {
        try{
            ProductosTiposEntity productosTipos = modelMapper.map(productosTiposDTO, ProductosTiposEntity.class);
            productosTiposRepository.save(productosTipos);
            return modelMapper.map(productosTipos, ProductosTiposDTO.class);
        }catch (Exception e) {
            log.error("Error al guardar ProductosTipos", e);
            throw new RuntimeException("Error al guardar ProductosTipos");
        }
    }

    @Override
    public List<ProductosTiposDTO> findByIdEmpresa(Long idEmpresa) {
        try {
            List<ProductosTiposEntity> productosTipos = productosTiposRepository.findByIdEmpresa(idEmpresa);
            return productosTipos.stream().map(productosTiposEntity -> modelMapper.map(productosTiposEntity, ProductosTiposDTO.class)).toList();
        }catch (Exception e) {
            log.error("Error al obtener ProductosTipos por idEmpresa", e);
            throw new RuntimeException("Error al obtener ProductosTipos por idEmpresa");
        }
    }
}
