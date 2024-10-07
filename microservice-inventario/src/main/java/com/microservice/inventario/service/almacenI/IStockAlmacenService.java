package com.microservice.inventario.service.almacenI;

import com.microservice.inventario.controller.DTO.request.ConvertirProductoRequest;
import com.microservice.inventario.persistence.entity.StockAlmacen;

public interface IStockAlmacenService {
    StockAlmacen registrarProductoEnAlmacen(Long idEmpresa, Long idAlmacen, Long idProducto, Long idEnvase,
                                            int cantidadEnvase, int cantidadProducto, double pesoTotal);
    Boolean convertirProducto(ConvertirProductoRequest convertirProductoRequest);

}
