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
@Table(name = "ubigeos")
public class UbigeoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column( name = "ubigeo_codigo", unique = true, nullable = false, updatable = false)
    private String ubigeoCodigo;
    private String nombre;
    private String departamento;
    private String provincia;
    private String distrito;
    @Column(name = "departamento_completo")
    private String departamentoCompleto;
    @Column(name = "provincia_completo")
    private String provinciaCompleto;
    @Column(name = "distrito_completo")
    private String distritoCompleto;
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
