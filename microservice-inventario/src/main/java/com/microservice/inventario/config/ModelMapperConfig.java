package com.microservice.inventario.config;

import com.microservice.inventario.controller.DTO.ProductosXAlmacenDTO;
import com.microservice.inventario.persistence.entity.ProductosXAlmacenEntity;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configurar mapeos personalizados
        modelMapper.typeMap(ProductosXAlmacenEntity.class, ProductosXAlmacenDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getAlmacen().getId(), ProductosXAlmacenDTO::setAlmacen);
            mapper.map(src -> src.getProductos().getId(), ProductosXAlmacenDTO::setProductoId);
        });

        return modelMapper;
    }
}
