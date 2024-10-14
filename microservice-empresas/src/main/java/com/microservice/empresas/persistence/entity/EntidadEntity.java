package com.microservice.empresas.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "entidades")
public class EntidadEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaEntity empresa;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = ZonasEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_id")
    private ZonasEntity zona;
    private String nombre;
    @Column(name = "apellido_paterno")
    private String apellidoPaterno;
    @Column(name = "apellido_materno")
    private String apellidoMaterno;

    @ManyToOne(targetEntity = DocumentoTiposEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_codigo", referencedColumnName = "doc_codigo")
    private DocumentoTiposEntity documentoTipo;

    @Column(name = "nro_documento", length = 8, unique = true)
    private String nroDocumento;
    private String email;
    private String celular;
    private String direccion;
    private String sexo;
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "entidades_x_entidades_tipos",
            joinColumns = @JoinColumn(name = "id_entidad"),
            inverseJoinColumns = {
                    @JoinColumn(name = "empresa_id", referencedColumnName = "empresa_id"),
                    @JoinColumn(name = "tipo_codigo", referencedColumnName = "tipo_codigo")
            }
    )
    private List<EntidadesTiposEntity> entidadesTiposList;
}
