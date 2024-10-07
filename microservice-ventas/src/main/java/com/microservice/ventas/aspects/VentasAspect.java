package com.microservice.ventas.aspects;

import com.microservice.ventas.client.InventarioClient;
import com.microservice.ventas.controller.DTO.ventas.ComprobantesVentasCabDTO;
import com.microservice.ventas.exception.ProductoNotFoundException;
import com.microservice.ventas.repository.IcomprobantesVentasCabRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Configuration
@EnableAspectJAutoProxy
@Aspect
@Component
public class VentasAspect {
    @Autowired
    private IcomprobantesVentasCabRepository iComprobantesVentasCabRepository;
    @Autowired
    private InventarioClient inventarioClient;

    @Around("execution(* com.microservice.ventas.service.ventas.VentasServiceImpl.save(..)) && args(comprobantesVentasCabDTO)")
    public Object verificarYAsignarNumero(ProceedingJoinPoint joinPoint, ComprobantesVentasCabDTO comprobantesVentasCabDTO) throws Throwable {
        String maxNumero = iComprobantesVentasCabRepository.findMaxNumero(comprobantesVentasCabDTO.getSerie(), comprobantesVentasCabDTO.getIdPuntoVenta());
        // Si hay un número máximo en la base de datos, calcular el siguiente número
        if (maxNumero != null) {
            int nuevoNumero = Integer.parseInt(maxNumero) + 1;

            if (!comprobantesVentasCabDTO.getNumero().equals(String.valueOf(nuevoNumero))) {
                comprobantesVentasCabDTO.setNumero(String.valueOf(nuevoNumero));
                System.out.println("Número actualizado automáticamente a: " + nuevoNumero);
            }
        } else {
            comprobantesVentasCabDTO.setNumero("1");
            System.out.println("Número inicial asignado: 1");
        }

        return joinPoint.proceed();
    }
    @Before("execution(* com.microservice.ventas.service.ventas.VentasServiceImpl.save(..)) && args(comprobantesVentasCabDTO)")
    public void verificarStock(ComprobantesVentasCabDTO comprobantesVentasCabDTO) {
        comprobantesVentasCabDTO.getComprobantesVentaDet().forEach(detalle -> {
            Long productoId = detalle.getIdProducto();
            Integer cantidadRequerida = detalle.getCantidad();

            // Llamar al cliente de inventario para verificar el stock disponible
            Integer stockDisponible;
            try {
                stockDisponible = inventarioClient.obtenerStockDisponible(productoId);
            } catch (ProductoNotFoundException ex) {
                throw new RuntimeException("El producto con ID " + productoId + " no existe en el inventario.");
            }

            // Verificar si el stock disponible es menor que la cantidad requerida
            if (stockDisponible == null || stockDisponible < cantidadRequerida) {
                throw new RuntimeException("Stock insuficiente para el producto ID: " + productoId
                        + ". Solo hay " + stockDisponible + " unidades disponibles.");
            }

            System.out.println("Stock verificado: " + stockDisponible + " unidades disponibles.");
        });
    }
    /*@Before("execution(* com.microservice.ventas.service.ventas.VentasServiceImpl.save(..)) && args(comprobantesVentasCabDTO)")
    public void verificarCliente(ComprobantesVentasCabDTO comprobantesVentasCabDTO) {
        // Llamar al cliente de inventario para verificar el stock disponible
    }*/

}
