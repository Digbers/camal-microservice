package com.microservice.inventario.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "StockAlmacen", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"idEmpresa","idAlmacen", "idProducto", "idEnvase"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
    @Builder
    public class StockAlmacen {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_stock")
        private Long idStock;
        @Column(name = "id_empresa", nullable = false)
        private Long idEmpresa;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, optional = false)
        @JoinColumn(name = "idProducto")
        private ProductosEntity producto;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
        @JoinColumn(name = "idEnvase")
        private EnvaseEntity envase;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
        @JoinColumn(name = "idAlmacen")
        private AlmacenEntity almacen;
        @Column(name = "cantidad_envase")
        private int cantidadEnvase;  // Cantidad de javas u otros envases
        @Column(name = "cantidad_producto")
        private int cantidadProducto; // Cantidad de productos (pollos)
        @Column(name = "peso_total", precision = 10, scale = 2)
        private BigDecimal pesoTotal;  // Peso total de productos
        @Column(name = "fecha_registro")
        private LocalDate fechaRegistro;
        @Column(name = "usercodigo_creacion")
        private String usuarioCreacion;
        @CreationTimestamp
        @Column(name = "fecha_creacion", updatable = false)
        private Timestamp fechaCreacion;
        @Column(name = "usercodigo_actualizacion")
        private String usuarioActualizacion;
        @UpdateTimestamp
        @Column(name = "fecha_actualizacion")
        private Timestamp fechaActualizacion;
    }
