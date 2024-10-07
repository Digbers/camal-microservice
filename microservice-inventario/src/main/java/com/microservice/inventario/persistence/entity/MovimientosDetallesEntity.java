package com.microservice.inventario.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimientos_detalles")
public class MovimientosDetallesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_empresa", nullable = false)
    private Long idEmpresa;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_movimiento", nullable = false)
    private MovimientosCabeceraEntity idMovimiento;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "id_producto")
    private ProductosEntity idProducto;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_envase")
    private EnvaseEntity envase;
    @Column(precision = 10, scale = 2)
    private BigDecimal peso;
    @Column(precision = 10, scale = 2)
    private BigDecimal total;
    @Column(name = "cantidad", nullable = false) // Cantidad de productos en este detalle (ambos)
    private Integer cantidad;
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
