package com.microservice.inventario.aspects;

import com.microservice.inventario.controller.DTO.request.ConvertirProductoRequest;
import com.microservice.inventario.persistence.entity.AlmacenEntity;
import com.microservice.inventario.persistence.entity.EnvaseEntity;
import com.microservice.inventario.persistence.entity.ProductosEntity;
import com.microservice.inventario.persistence.entity.StockAlmacen;
import com.microservice.inventario.persistence.repository.IStockAlmacenRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Aspect
@RequiredArgsConstructor
public class ProductoAspect {

    private final IStockAlmacenRepository stockAlmacenRepository;
    /**
     * Aspecto para verificar el stock antes de realizar la conversión de productos.
     */
    @Around("execution(* com.microservice.inventario.service.almacenI.StockAlmacenService.convertirProducto(..)) && args(request)")
    public Object verificarStock(ProceedingJoinPoint joinPoint, ConvertirProductoRequest request) throws Throwable {
        // 1. Verificar la existencia del producto en StockAlmacen
        Optional<StockAlmacen> stockOpt = stockAlmacenRepository.findByIds(request.idAlmacen(), request.idProducto(), request.idEnvase(), request.idEmpresa());
        if (stockOpt.isEmpty()) {
            throw new RuntimeException("Producto no encontrado en el almacén. Operación abortada.");
        }
        StockAlmacen stock = stockOpt.get();
        // 2. Verificar la cantidad disponible
        if (stock.getCantidadProducto() < request.cantidadProducto()) {
            throw new RuntimeException("Cantidad insuficiente para la conversión. Operación abortada.");
        }
        try {
            Object result = joinPoint.proceed(); // retorna el resultado del método
            return result;
        } catch (Exception ex) {
            throw new RuntimeException("Error durante la conversión de productos. Transacción revertida.", ex);
        }
    }
}
