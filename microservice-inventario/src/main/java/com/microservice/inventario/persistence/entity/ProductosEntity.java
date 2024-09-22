package com.microservice.inventario.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productos")
public class ProductosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_empresa", nullable = false)
    private Long empresaId;
    @Column(unique = true, nullable = false, length = 50)
    private String codigo;
    @Column(nullable = false, length = 150)
    private String nombre;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "tipo_codigo", nullable = false)
    private ProductosTiposEntity tipo;
    @OneToMany(mappedBy = "productos", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<ProductosXAlmacenEntity> productosXAlmacenes = new ArrayList<>();
    private String marca;
    private String presentacion;
    @Column(name = "capacidad_pieza")
    private Integer capacidadCantidad;
    @Column(name = "generar_stock")
    private Boolean generarStock;
    private Boolean estado;
    private Double precioSugerido;
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
