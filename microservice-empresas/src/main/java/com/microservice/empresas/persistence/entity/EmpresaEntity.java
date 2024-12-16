package com.microservice.empresas.persistence.entity;

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
@Table(name = "empresas")
public class EmpresaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "razon_social", unique = true, nullable = false, length = 200)
    private String razonSocial;
    @Column(name = "empresa_codigo", unique = true, nullable = false, length = 11)
    private String empresaCodigo;
    @Column(name = "ruc", unique = true, nullable = false, length = 11)
    private String ruc;
    @Column(name = "direccion", nullable = false)
    private String direccion;
    @Column(length = 40)
    private String departamento;
    @Column(length = 40)
    private String provincia;
    @Column(length = 40)
    private String distrito;
    @Column(length = 6)
    private String ubigeo;
    @Column(length = 20)
    private String telefono;
    @Column(length = 15)
    private String celular;
    @Column(length = 100)
    private String correo;
    @Column(length = 100)
    private String web;
    private String logo;
    private Boolean estado = true;
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
