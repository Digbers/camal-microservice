package com.microservice.inventario.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "puntos_venta")
public class PuntoVentaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_empresa", nullable = false)
    private Long idEmpresa;
    private String direccion;
    private String nombre;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "ubigeo_id")
    private UbigeoEntity ubigeo;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "almacen_id", referencedColumnName = "id")
    private AlmacenEntity almacen;
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
