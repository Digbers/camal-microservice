package com.microservice.inventario.config;

import com.microservice.inventario.controller.DTO.PuntoVentaDTO;
import com.microservice.inventario.controller.DTO.StockAlmacenDTO;
import com.microservice.inventario.persistence.entity.PuntoVentaEntity;
import com.microservice.inventario.persistence.entity.StockAlmacen;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Mapeo personalizado para PuntoVentaEntity a PuntoVentaDTO
        modelMapper.typeMap(PuntoVentaEntity.class, PuntoVentaDTO.class).addMappings(mapper -> {
            mapper.map(PuntoVentaEntity::getAlmacen, PuntoVentaDTO::setAlmacen);
            mapper.map(PuntoVentaEntity::getUbigeo, PuntoVentaDTO::setUbigeo);
        });
        // Desactivar la coincidencia implícita para evitar ambigüedades.
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // Configurar mapeos explícitos entre StockAlmacen y StockAlmacenDTO
        modelMapper.typeMap(StockAlmacen.class, StockAlmacenDTO.class).addMappings(mapper -> {
            // Mapear idProducto explícitamente
            mapper.map(src -> src.getProducto().getIdProducto(), StockAlmacenDTO::setIdProducto);
            // Mapear idEnvase explícitamente
            mapper.map(src -> src.getEnvase().getIdEnvase(), StockAlmacenDTO::setIdEnvase);
            // Mapear idAlmacen explícitamente
            mapper.map(src -> src.getAlmacen().getId(), StockAlmacenDTO::setIdAlmacen);
            // Mapear los otros atributos directamente
            mapper.map(StockAlmacen::getCantidadEnvase, StockAlmacenDTO::setCantidadEnvase);
            mapper.map(StockAlmacen::getCantidadProducto, StockAlmacenDTO::setCantidadProducto);
            mapper.map(StockAlmacen::getPesoTotal, StockAlmacenDTO::setPesoTotal);
            mapper.map(StockAlmacen::getFechaRegistro, StockAlmacenDTO::setFechaRegistro);
            mapper.map(StockAlmacen::getUsuarioCreacion, StockAlmacenDTO::setUsuarioCreacion);
            mapper.map(StockAlmacen::getFechaCreacion, StockAlmacenDTO::setFechaCreacion);
        });


        return modelMapper;
    }
}
