package com.microservice.inventario.service;

import com.microservice.inventario.controller.DTO.ProductoDTO;
import com.microservice.inventario.controller.DTO.ProductosXAlmacenDTO;
import com.microservice.inventario.persistence.entity.AlmacenEntity;
import com.microservice.inventario.persistence.repository.AlmacenRepository;
import com.microservice.inventario.service.productos.ProductosServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductNotificationScheduler {

    @Autowired
    private ProductosServiceImpl productosService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AlmacenRepository almacenRepository;


    @Scheduled(cron = "0 0/30 * * * ?")
    //@Scheduled(cron = "0 * * * * ?")
    public void checkStockMinimo() {

        List<ProductoDTO> productosConStockMinimo = productosService.findStockMinimo();

        for (ProductoDTO producto : productosConStockMinimo) {
            // Iterar sobre la lista de productos por almacén
            for (ProductosXAlmacenDTO productosXAlmacen : producto.getProductosXAlmacen()) {
                if (productosXAlmacen.getCantidad() < 10) {

                    Optional<AlmacenEntity> almacenOpt = almacenRepository.findById(productosXAlmacen.getAlmacen());
                    String nombreAlmacen = almacenOpt.isPresent() ? almacenOpt.get().getNombre() : "Desconocido";

                    String messageBody = "El producto " + producto.getNombre() +
                            " tiene solo " + productosXAlmacen.getCantidad() +
                            " unidades en stock en el almacén: " + nombreAlmacen + ".";

                    // Enviar notificación a todos los administradores (topic "admin")
                    notificationService.sendNotificationToTopic("admin", "Alerta de Stock", messageBody);
                }
            }
        }
    }
}

