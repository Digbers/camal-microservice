package com.microservice.inventario.config;

import com.microservice.inventario.controller.DTO.StockAlmacenDTO;
import com.microservice.inventario.persistence.entity.ProductosXAlmacenEntity;
import com.microservice.inventario.persistence.entity.StockAlmacen;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Configurar mapeos entre StockAlmacen y StockAlmacenDTO
        modelMapper.typeMap(StockAlmacen.class, StockAlmacenDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getProducto().getIdProducto(), StockAlmacenDTO::setIdProducto);
            mapper.map(src -> src.getEnvase().getIdEnvase(), StockAlmacenDTO::setIdEnvase);
            mapper.map(src -> src.getAlmacen().getId(), StockAlmacenDTO::setIdAlmacen);
        });
        return modelMapper;
    }
}
